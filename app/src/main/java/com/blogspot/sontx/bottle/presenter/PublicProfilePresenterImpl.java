package com.blogspot.sontx.bottle.presenter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerProfileService;
import com.blogspot.sontx.bottle.model.service.interfaces.PrivateProfileService;
import com.blogspot.sontx.bottle.model.service.interfaces.PublicProfileService;
import com.blogspot.sontx.bottle.presenter.interfaces.PublicProfilePresenter;
import com.blogspot.sontx.bottle.utils.StringUtils;
import com.blogspot.sontx.bottle.view.interfaces.PublicProfileView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class PublicProfilePresenterImpl extends PresenterBase implements PublicProfilePresenter {
    private final PublicProfileView publicProfileView;
    private final PublicProfileService publicProfileService;
    private final PrivateProfileService privateProfileService;
    private final BottleServerProfileService bottleServerProfileService;

    public PublicProfilePresenterImpl(PublicProfileView publicProfileView) {
        this.publicProfileView = publicProfileView;
        publicProfileService = FirebaseServicePool.getInstance().getPublicProfileService();
        privateProfileService = FirebaseServicePool.getInstance().getPrivateProfileService();
        bottleServerProfileService = FirebaseServicePool.getInstance().getBottleServerProfileService();
    }

    public PublicProfilePresenterImpl() {
        this(null);
    }

    private PublicProfile getDefaultPublicProfile() {
        return privateProfileService.getDefaultPublicProfile();
    }

    private void uploadToStorageAsync(File file, final Callback<String> callback) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String currentUserId = App.getInstance().getBottleContext().getCurrentBottleUser().getUid();
        StorageReference currentUserPhotoRef = storage.getReference("photos").child(currentUserId);
        currentUserPhotoRef.delete();
        StorageReference avatarRef = currentUserPhotoRef.child(StringUtils.randomString());

        Uri fileUri = Uri.fromFile(file);
        avatarRef.putFile(fileUri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                publicProfileView.showErrorMessage(e);
                callback.onSuccess(null);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests")
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                callback.onSuccess(downloadUrl != null ? downloadUrl.toString() : null);
            }
        });
    }

    @Override
    public void updateDisplayNameAsync(String newDisplayName) {
        if (StringUtils.isEmpty(newDisplayName)) {
            publicProfileView.showErrorMessage("Display name is empty");
            return;
        }

        final PublicProfile publicProfile = App.getInstance().getBottleContext().getCurrentPublicProfile();
        publicProfile.setDisplayName(newDisplayName);

        // update to bottle server
        bottleServerProfileService.updateProfileAsync(publicProfile, new Callback<PublicProfile>() {
            @Override
            public void onSuccess(PublicProfile result) {
            }

            @Override
            public void onError(Throwable what) {
                publicProfileView.showErrorMessage(what);
            }
        });

        // update to firebase server
        publicProfileService.updatePublicProfileAsync(publicProfile, publicProfileView == null ? null : new Callback<PublicProfile>() {
            @Override
            public void onSuccess(PublicProfile result) {
                publicProfileView.hideProcess();
                publicProfileView.onUpdatedPublicProfile(publicProfile);
            }

            @Override
            public void onError(Throwable what) {
                publicProfileView.showErrorMessage(what);
                publicProfileView.onUpdatedPublicProfile(null);
            }
        });
    }

    @Override
    public void updateAvatarAsync(File avatarFile) {
        if (!avatarFile.exists()) {
            publicProfileView.showErrorMessage("Avatar photo isn't exist");
            return;
        }

        uploadToStorageAsync(avatarFile, new Callback<String>() {
            @Override
            public void onSuccess(String result) {
                if (result == null) {
                    Log.d(TAG, "avatar url is empty @@");
                    return;
                }

                Log.d(TAG, "new avatar url: " + result);

                final PublicProfile publicProfile = App.getInstance().getBottleContext().getCurrentPublicProfile();
                publicProfile.setAvatarUrl(result);

                // update to bottle server
                bottleServerProfileService.updateProfileAsync(publicProfile, new Callback<PublicProfile>() {
                    @Override
                    public void onSuccess(PublicProfile result) {
                    }

                    @Override
                    public void onError(Throwable what) {
                        publicProfileView.showErrorMessage(what);
                    }
                });

                // update to firebase server
                publicProfileService.updatePublicProfileAsync(publicProfile, publicProfileView == null ? null : new Callback<PublicProfile>() {
                    @Override
                    public void onSuccess(PublicProfile result) {
                        publicProfileView.hideProcess();
                        publicProfileView.onUpdatedPublicProfile(publicProfile);
                    }

                    @Override
                    public void onError(Throwable what) {
                        publicProfileView.showErrorMessage(what);
                        publicProfileView.onUpdatedPublicProfile(null);
                    }
                });
            }

            @Override
            public void onError(Throwable what) {
                publicProfileView.showErrorMessage(what);
            }
        });
    }

    @Override
    public void updatePublicProfileIfEmptyAsync() {
        final PublicProfile publicProfile = getDefaultPublicProfile();

        if (publicProfileView != null) {
            publicProfileService.updatePublicProfileIfEmptyAsync(publicProfile, new Callback<PublicProfile>() {
                @Override
                public void onSuccess(PublicProfile result) {
                    publicProfileView.onUpdatedPublicProfile(publicProfile);
                }

                @Override
                public void onError(Throwable what) {
                    publicProfileView.showErrorMessage(what);
                }
            });
        } else {
            publicProfileService.updatePublicProfileIfEmptyAsync(publicProfile, null);
        }
    }
}
