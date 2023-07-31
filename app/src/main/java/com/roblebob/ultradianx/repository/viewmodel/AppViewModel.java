package com.roblebob.ultradianx.repository.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.roblebob.ultradianx.repository.model.Adventure;
import com.roblebob.ultradianx.repository.model.AdventureDao;
import com.roblebob.ultradianx.repository.model.AppDatabase;
import com.roblebob.ultradianx.repository.model.AppStateDao;
import com.roblebob.ultradianx.repository.worker.AddAdventureWorker;
import com.roblebob.ultradianx.repository.worker.InitWorker;
import com.roblebob.ultradianx.repository.worker.RefreshWorker;

import java.util.List;

public class AppViewModel extends ViewModel {
    private final WorkManager mWorkManager;
    AdventureDao adventureDao;
    AppStateDao appStateDao;

    public AppViewModel(@NonNull Application application) {
        super();
        adventureDao = AppDatabase.getInstance(application.getApplicationContext()).adventureDao();
        appStateDao = AppDatabase.getInstance(application.getApplicationContext()).appStateDao();
        mWorkManager = WorkManager.getInstance(application);
    }


    public LiveData<List<Adventure>> getAdventureListLive() { return adventureDao.loadAdventureListLive(); }
    public LiveData<List<Integer>> getAdventureIdListLive() { return adventureDao.loadAdventureIdListLive(); }
    public LiveData<Adventure> getAdventureByIdLive(int id) { return adventureDao.loadAdventureByIdLive( id); }


    public void initialRun() {
        mWorkManager.enqueue( OneTimeWorkRequest.from(InitWorker.class));
    }


    public void refresh(Data data) {
        if (data == null) {
            mWorkManager.enqueue( OneTimeWorkRequest.from( RefreshWorker.class));
        } else {
            mWorkManager.enqueue( new OneTimeWorkRequest
                    .Builder( RefreshWorker.class)
                    .setInputData(data)
                    .build()
            );
        }
    }


    public void addAdventure(Data data) {
        mWorkManager.enqueue( new OneTimeWorkRequest
                .Builder( AddAdventureWorker.class)
                .setInputData(data)
                .build()
        );
    }

}