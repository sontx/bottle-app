package com.blogspot.sontx.bottle.model.service;

import com.blogspot.sontx.bottle.model.Constants;
import com.blogspot.sontx.bottle.model.bean.AccountBasicInfo;
import com.blogspot.sontx.bottle.model.service.interfaces.AccountManagerService;
import com.blogspot.sontx.bottle.model.service.interfaces.Callback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import lombok.NonNull;

public class FirebaseAccountManagerService implements AccountManagerService {
    private final DatabaseReference basicUserRef;

    public FirebaseAccountManagerService() {
        String usersPublicInfoKey = System.getProperty(Constants.FIREBASE_USER_PUBLIC_INFO_KEY);
        basicUserRef = FirebaseDatabase.getInstance().getReference(usersPublicInfoKey);
    }

    @Override
    public void resolveAsync(final String id, final @NonNull Callback<AccountBasicInfo> callback) {
        basicUserRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.onSuccess(dataSnapshot.getValue(AccountBasicInfo.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    @Override
    public void updateUserPublicInfo(final AccountBasicInfo basicInfo) {
        basicUserRef.child(basicInfo.getId()).setValue(basicInfo);
    }
}
