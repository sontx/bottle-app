package com.blogspot.sontx.bottle.system.provider;

import android.content.Intent;

import com.blogspot.sontx.bottle.model.bean.LoginData;
import com.blogspot.sontx.bottle.model.service.SimpleCallback;

public interface Auth2Provider {
    void setOnAuthCompleted(SimpleCallback<LoginData> onAuthCompleted);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void logout();
}
