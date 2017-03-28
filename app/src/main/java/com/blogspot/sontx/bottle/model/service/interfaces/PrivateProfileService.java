package com.blogspot.sontx.bottle.model.service.interfaces;

import com.blogspot.sontx.bottle.model.bean.PublicProfile;

public interface PrivateProfileService extends ServiceBase {
    PublicProfile getDefaultPublicProfile();
}
