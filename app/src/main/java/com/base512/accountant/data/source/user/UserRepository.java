/**
 * Copyright (C) 2016 Thomas Howe - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Thomas Howe <tentothehundredthpower@gmail.com>, August 2016
 */
package com.base512.accountant.data.source.user;

import android.support.annotation.NonNull;

import com.base512.accountant.data.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class UserRepository implements UserDataSource {

    private final DatabaseReference mDatabaseReference;

    @Inject
    public UserRepository(DatabaseReference databaseReference) {
        mDatabaseReference = databaseReference.child("users");
    }

    @Override
    public void getUser(@NonNull final GetDataCallback callback) {
        mDatabaseReference.addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("wakeupTime").getValue(Integer.class) == null) {
                        callback.onDataError();
                        return;
                    }
                        User user = new User(dataSnapshot.child("wakeupTime").getValue(Integer.class));
                        callback.onDataLoaded(user);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    callback.onDataError();
                }
            }
        );
    }

    @Override
    public void setUserWakeupTime(final int time) {
        mDatabaseReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.child("wakeupTime").setValue(time);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }
}
