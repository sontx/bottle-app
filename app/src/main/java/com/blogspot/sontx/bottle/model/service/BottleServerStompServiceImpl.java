package com.blogspot.sontx.bottle.model.service;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.Constants;
import com.blogspot.sontx.bottle.model.bean.BottleUser;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerStompService;
import com.blogspot.sontx.bottle.system.receiver.NetworkStateReceiver;
import com.blogspot.sontx.bottle.utils.ThreadUtils;

import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Setter;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import ua.naiksoftware.stomp.LifecycleEvent;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompHeader;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

class BottleServerStompServiceImpl extends BottleServerServiceBase implements BottleServerStompService, NetworkStateReceiver.NetworkStateReceiverListener {
    private final StompClient client;
    private boolean connected = false;
    private boolean requiredDisconnect;
    private LifecycleHandler lifecycleHandler;
    private NetworkStateReceiver networkStateReceiver;
    private HashMap<String, SimpleCallback<String>> topics = new HashMap<>();

    @Override
    public void clearCached() {
        disconnect();
    }

    BottleServerStompServiceImpl() {
        String endpointUrl = System.getProperty(Constants.BOTTLE_SERVER_STOMP_ENDPOINT_KEY);
        client = Stomp.over(WebSocket.class, endpointUrl);
    }

    @Override
    public void reconnect(@Nullable Callback<Void> callback) {
        requiredDisconnect = false;

        if (!App.getInstance().getBottleContext().isLogged()) {
            if (callback != null)
                callback.onError(new Exception("Unauthenticated"));
            return;
        }

        BottleUser bottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();

        StompHeader header = new StompHeader("Authorization", "Bearer " + bottleUser.getToken());
        final List<StompHeader> headers = new ArrayList<>(1);
        headers.add(header);

        if (client.isConnected() && callback != null)
            callback.onSuccess(null);

        if (lifecycleHandler == null)
            client.lifecycle().subscribe(lifecycleHandler = new LifecycleHandler());
        lifecycleHandler.setOneshotCallback(callback);

        try {
            client.connect(headers, true);
        } catch (IllegalStateException ignored) {
            if (callback != null)
                callback.onSuccess(null);
        }
        connected = true;
    }

    @Override
    public void disconnect() {
        requiredDisconnect = true;
        synchronized (this) {
            for (String topic : topics.keySet()) {
                client.topic(topic).unsubscribeOn(Schedulers.immediate());
            }
            topics.clear();
        }
        client.disconnect();
        connected = false;
    }

    @Override
    public void subscribe(final String topic, final SimpleCallback<String> callback) {
        if (client.isConnected()) {
            doSubscribe(topic, callback);
        } else {
            reconnect(new Callback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    doSubscribe(topic, callback);
                }

                @Override
                public void onError(Throwable what) {
                    Log.e(TAG, "subscribe: ", what);
                }
            });
        }
    }

    private void doSubscribe(String topic, final SimpleCallback<String> callback) {
        synchronized (this) {
            topics.put(topic, callback);
        }
        client.topic(topic).subscribe(new Action1<StompMessage>() {
            @Override
            public void call(StompMessage stompMessage) {
                callback.onCallback(stompMessage.getPayload());
            }
        });
    }

    @Override
    public void unsubscribe(String topic) {
        client.topic(topic).unsubscribeOn(Schedulers.newThread());
        synchronized (this) {
            topics.remove(topic);
        }
    }

    private void lostConnect() {
        if (requiredDisconnect)
            return;

        final ConnectivityManager conMgr = (ConnectivityManager) App.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            tryToConnect();
        } else {
            // notify user you are not online
            if (networkStateReceiver == null) {
                networkStateReceiver = new NetworkStateReceiver();
                networkStateReceiver.addListener(this);
                App.getInstance().registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
            }
        }
    }

    private void tryToConnect() {
        ThreadUtils.run(new Runnable() {
            @Override
            public void run() {
                reconnect(null);
            }
        });
    }

    @Override
    public void networkAvailable() {
        tryToConnect();
    }

    @Override
    public void networkUnavailable() {

    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });

    private class LifecycleHandler implements Action1<LifecycleEvent> {
        @Setter
        private Callback<Void> oneshotCallback;

        @Override
        public void call(LifecycleEvent lifecycleEvent) {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    if (oneshotCallback != null)
                        oneshotCallback.onSuccess(null);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (BottleServerStompServiceImpl.this) {
                                for (String topic : topics.keySet()) {
                                    subscribe(topic, topics.get(topic));
                                }
                            }
                        }
                    }, 500);
                    break;

                case CLOSED:
                    if (oneshotCallback != null)
                        oneshotCallback.onError(lifecycleEvent.getException());
                    lostConnect();
                    break;

                case ERROR:
                    if (oneshotCallback != null)
                        oneshotCallback.onError(lifecycleEvent.getException());
                    lostConnect();
                    break;
            }
            oneshotCallback = null;
        }
    }
}
