package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.model.bean.Coordination;
import com.blogspot.sontx.bottle.model.bean.GeoMessage;
import com.blogspot.sontx.bottle.model.bean.UserSetting;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerMessageService;
import com.blogspot.sontx.bottle.presenter.interfaces.MapMessagePresenter;
import com.blogspot.sontx.bottle.view.interfaces.MapMessageView;

import java.util.List;

public class MapMessagePresenterImpl implements MapMessagePresenter {
    private final MapMessageView mapMessageView;
    private final BottleServerMessageService bottleServerMessageService;

    public MapMessagePresenterImpl(MapMessageView mapMessageView) {
        this.mapMessageView = mapMessageView;
        bottleServerMessageService = FirebaseServicePool.getInstance().getBottleServerMessageService();
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
                        mapMessageView.showErrorMessage(what.getMessage());
                    }
                });
    }
}
