package com.roblebob.ultradianx.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.roblebob.ultradianx.model.Adventure;

import java.util.List;

public class AppViewModel extends ViewModel {

    private final AppRepository appRepository;

    public AppViewModel(Context context) {
        appRepository = new AppRepository(context);
    }

    public LiveData<List<Adventure>> getAdventureListLive() {
        return appRepository.getAdventureListLive();
    }

    public void start() {
        appRepository.integrate();
    }








}