package com.blogspot.sontx.bottle.model.service;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.blogspot.sontx.bottle.Constants;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.service.interfaces.Callback;
import com.blogspot.sontx.bottle.model.service.interfaces.PublicProfileService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import lombok.NonNull;

public class FirebasePublicProfileService extends FirebaseServiceBase implements PublicProfileService {
    private final DatabaseReference publicProfileRef;

    public FirebasePublicProfileService(Context context) {
        super(context);
        String publicProfileKey = System.getProperty(Constants.FIREBASE_PUBLIC_PROFILE_KEY);
        publicProfileRef = FirebaseDatabase.getInstance().getReference(publicProfileKey);
    }

    @Override
    public void updatePublicProfileAsync(@NonNull final PublicProfile publicProfile, @Nullable final Callback<PublicProfile> callback) {
        Task<Void> updatePublicProfileTask = publicProfileRef.child(publicProfile.getId()).setValue(publicProfile);
        Log.d(TAG, "updatePublicProfileAsync: updating...");
        if (callback != null) {
            updatePublicProfileTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@android.support.annotation.NonNull Task<Void> task) {
                    Exception exception = task.getException();
                    if (exception != null)
                        callback.onError(exception);
                    else
                        callback.onSuccess(publicProfile);
                    Log.d(TAG, "updatePublicProfileAsync: updated!");
                }
            });
        }
    }

    @Override
    public void updatePublicProfileIfEmptyAsync(@NonNull final PublicProfile publicProfile, @Nullable final Callback<PublicProfile> callback) {
        publicProfileRef.child(publicProfile.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || !dataSnapshot.exists()) {
                    Log.d(TAG, "updatePublicProfileIfEmptyAsync: need updating");
                    updatePublicProfileAsync(publicProfile, callback);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (callback != null)
                    callback.onError(databaseError.toException());
                Log.d(TAG, "updatePublicProfileIfEmptyAsync: " + databaseError.toString());
            }
        });
    }

    @Override
    public void getPublicProfileAsync(String anotherMemberId, final Callback<PublicProfile> callback) {
        publicProfileRef.child(anotherMemberId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PublicProfile publicProfile = dataSnapshot.getValue(PublicProfile.class);
                callback.onSuccess(publicProfile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }
}
