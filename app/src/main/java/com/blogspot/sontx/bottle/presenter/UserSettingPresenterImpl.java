package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.model.bean.UserSetting;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerUserSettingService;
import com.blogspot.sontx.bottle.presenter.interfaces.UserSettingPresenter;
import com.blogspot.sontx.bottle.view.interfaces.UserSettingView;

public class UserSettingPresenterImpl implements UserSettingPresenter {
    private final UserSettingView userSettingView;
    private final BottleServerUserSettingService bottleServerUserSettingService;

    public UserSettingPresenterImpl(UserSettingView userSettingView) {
        this.userSettingView = userSettingView;
        bottleServerUserSettingService = FirebaseServicePool.getInstance().getBottleServerUserSettingService();
    }

    @Override
    public void getUserSettingAsync() {
        bottleServerUserSettingService.getUserSettingAsync(new Callback<UserSetting>() {
            @Override
            public void onSuccess(UserSetting result) {
                userSettingView.onHasUserSetting(result);
            }

            @Override
            public void onError(Throwable what) {
                userSettingView.showErrorMessage(what);
            }
        });
    }
}
