package com.roblebob.ultradianx.viewmodel;

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
import com.roblebob.ultradianx.repository.worker.ClockifyWorker;
import com.roblebob.ultradianx.repository.worker.DefaultWorker;

import java.time.Instant;
import java.util.List;

public class AppViewModel extends ViewModel {

    private final WorkManager mWorkManager;
    AdventureDao adventureDao;

    public AppViewModel(@NonNull Application application) {
        super();
        adventureDao = AppDatabase.getInstance(application.getApplicationContext()).adventureDao();
        mWorkManager = WorkManager.getInstance(application);
    }

    public LiveData<List<Adventure>> getAdventureListLive() {
        return adventureDao.loadAdventureListLive();
    }

    public LiveData<Adventure> getAdventureLive( String title) {
        return adventureDao.loadAdventureLive( title);
    }

    public void start() {
        mWorkManager.enqueue(OneTimeWorkRequest.from(DefaultWorker.class));

//        mWorkManager.enqueue(
//                new PeriodicWorkRequest.Builder(ReminderWorker.class,
//                        15, TimeUnit.MINUTES,
//                        1, TimeUnit.MINUTES)
//                        .build()
//        );
    }

    public void remoteClockify(String title, Instant t_start, Instant t_end) {
        Data.Builder builder = new Data.Builder();
        builder.putString("title", title);
        builder.putString("t_start", t_start.toString());
        builder.putString("t_end", t_end.toString());
        Data data = builder.build();

        OneTimeWorkRequest.Builder requestBuilder = new OneTimeWorkRequest.Builder( ClockifyWorker.class);
        requestBuilder.setInputData(data);

        mWorkManager.enqueue(requestBuilder.build());
    }
}