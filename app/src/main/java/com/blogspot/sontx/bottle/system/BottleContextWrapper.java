package com.blogspot.sontx.bottle.system;

import android.content.Context;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.Constants;
import com.blogspot.sontx.bottle.model.bean.BottleUser;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.UserSetting;
import com.blogspot.sontx.bottle.system.provider.Auth2Provider;

import lombok.Getter;

public final class BottleContextWrapper {

    @Getter
    private final BottleContext bottleContext;

    public BottleContextWrapper() {
        bottleContext = new BottleContextImpl();
    }

    public void setCurrentBottleUser(BottleUser currentBottleUser) {
        bottleContext.currentBottleUser = currentBottleUser;
    }

    public void setCurrentUserSetting(UserSetting currentUserSetting) {
        bottleContext.currentUserSetting = currentUserSetting;
    }

    public void setCurrentAuth2Provider(Auth2Provider currentAuth2Provider) {
        bottleContext.currentAuth2Provider = currentAuth2Provider;
    }

    public void setCurrentPublicProfile(PublicProfile publicProfile) {
        bottleContext.currentPublicProfile = publicProfile;
    }

    private static class BottleContextImpl extends BottleContext {
        @Override
        public boolean isLogged() {
            return currentBottleUser != null && currentBottleUser.getToken() != null;
        }

        @Override
        public Context getAppContext() {
            return App.getInstance().getApplicationContext();
        }

        BottleContextImpl() {
            resource = new Resource(System.getProperty(Constants.BOTTLE_FS_STORAGE_URL_KEY));
        }
    }
}
