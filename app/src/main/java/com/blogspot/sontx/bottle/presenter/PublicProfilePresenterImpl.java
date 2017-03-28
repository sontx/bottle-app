package com.blogspot.sontx.bottle.presenter;

import android.support.annotation.Nullable;

import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.service.FirebasePrivateProfileService;
import com.blogspot.sontx.bottle.model.service.FirebasePublicProfileService;
import com.blogspot.sontx.bottle.model.service.interfaces.Callback;
import com.blogspot.sontx.bottle.model.service.interfaces.PrivateProfileService;
import com.blogspot.sontx.bottle.model.service.interfaces.PublicProfileService;
import com.blogspot.sontx.bottle.presenter.interfaces.PublicProfilePresenter;
import com.blogspot.sontx.bottle.view.interfaces.PublicProfileView;

public class PublicProfilePresenterImpl implements PublicProfilePresenter {
    private final PublicProfileView publicProfileView;
    private final PublicProfileService publicProfileService;
    private final PrivateProfileService privateProfileService;

    public PublicProfilePresenterImpl(PublicProfileView publicProfileView) {
        this.publicProfileView = publicProfileView;
        publicProfileService = new FirebasePublicProfileService(publicProfileView.getContext());
        privateProfileService = new FirebasePrivateProfileService(publicProfileView.getContext());
    }

    public PublicProfilePresenterImpl() {
        this.publicProfileView = null;
        publicProfileService = new FirebasePublicProfileService(null);
        privateProfileService = new FirebasePrivateProfileService(null);
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
                    publicProfileView.showErrorMessage(what.getMessage());
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
                    publicProfileView.showErrorMessage(what.getMessage());
                }
            });
        } else {
            publicProfileService.updatePublicProfileIfEmptyAsync(publicProfile, null);
        }
    }
}
