package com.blogspot.sontx.bottle.model.service;

public interface Callback<T> {
    void onSuccess(T result);

    void onError(Throwable what);
}
