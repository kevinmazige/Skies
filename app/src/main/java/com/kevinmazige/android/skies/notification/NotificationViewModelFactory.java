package com.kevinmazige.android.skies.notification;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import android.content.Context;

import android.support.annotation.NonNull;


import com.kevinmazige.android.skies.data.DataRepository;

import java.lang.reflect.InvocationTargetException;

public class NotificationViewModelFactory implements ViewModelProvider.Factory {

    private final DataRepository mRepository;

    private NotificationViewModelFactory(DataRepository mRepository) {
        this.mRepository = mRepository;
    }

    public static NotificationViewModelFactory createFactory(Activity activity) {
        Context context = activity.getApplicationContext();
        if (context == null) {
            throw new IllegalStateException("Not yet attached to Application");
        }
        return new NotificationViewModelFactory(DataRepository
                .getInstance(context));
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.getConstructor(DataRepository.class).newInstance(mRepository);
        } catch (InstantiationException | IllegalAccessException |
                NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }
    }
}


