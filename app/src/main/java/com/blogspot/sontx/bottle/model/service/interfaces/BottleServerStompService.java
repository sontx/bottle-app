package com.blogspot.sontx.bottle.model.service.interfaces;

import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.SimpleCallback;

public interface BottleServerStompService extends ServiceBase {
    void reconnect(Callback<Void> callback);

    void disconnect();

    void subscribe(String topic, SimpleCallback<String> callback);

    void unsubscribe(String topic);
}
