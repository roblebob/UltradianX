package com.roblebob.ultradianx.repository.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.roblebob.ultradianx.repository.model.Adventure;
import com.roblebob.ultradianx.repository.model.AdventureDao;
import com.roblebob.ultradianx.repository.model.AppDatabase;
import com.roblebob.ultradianx.repository.model.History;
import com.roblebob.ultradianx.repository.model.HistoryDao;

import java.util.Objects;

/**
 * This worker is used to update the priority of the passive adventures.
 * It checks the last time the adventure was updated, and calculates the new priority, and finally
 * resets 'last' to now.
 */
public class RefreshWorker extends Worker {
    public static final String TAG = RefreshWorker.class.getSimpleName();

    private final AdventureDao mAdventureDao;
    private final HistoryDao mHistoryDao;
    private final WorkManager mWorkManager;

    int mActiveCount = 0;

    public RefreshWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mAdventureDao = AppDatabase.getInstance(context).adventureDao();
        mHistoryDao = AppDatabase.getInstance(context).historyDao();
        mWorkManager = WorkManager.getInstance(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e(TAG, "doWork: ");

        Data data = getInputData();
        int id = data.getInt("id", -1);

        if (id < 1) {
            mAdventureDao.loadAdventureList().forEach( (adventure1) -> {
                // TODO
                Adventure adventure = new Adventure(adventure1);

                adventure.refresh();
                mAdventureDao.update(adventure);
                if (adventure.isActive()) { mActiveCount--; }
            });
            if (mActiveCount > 1) { Log.e(TAG, "---->  " + "WARNING!!! MORE THEN ONE IS ACTIVE!!!!!!!!!!" ); }

        } else {
            Adventure adventure = mAdventureDao.loadAdventureById(id);
            adventure.refresh();

            // if transition ...
            if ( adventure.isActive() &&        //  .. from active
                    !data.getBoolean("active", true) // ... to passive
            ) {

                mHistoryDao.insert( new History( adventure.getId(),
                        adventure.getLasttimePassive(),
                        adventure.getLasttime()));

                mWorkManager.enqueue(OneTimeWorkRequest.from(ClockifyWorker.class));
            }

            adventure.update( data);
            mAdventureDao.update(adventure);
        }

        return Result.success();
    }
}
