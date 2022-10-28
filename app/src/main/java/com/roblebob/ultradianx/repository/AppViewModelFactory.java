package com.roblebob.ultradianx.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AppViewModelFactory  extends ViewModelProvider.NewInstanceFactory {

    private final Context mContext;

    public AppViewModelFactory(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create (@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new AppViewModel(mContext);
    }
}
