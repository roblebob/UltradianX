package com.roblebob.ultradianx.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkManager;

import com.roblebob.ultradianx.repository.model.Adventure;
import com.roblebob.ultradianx.repository.model.AdventureDao;
import com.roblebob.ultradianx.repository.model.AppDatabase;
import com.roblebob.ultradianx.repository.model.AppStateDao;
import com.roblebob.ultradianx.repository.worker.ActiveProgressionWorker;
import com.roblebob.ultradianx.repository.worker.ClockifyWorker;
import com.roblebob.ultradianx.repository.worker.HistoryWorker;
import com.roblebob.ultradianx.repository.worker.InitialRunWorker;
import com.roblebob.ultradianx.repository.worker.UpdatePassiveAdventureListWorker;
import com.roblebob.ultradianx.repository.worker.UpdateActiveAdventureWorker;

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


    public LiveData<String> getAppStateByKeyLive(String key) { return appStateDao.loadValueByKeyLive( key); }

    public LiveData<List<Adventure>> getAdventureListLive() { return adventureDao.loadAdventureListLive(); }
    public LiveData<List<Integer>> getAdventureIdListLive() { return adventureDao.loadAdventureIdListLive(); }
    public LiveData<Adventure> getAdventureByIdLive(int id) { return adventureDao.loadAdventureByIdLive( id); }


    public void initialRun() {
        mWorkManager.enqueue(OneTimeWorkRequest.from(InitialRunWorker.class));
    }


    public void updatePassiveAdventureList() {
        OneTimeWorkRequest.Builder requestBuilder = new OneTimeWorkRequest.Builder( UpdatePassiveAdventureListWorker.class);
        mWorkManager.enqueue( requestBuilder.build());
    }


    public void updateActiveProgression(int id) {
        Data.Builder builder = new Data.Builder();
        builder.putInt("id", id);
        Data data = builder.build();

        OneTimeWorkRequest.Builder updateActiveProgressionRequestBuilder = new OneTimeWorkRequest.Builder( ActiveProgressionWorker.class);
        updateActiveProgressionRequestBuilder.setInputData( data);


        WorkContinuation continuation = mWorkManager.beginWith( updateActiveProgressionRequestBuilder.build());

        OneTimeWorkRequest.Builder historyRequestBuilder = new OneTimeWorkRequest.Builder( HistoryWorker.class);

        continuation = continuation.then(historyRequestBuilder.build());

        continuation.enqueue();

        //mWorkManager.enqueue( updateActiveProgressionRequestBuilder.build());
    }


    public void updateActiveAdventure( Data data) {
        OneTimeWorkRequest.Builder updateActiveAdventureRequestBuilder = new OneTimeWorkRequest.Builder( UpdateActiveAdventureWorker.class);
        updateActiveAdventureRequestBuilder.setInputData( data);


        WorkContinuation continuation = mWorkManager.beginWith( updateActiveAdventureRequestBuilder.build());

        OneTimeWorkRequest.Builder historyRequestBuilder = new OneTimeWorkRequest.Builder( HistoryWorker.class);

        continuation = continuation.then(historyRequestBuilder.build());

        continuation.enqueue();


        //mWorkManager.enqueue( updateActiveAdventureRequestBuilder.build());
    }


    // TODO chain call (continuation) to HistoryWorker from both UpdateActiveProgressionWorker and UpdateActiveAdventureWorker
    // TODO ClockifyWorker


















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



    //        mWorkManager.enqueue(
//                new PeriodicWorkRequest.Builder(ReminderWorker.class,
//                        15, TimeUnit.MINUTES,
//                        1, TimeUnit.MINUTES)
//                        .build()
//        );
}