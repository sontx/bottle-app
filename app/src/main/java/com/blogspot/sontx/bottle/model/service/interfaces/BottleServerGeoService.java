package com.blogspot.sontx.bottle.model.service.interfaces;

import com.blogspot.sontx.bottle.model.bean.GeoMessage;
import com.blogspot.sontx.bottle.model.service.Callback;

public interface BottleServerGeoService extends ServiceBase {
    void postGeoMessageAsync(GeoMessage geoMessage, Callback<GeoMessage> callback);

    void editGeoMessageAsync(GeoMessage geoMessage, Callback<GeoMessage> callback);
}
