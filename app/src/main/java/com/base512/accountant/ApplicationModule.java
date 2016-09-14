/**
 * Copyright (C) 2016 Thomas Howe - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Thomas Howe <tentothehundredthpower@gmail.com>, August 2016
 */
package com.base512.accountant;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class ApplicationModule {

    private final Context mContext;
    private final FirebaseDatabase mDatabase;
    private final DatabaseReference mDatabaseReference;
    private final FirebaseUser mUser;

    public ApplicationModule(Context context) {
        mContext = context;
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Provides
    Context provideContext() {
        return mContext;
    }

    @Provides
    @Singleton
    FirebaseDatabase provideDatabase() {
        return mDatabase;
    }

    @Provides
    @Singleton
    FirebaseUser provideUser() {
        return mUser;
    }
}
