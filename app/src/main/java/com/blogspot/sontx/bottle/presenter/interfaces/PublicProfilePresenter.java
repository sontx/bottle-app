package com.blogspot.sontx.bottle.presenter.interfaces;

import java.io.File;

public interface PublicProfilePresenter {
    void updateDisplayNameAsync(String displayName);

    void updateAvatarAsync(File avatarFile);

    void updatePublicProfileIfEmptyAsync();
}
