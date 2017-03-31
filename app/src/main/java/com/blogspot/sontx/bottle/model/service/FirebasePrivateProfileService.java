package com.blogspot.sontx.bottle.model.service;

import android.content.Context;
import android.net.Uri;

import com.blogspot.sontx.bottle.Constants;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.service.interfaces.PrivateProfileService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

class FirebasePrivateProfileService extends FirebaseServiceBase implements PrivateProfileService {
    private FirebaseUser firebaseUser;

    FirebasePrivateProfileService(Context context) {
        super(context);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public PublicProfile getDefaultPublicProfile() {
        PublicProfile publicProfile = new PublicProfile();
        publicProfile.setDisplayName(firebaseUser.getDisplayName());
        Uri photoUrl = firebaseUser.getPhotoUrl();
        publicProfile.setAvatarUrl(photoUrl != null ? photoUrl.toString() : System.getProperty(Constants.UI_DEFAULT_AVATAR_URL_KEY));
        publicProfile.setId(firebaseUser.getUid());
        return publicProfile;
    }

    @Override
    public String getCurrentUserId() {
        return firebaseUser.getUid();
    }
}
