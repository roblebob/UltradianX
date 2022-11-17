package com.roblebob.ultradianx.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.roblebob.ultradianx.repository.model.Adventure;
import com.roblebob.ultradianx.repository.model.AdventureDao;
import com.roblebob.ultradianx.repository.model.AppDatabase;
import com.roblebob.ultradianx.repository.model.Detail;
import com.roblebob.ultradianx.repository.model.DetailDao;
import com.roblebob.ultradianx.repository.worker.ReminderWorker;
import com.roblebob.ultradianx.repository.worker.UpdateWorker;
import com.roblebob.ultradianx.repository.worker.UpdateWorker4Details;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AppViewModel extends ViewModel {

    // private final AppRepository appRepository;
    private WorkManager mWorkManager;
    AdventureDao adventureDao;
    DetailDao detailDao;


    public AppViewModel(@NonNull Application application) {
        super();
        // appRepository = new AppRepository(application.getApplicationContext());
        adventureDao = AppDatabase.getInstance(application.getApplicationContext()).adventureDao();
        detailDao = AppDatabase.getInstance(application.getApplicationContext()).detailDao();
        mWorkManager = WorkManager.getInstance(application);
    }


    public LiveData<List<Adventure>> getAdventureListLive() {
        return adventureDao.loadAdventureListLive();
    }



    public LiveData<List<Detail>> getDetailsOfAdventure(String adventure) {
        return detailDao.loadDetailsForAdventure( adventure);
    }

    public LiveData<List<Detail>> getTagsOfAdventure(String adventure) {
        return detailDao.loadTagsForAdventure( adventure);
    }

    public LiveData<List<String>> getAdventures() {
        return detailDao.loadAdventures();
    }






    public void start() {
        //appRepository.integrate();

        mWorkManager.enqueue(OneTimeWorkRequest.from(UpdateWorker.class));
        mWorkManager.enqueue(OneTimeWorkRequest.from(UpdateWorker4Details.class));

        mWorkManager.enqueue(
                new PeriodicWorkRequest.Builder(ReminderWorker.class,
                        15, TimeUnit.MINUTES,
                        1, TimeUnit.MINUTES)
                        .build()
        );
    }
}