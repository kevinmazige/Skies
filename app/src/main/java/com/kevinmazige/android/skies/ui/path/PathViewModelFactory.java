package com.kevinmazige.android.skies.ui.path;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.kevinmazige.android.skies.data.DataRepository;

import java.lang.reflect.InvocationTargetException;

public class PathViewModelFactory implements ViewModelProvider.Factory {

    private final DataRepository mRepository;

    public static PathViewModelFactory createFactory(Activity activity) {
        Context context = activity.getApplicationContext();
        if (context == null) {
            throw new IllegalStateException("Not yet attached to Application");
        }
        return new PathViewModelFactory(DataRepository.getInstance(context));
    }

    private PathViewModelFactory(DataRepository dataRepository) {
        mRepository = dataRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.getConstructor(DataRepository.class).newInstance(mRepository);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }
    }
}
