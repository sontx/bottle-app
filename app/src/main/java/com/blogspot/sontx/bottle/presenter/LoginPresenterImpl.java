package com.blogspot.sontx.bottle.presenter;

import android.util.Log;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.LoginData;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.SimpleCallback;
import com.blogspot.sontx.bottle.model.service.interfaces.ChatServerLoginService;
import com.blogspot.sontx.bottle.model.service.interfaces.PrivateProfileService;
import com.blogspot.sontx.bottle.model.service.interfaces.PublicProfileService;
import com.blogspot.sontx.bottle.presenter.interfaces.LoginPresenter;
import com.blogspot.sontx.bottle.system.provider.Auth2Provider;
import com.blogspot.sontx.bottle.view.interfaces.LoginView;

public class LoginPresenterImpl extends PresenterBase implements LoginPresenter {
    private final LoginView loginView;
    private final PublicProfileService publicProfileService;
    private final PrivateProfileService privateProfileService;
    private final ChatServerLoginService chatServerLoginService;

    public LoginPresenterImpl(@lombok.NonNull LoginView loginView) {
        this.loginView = loginView;
        publicProfileService = FirebaseServicePool.getInstance().getPublicProfileService();
        privateProfileService = FirebaseServicePool.getInstance().getPrivateProfileService();
        chatServerLoginService = FirebaseServicePool.getInstance().getChatServerLoginService();
    }

    @Override
    public void facebookLoginAsync(LoginData loginData) {
        if (loginData.getState() != LoginData.STATE_SUCCESS) {
            if (loginData.getState() == LoginData.STATE_ERROR)
                loginView.showErrorMessage(loginView.getContext().getString(R.string.facebook_login_error));
            loginView.onLoginStateChanged(false);
        } else {
            chatServerLoginService.facebookLoginAsync(loginData, new SimpleCallback<String>() {
                @Override
                public void onCallback(String value) {
                    if (value == null) {
                        loginView.showErrorMessage("Authentication failed.");
                        loginView.onLoginStateChanged(false);
                    } else {
                        updatePublicProfileIfEmptyAsync();
                    }
                }
            });
        }
    }

    @Override
    public void logout() {
        Auth2Provider currentProvider = App.getInstance().getBottleContext().getCurrentAuth2Provider();
        if (currentProvider != null)
            currentProvider.logout();

        chatServerLoginService.signOut(new SimpleCallback<String>() {
            @Override
            public void onCallback(String value) {
                Log.d(TAG, "logout");
            }
        });
    }

    @Override
    public void checkLoginWithChatServer() {
        if (chatServerLoginService.isLoggedIn()) {
            updatePublicProfileIfEmptyAsync();
        } else {
            loginView.onLoginStateChanged(false);
        }
    }

    private void updatePublicProfileIfEmptyAsync() {
        PublicProfile defaultPublicProfile = privateProfileService.getDefaultPublicProfile();
        publicProfileService.updatePublicProfileIfEmptyAsync(defaultPublicProfile, new Callback<PublicProfile>() {
            @Override
            public void onSuccess(PublicProfile result) {
                loginView.onLoginStateChanged(true);
            }

            @Override
            public void onError(Throwable what) {
                loginView.showErrorMessage(what.getMessage());
                loginView.onLoginStateChanged(false);
            }
        });
    }
}
