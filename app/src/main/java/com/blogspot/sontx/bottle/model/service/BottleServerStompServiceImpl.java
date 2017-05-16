package com.blogspot.sontx.bottle.model.service;

import android.util.Log;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.Constants;
import com.blogspot.sontx.bottle.model.bean.BottleUser;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerStompService;

import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import rx.schedulers.Schedulers;
import ua.naiksoftware.stomp.LifecycleEvent;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompHeader;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

class BottleServerStompServiceImpl extends BottleServerServiceBase implements BottleServerStompService {
    private final StompClient client;
    private boolean connected = false;

    public BottleServerStompServiceImpl() {
        String endpointUrl = System.getProperty(Constants.BOTTLE_SERVER_STOMP_ENDPOINT_KEY);
        client = Stomp.over(WebSocket.class, endpointUrl);
    }

    @Override
    public void reconnect(final Callback<Void> callback) {
        if (connected)
            return;

        if (!App.getInstance().getBottleContext().isLogged()) {
            callback.onError(new Exception("Unauthenticated"));
            return;
        }

        BottleUser bottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();

        StompHeader header = new StompHeader("Authorization", "Bearer " + bottleUser.getToken());
        final List<StompHeader> headers = new ArrayList<>(1);
        headers.add(header);

        if (client.isConnected())
            callback.onSuccess(null);

        client.lifecycle().subscribe(new Action1<LifecycleEvent>() {
            @Override
            public void call(LifecycleEvent lifecycleEvent) {
                switch (lifecycleEvent.getType()) {
                    case OPENED:
                        callback.onSuccess(null);
                        break;

                    case CLOSED:
                        callback.onError(lifecycleEvent.getException());
                        break;

                    case ERROR:
                        callback.onError(lifecycleEvent.getException());
                        break;
                }
                client.lifecycle().unsubscribeOn(Schedulers.newThread());
            }
        });
        try {
            client.connect(headers, true);
        } catch (IllegalStateException ignored) {
            callback.onSuccess(null);
        }
        connected = true;
    }

    @Override
    public void disconnect() {
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
    }
}
