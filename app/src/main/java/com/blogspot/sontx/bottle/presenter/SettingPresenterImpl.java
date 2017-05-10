package com.blogspot.sontx.bottle.presenter;

import android.util.Log;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.model.bean.GeoMessage;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.UserSetting;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerGeoService;
import com.blogspot.sontx.bottle.presenter.interfaces.SettingPresenter;
import com.blogspot.sontx.bottle.system.Resource;
import com.blogspot.sontx.bottle.view.interfaces.SettingView;

public class SettingPresenterImpl extends PresenterBase implements SettingPresenter {
    private final SettingView settingView;
    private final BottleServerGeoService bottleServerGeoService;

    public SettingPresenterImpl(SettingView settingView) {
        this.settingView = settingView;
        this.bottleServerGeoService = FirebaseServicePool.getInstance().getBottleServerGeoService();
    }

    @Override
    public void updateUI() {
        PublicProfile publicProfile = App.getInstance().getBottleContext().getCurrentPublicProfile();
        Resource resource = App.getInstance().getBottleContext().getResource();

        settingView.showDisplayName(publicProfile.getDisplayName());
        settingView.showAvatar(resource.absoluteUrl(publicProfile.getAvatarUrl()));

        UserSetting currentUserSetting = App.getInstance().getBottleContext().getCurrentUserSetting();
        int messageId = currentUserSetting.getMessageId();
        if (messageId >= 0) {
            bottleServerGeoService.getGeoMessageAsync(messageId, new Callback<GeoMessage>() {
                @Override
                public void onSuccess(GeoMessage result) {
                    settingView.showStatus(result.getText());
                }

                @Override
                public void onError(Throwable what) {
                    Log.e(TAG, "updateUI(): ", what);
                    settingView.showStatus(null);
                }
            });
        } else {
            settingView.showStatus(null);
        }
    }
}
