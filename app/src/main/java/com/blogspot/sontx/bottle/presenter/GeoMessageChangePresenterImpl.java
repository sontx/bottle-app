package com.blogspot.sontx.bottle.presenter;

import android.util.Log;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.model.bean.GeoMessage;
import com.blogspot.sontx.bottle.model.bean.event.GeoMessageChanged;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.SimpleCallback;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerGeoService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerStompService;
import com.blogspot.sontx.bottle.presenter.interfaces.GeoMessageChangePresenter;
import com.blogspot.sontx.bottle.view.interfaces.GeoMessageChangeView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;

public class GeoMessageChangePresenterImpl extends PresenterBase implements GeoMessageChangePresenter, SimpleCallback<String> {
    private static final String TOPIC = "/geo";

    private final BottleServerStompService bottleServerStompService;
    private final BottleServerGeoService bottleServerGeoService;
    private final GeoMessageChangeView geoMessageChangeView;
    private final ObjectMapper objectMapper;

    public GeoMessageChangePresenterImpl(GeoMessageChangeView geoMessageChangeView) {
        this.geoMessageChangeView = geoMessageChangeView;
        bottleServerStompService = FirebaseServicePool.getInstance().getBottleServerStompService();
        bottleServerGeoService = FirebaseServicePool.getInstance().getBottleServerGeoService();
        objectMapper = new ObjectMapper();
    }

    @Override
    public void subscribe() {
        bottleServerStompService.subscribe(TOPIC, this);
    }

    @Override
    public void unsubscribe() {
        bottleServerStompService.unsubscribe(TOPIC);
    }

    @Override
    public void onCallback(String value) {
        if (value != null && !"".equals(value)) {
            try {
                GeoMessageChanged geoMessageChanged = objectMapper.readValue(value, GeoMessageChanged.class);
                if (isNotMine(geoMessageChanged.getId()) && !checkInbound(geoMessageChanged))
                    return;
                acceptGeoMessageChangeAsync(geoMessageChanged);
            } catch (IOException e) {
                geoMessageChangeView.showErrorMessage(e);
                Log.e(TAG, "onCallback", e);
            }
        }
    }

    private void acceptGeoMessageChangeAsync(final GeoMessageChanged geoMessageChanged) {
        if (!"delete".equals(geoMessageChanged.getState())) {
            bottleServerGeoService.getGeoMessageAsync(geoMessageChanged.getId(), new Callback<GeoMessage>() {
                @Override
                public void onSuccess(GeoMessage result) {
                    String currentUserId = App.getInstance().getBottleContext().getCurrentBottleUser().getUid();
                    if (result.getOwner().getId().equals(currentUserId))
                        return;

                    String state = geoMessageChanged.getState();

                    if ("add".equals(state))
                        geoMessageChangeView.addGeoMessage(result);
                    else if ("update".equals(state))
                        geoMessageChangeView.updateGeoMessage(result);
                }

                @Override
                public void onError(Throwable what) {
                    geoMessageChangeView.showErrorMessage(what);
                }
            });
        } else {
            geoMessageChangeView.removeGeoMessage(geoMessageChanged.getId());
        }
    }

    private boolean checkInbound(GeoMessageChanged geoMessageChanged) {
        LatLng latLng = new LatLng(geoMessageChanged.getLatitude(), geoMessageChanged.getLongitude());
        return geoMessageChangeView.getViewBound().contains(latLng);
    }

    private boolean isNotMine(int id) {
        int messageId = App.getInstance().getBottleContext().getCurrentUserSetting().getMessageId();
        return messageId != id;
    }
}
