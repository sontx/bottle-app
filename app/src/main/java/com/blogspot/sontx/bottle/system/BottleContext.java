package com.blogspot.sontx.bottle.system;

import android.content.Context;

import com.blogspot.sontx.bottle.model.bean.BottleUser;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.UserSetting;
import com.blogspot.sontx.bottle.system.provider.Auth2Provider;

import lombok.Getter;

public abstract class BottleContext {

    @Getter
    protected boolean isLogged = false;

    @Getter
    protected BottleUser currentBottleUser;

    @Getter
    protected UserSetting currentUserSetting;

    @Getter
    protected PublicProfile currentPublicProfile;

    @Getter
    protected Auth2Provider currentAuth2Provider;

    @Getter
    protected Resource resource;

    @Getter
    protected Context appContext;

    public abstract void clear();
}
