package com.blogspot.sontx.bottle.presenter.interfaces;

import com.blogspot.sontx.bottle.model.bean.Coordination;
import com.blogspot.sontx.bottle.model.bean.GeoMessage;

public interface GeoMessagePresenter {
    void getMapMessagesAroundMyLocationAsync(double latitudeRadius, double longitudeRadius);

    void postGeoMessageAsync(String text, Coordination coordination, String mediaPath, String type);

    void editGeoMessageAsync(GeoMessage geoMessage);
}
