package com.blogspot.sontx.bottle.model.service.interfaces;

import android.support.annotation.Nullable;

import com.blogspot.sontx.bottle.model.bean.PublicProfile;

public interface PublicProfileService {
    void updatePublicProfileAsync(PublicProfile publicProfile, @Nullable Callback<PublicProfile> callback);

    void updatePublicProfileIfEmptyAsync(PublicProfile publicProfile, @Nullable Callback<PublicProfile> callback);
}
