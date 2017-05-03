package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.interfaces.PrivateProfileService;
import com.blogspot.sontx.bottle.model.service.interfaces.PublicProfileService;
import com.blogspot.sontx.bottle.presenter.interfaces.PublicProfilePresenter;
import com.blogspot.sontx.bottle.view.interfaces.PublicProfileView;

public class PublicProfilePresenterImpl extends PresenterBase implements PublicProfilePresenter {
    private final PublicProfileView publicProfileView;
    private final PublicProfileService publicProfileService;
    private final PrivateProfileService privateProfileService;

    public PublicProfilePresenterImpl(PublicProfileView publicProfileView) {
        this.publicProfileView = publicProfileView;
        publicProfileService = FirebaseServicePool.getInstance().getPublicProfileService();
        privateProfileService = FirebaseServicePool.getInstance().getPrivateProfileService();
    }

    public PublicProfilePresenterImpl() {
        this(null);
    }

    private PublicProfile getDefaultPublicProfile() {
        return privateProfileService.getDefaultPublicProfile();
    }

    @Override
    public void updatePublicProfileAsync(final PublicProfile publicProfile) {
        if (publicProfile == null)
            return;

        if (publicProfileView != null) {
            publicProfileView.showProcess();
            publicProfileService.updatePublicProfileAsync(publicProfile, new Callback<PublicProfile>() {
                @Override
                public void onSuccess(PublicProfile result) {
                    publicProfileView.hideProcess();
                    publicProfileView.onUpdatedPublicProfile(publicProfile);
                }

                @Override
                public void onError(Throwable what) {
                    publicProfileView.hideProcess();
                    publicProfileView.showErrorMessage(what);
                    publicProfileView.onUpdatedPublicProfile(null);
                }
            });
        } else {
            publicProfileService.updatePublicProfileAsync(publicProfile, null);
        }
    }

    @Override
    public void updatePublicProfileIfEmptyAsync() {
        final PublicProfile publicProfile = getDefaultPublicProfile();

        if (publicProfileView != null) {
            publicProfileView.showProcess();
            publicProfileService.updatePublicProfileIfEmptyAsync(publicProfile, new Callback<PublicProfile>() {
                @Override
                public void onSuccess(PublicProfile result) {
                    publicProfileView.hideProcess();
                    publicProfileView.onUpdatedPublicProfile(publicProfile);
                }

                @Override
                public void onError(Throwable what) {
                    publicProfileView.hideProcess();
                    publicProfileView.showErrorMessage(what);
                }
            });
        } else {
            publicProfileService.updatePublicProfileIfEmptyAsync(publicProfile, null);
        }
    }
}
