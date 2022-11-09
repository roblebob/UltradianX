package com.roblebob.ultradianx.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.roblebob.ultradianx.model.Adventure;
import com.roblebob.ultradianx.worker.UpdateWorker;

import java.util.List;

public class AppViewModel extends ViewModel {

    // private final AppRepository appRepository;
    private WorkManager mWorkManager;
    AdventureDao adventureDao;


    public AppViewModel(@NonNull Application application) {
        super();
        // appRepository = new AppRepository(application.getApplicationContext());

        adventureDao = AppDatabase.getInstance(application.getApplicationContext()).adventureDao();
        mWorkManager = WorkManager.getInstance(application);
    }

    public LiveData<List<Adventure>> getAdventureListLive() {
        return adventureDao.loadAdventureListLive();
    }



    public void start() {

        //appRepository.integrate();


        mWorkManager.enqueue(OneTimeWorkRequest.from(UpdateWorker.class));
    }








}