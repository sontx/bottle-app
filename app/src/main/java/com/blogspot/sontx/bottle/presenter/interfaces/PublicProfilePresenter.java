package com.blogspot.sontx.bottle.presenter.interfaces;

import com.blogspot.sontx.bottle.model.bean.PublicProfile;

public interface PublicProfilePresenter {
    void updatePublicProfileAsync(PublicProfile publicProfile);

    void updatePublicProfileIfEmptyAsync();
}
