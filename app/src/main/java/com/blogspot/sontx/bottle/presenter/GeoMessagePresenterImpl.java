package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.model.bean.Coordination;
import com.blogspot.sontx.bottle.model.bean.GeoMessage;
import com.blogspot.sontx.bottle.model.bean.MessageBase;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.UploadResult;
import com.blogspot.sontx.bottle.model.bean.UserSetting;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleFileStreamService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerGeoService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerMessageService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerUserSettingService;
import com.blogspot.sontx.bottle.presenter.interfaces.GeoMessagePresenter;
import com.blogspot.sontx.bottle.utils.StringUtils;
import com.blogspot.sontx.bottle.view.interfaces.MapMessageView;

import java.util.List;

public class GeoMessagePresenterImpl implements GeoMessagePresenter {
    private final MapMessageView mapMessageView;
    private final BottleServerGeoService bottleServerGeoService;
    private final BottleServerMessageService bottleServerMessageService;
    private final BottleFileStreamService bottleFileStreamService;
    private final BottleServerUserSettingService bottleServerUserSettingService;
    private final PublicProfile currentPublicProfile;

    public GeoMessagePresenterImpl(MapMessageView mapMessageView) {
        this.mapMessageView = mapMessageView;
        bottleServerMessageService = FirebaseServicePool.getInstance().getBottleServerMessageService();
        bottleFileStreamService = FirebaseServicePool.getInstance().getBottleFileStreamService();
        bottleServerGeoService = FirebaseServicePool.getInstance().getBottleServerGeoService();
        bottleServerUserSettingService = FirebaseServicePool.getInstance().getBottleServerUserSettingService();
        currentPublicProfile = App.getInstance().getBottleContext().getCurrentPublicProfile();
    }

    @Override
    public void getMapMessagesAroundMyLocationAsync(double latitudeRadius, double longitudeRadius) {
        UserSetting currentUserSetting = App.getInstance().getBottleContext().getCurrentUserSetting();
        Coordination currentLocation = currentUserSetting.getCurrentLocation();
        bottleServerMessageService.getMapMessagesAroundLocationAsync(
                currentLocation.getLatitude(),
                currentLocation.getLongitude(),
                latitudeRadius,
                longitudeRadius,
                new Callback<List<GeoMessage>>() {

                    @Override
                    public void onSuccess(List<GeoMessage> result) {
                        mapMessageView.showMapMessages(result);
                    }

                    @Override
                    public void onError(Throwable what) {
                        mapMessageView.showErrorMessage(what);
                    }
                });
    }

    @Override
    public void postGeoMessageAsync(String text, Coordination coordination, String mediaPath, String type, int emotion) {
        final GeoMessage tempGeoMessage = new GeoMessage();
        tempGeoMessage.setId(MessageBase.UNDEFINED_ID);
        tempGeoMessage.setText(text);
        tempGeoMessage.setOwner(currentPublicProfile);
        tempGeoMessage.setMediaUrl(mediaPath);
        tempGeoMessage.setType(type);
        tempGeoMessage.setEmotion(emotion);

        tempGeoMessage.setLatitude(coordination.getLatitude());
        tempGeoMessage.setLongitude(coordination.getLongitude());

        if (currentPublicProfile != null)
            mapMessageView.addGeoMessage(tempGeoMessage);

        if (StringUtils.isEmpty(mediaPath)) {
            postGeoMessageAsync(tempGeoMessage);
        } else {
            // link, we don't need to upload them
            if (type.equalsIgnoreCase(MessageBase.LINK)) {
                postGeoMessageAsync(tempGeoMessage);
            } else {
                bottleFileStreamService.uploadAsync(mediaPath, new Callback<UploadResult>() {
                    @Override
                    public void onSuccess(UploadResult result) {
                        tempGeoMessage.setMediaUrl(result.getName());
                        postGeoMessageAsync(tempGeoMessage);
                    }

                    @Override
                    public void onError(Throwable what) {
                        mapMessageView.showErrorMessage(what);
                    }
                });
            }
        }
    }

    @Override
    public void editGeoMessageAsync(GeoMessage geoMessage) {
        bottleServerGeoService.editGeoMessageAsync(geoMessage, new Callback<GeoMessage>() {
            @Override
            public void onSuccess(GeoMessage result) {
                // nothing
            }

            @Override
            public void onError(Throwable what) {
                mapMessageView.showErrorMessage(what);
            }
        });
    }

    @Override
    public void updateCurrentUserLocationAsync(Coordination currentLocation) {
        UserSetting currentUserSetting = App.getInstance().getBottleContext().getCurrentUserSetting();
        currentUserSetting.setCurrentLocation(currentLocation);

        bottleServerUserSettingService.updateUserSettingAsync(currentUserSetting, new Callback<UserSetting>() {
            @Override
            public void onSuccess(UserSetting result) {
                // do nothing
            }

            @Override
            public void onError(Throwable what) {
                mapMessageView.showErrorMessage(what);
            }
        });
    }

    private void postGeoMessageAsync(final GeoMessage tempGeoMessage) {
        bottleServerGeoService.postGeoMessageAsync(tempGeoMessage, new Callback<GeoMessage>() {
            @Override
            public void onSuccess(GeoMessage result) {
                mapMessageView.updateGeoMessage(result, tempGeoMessage);
            }

            @Override
            public void onError(Throwable what) {
                mapMessageView.showErrorMessage(what);
            }
        });
    }
}
