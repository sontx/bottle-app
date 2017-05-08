package com.blogspot.sontx.bottle.model.service.interfaces;

import com.blogspot.sontx.bottle.model.bean.UserSetting;
import com.blogspot.sontx.bottle.model.service.Callback;

public interface BottleServerUserSettingService extends ServiceBase {
    void getUserSettingAsync(Callback<UserSetting> callback);

    UserSetting getCurrentUserSetting();

    void updateUserSettingAsync(UserSetting userSetting, Callback<UserSetting> callback);
}
