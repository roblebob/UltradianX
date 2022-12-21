package com.roblebob.ultradianx.viewmodel;

import android.app.Application;
import android.util.Log;

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
import com.roblebob.ultradianx.repository.worker.ClockifyWorker;
import com.roblebob.ultradianx.repository.worker.InitialRunWorker;
import com.roblebob.ultradianx.repository.worker.UpdateAdventureWorker;

import java.time.Instant;
import java.util.List;

public class AppViewModel extends ViewModel {
    public static final String TAG = AppViewModel.class.getSimpleName();

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
    public LiveData<Adventure> getAdventureByIdLive(int id) { return adventureDao.loadAdventureByIdLive( id); }
    public LiveData<Adventure> getAdventureByTitleLive(String title) { return adventureDao.loadAdventureByTitleLive( title); }

    public LiveData<String> getAppStateByKey( String key) { return appStateDao.loadValueByKeyLive( key); }




    public void initialRun() {
        mWorkManager.enqueue(OneTimeWorkRequest.from(InitialRunWorker.class));


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



    public void updateAdventure(int id, double priority, String last) {
        Data.Builder builder = new Data.Builder();
        builder.putInt("id", id);
        builder.putDouble("priority", priority);
        builder.putString("last", last);
        Data data = builder.build();

        OneTimeWorkRequest.Builder requestBuilder = new OneTimeWorkRequest.Builder( UpdateAdventureWorker.class);
        requestBuilder.setInputData( data);
        mWorkManager.enqueue( requestBuilder.build());

        Log.e(TAG, "----> Adventure has been updated");
    }

}