package com.blogspot.sontx.bottle.model.service;

import android.content.Context;
import android.net.Uri;

import com.blogspot.sontx.bottle.Constants;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.service.interfaces.PrivateProfileService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

class FirebasePrivateProfileService extends FirebaseServiceBase implements PrivateProfileService {

    FirebasePrivateProfileService(Context context) {
        super(context);
    }

    @Override
    public PublicProfile getDefaultPublicProfile() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null)
            return null;
        PublicProfile publicProfile = new PublicProfile();
        publicProfile.setDisplayName(firebaseUser.getDisplayName());
        Uri photoUrl = firebaseUser.getPhotoUrl();
        publicProfile.setAvatarUrl(photoUrl != null ? photoUrl.toString() : System.getProperty(Constants.UI_DEFAULT_AVATAR_URL_KEY));
        publicProfile.setId(firebaseUser.getUid());
        return publicProfile;
    }

    @Override
    public String getCurrentUserId() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null)
            return null;
        return firebaseUser.getUid();
    }
}
