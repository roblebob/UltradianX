package com.roblebob.ultradianx.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.roblebob.ultradianx.viewmodel.AppViewModel;

public class AppViewModelFactory  extends ViewModelProvider.NewInstanceFactory {

    private final Application mApplication;

    public AppViewModelFactory(Application application) {
        mApplication = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create (@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new AppViewModel(mApplication);
    }
}
