package com.blogspot.sontx.bottle.view.interfaces;

import com.blogspot.sontx.bottle.model.bean.GeoMessage;

import java.util.List;

public interface MapMessageView extends ViewBase {
    void showMapMessages(List<GeoMessage> geoMessageList);
}
