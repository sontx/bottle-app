package com.blogspot.sontx.bottle.model.service.interfaces;

import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.service.Callback;

public interface BottleServerProfileService extends ServiceBase {
    void updateProfileAsync(PublicProfile publicProfile, Callback<PublicProfile> callback);
}
