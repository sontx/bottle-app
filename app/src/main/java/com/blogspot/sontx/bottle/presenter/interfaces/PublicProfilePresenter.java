package com.blogspot.sontx.bottle.presenter.interfaces;

import com.blogspot.sontx.bottle.model.bean.PublicProfile;

public interface PublicProfilePresenter extends ViewLifecyclePresenter {
    void updatePublicProfileAsync(PublicProfile publicProfile);

    void updatePublicProfileIfEmptyAsync();
}
