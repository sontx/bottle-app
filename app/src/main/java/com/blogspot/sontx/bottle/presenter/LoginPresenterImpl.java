package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.BottleUser;
import com.blogspot.sontx.bottle.model.bean.LoginData;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.SimpleCallback;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerAuthService;
import com.blogspot.sontx.bottle.model.service.interfaces.LoginService;
import com.blogspot.sontx.bottle.model.service.interfaces.PrivateProfileService;
import com.blogspot.sontx.bottle.model.service.interfaces.PublicProfileService;
import com.blogspot.sontx.bottle.presenter.interfaces.LoginPresenter;
import com.blogspot.sontx.bottle.system.provider.Auth2Provider;
import com.blogspot.sontx.bottle.system.provider.Auth2ProviderBase;
import com.blogspot.sontx.bottle.view.interfaces.LoginView;

public class LoginPresenterImpl extends PresenterBase implements LoginPresenter {
    private final LoginView loginView;
    private final PublicProfileService publicProfileService;
    private final PrivateProfileService privateProfileService;
    private final LoginService loginService;
    private final BottleServerAuthService bottleServerAuthService;

    public LoginPresenterImpl(@lombok.NonNull LoginView loginView) {
        this.loginView = loginView;
        publicProfileService = FirebaseServicePool.getInstance().getPublicProfileService();
        privateProfileService = FirebaseServicePool.getInstance().getPrivateProfileService();
        loginService = FirebaseServicePool.getInstance().getLoginService();
        bottleServerAuthService = FirebaseServicePool.getInstance().getBottleServerAuthService();
    }

    @Override
    public void facebookLoginAsync(LoginData loginData) {
        if (loginData.getState() != LoginData.STATE_SUCCESS) {
            if (loginData.getState() == LoginData.STATE_ERROR)
                loginView.showErrorMessage(loginView.getContext().getString(R.string.facebook_login_error));
        } else {
            loginView.showProcess();
            loginService.facebookLoginAsync(loginData, new SimpleCallback<String>() {
                @Override
                public void onCallback(String value) {
                    loginView.hideProcess();
                    if (value == null) {
                        loginView.showErrorMessage("Authentication failed.");
                        loginView.updateUI(null);
                    } else {
                        verifyWithBottleServerAsync();
                        updatePublicProfileIfEmptyAsync();
                    }
                }
            });
        }
    }

    @Override
    public void logout() {
        loginView.showProcess();

        Auth2Provider currentProvider = Auth2ProviderBase.getCurrentProvider();
        if (currentProvider != null)
            currentProvider.logout();

        loginService.signOut(new SimpleCallback<String>() {
            @Override
            public void onCallback(String value) {
                loginView.hideProcess();
                loginView.updateUI(null);
            }
        });
    }

    @Override
    public void onResume() {
        checkLoginState();
    }

    private void verifyWithBottleServerAsync() {
        loginService.getCurrentUserTokenAsync(new SimpleCallback<String>() {
            @Override
            public void onCallback(String value) {
                if (value != null) {
                    bottleServerAuthService.loginWithTokenAsync(value, new Callback<BottleUser>() {
                        @Override
                        public void onSuccess(BottleUser result) {
                            loginView.updateUI(result.getUid());
                        }

                        @Override
                        public void onError(Throwable what) {
                            loginView.showErrorMessage(what.getMessage());
                            loginView.updateUI(null);
                        }
                    });
                } else {
                    loginView.updateUI(null);
                }
            }
        });
    }

    private void updatePublicProfileIfEmptyAsync() {
        PublicProfile defaultPublicProfile = privateProfileService.getDefaultPublicProfile();
        publicProfileService.updatePublicProfileIfEmptyAsync(defaultPublicProfile, new Callback<PublicProfile>() {
            @Override
            public void onSuccess(PublicProfile result) {
            }

            @Override
            public void onError(Throwable what) {
                loginView.showErrorMessage(what.getMessage());
            }
        });
    }

    private void checkLoginState() {
        String currentUserId = loginService.getCurrentUserId();
        if (currentUserId != null) {
            verifyWithBottleServerAsync();
            updatePublicProfileIfEmptyAsync();
        } else {
            loginView.updateUI(null);
        }
    }
}
