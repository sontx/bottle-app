package com.blogspot.sontx.bottle.system.provider;

import lombok.Getter;

public abstract class Auth2ProviderBase implements Auth2Provider {
    protected final static String TAG = "provider";
    @Getter
    protected static Auth2Provider currentProvider = null;
}
