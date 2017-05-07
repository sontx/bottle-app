package com.blogspot.sontx.bottle.view.interfaces;

import com.blogspot.sontx.bottle.model.bean.GeoMessage;
import com.google.android.gms.maps.model.LatLngBounds;

public interface GeoMessageChangeView extends ViewBase {
    void addGeoMessage(GeoMessage message);

    void updateGeoMessage(GeoMessage message);

    LatLngBounds getViewBound();
}
