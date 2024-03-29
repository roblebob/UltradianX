package com.roblebob.ultradianx.repository.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.roblebob.ultradianx.repository.model.Adventure;
import com.roblebob.ultradianx.repository.model.AdventureDao;
import com.roblebob.ultradianx.repository.model.AppDatabase;
import com.roblebob.ultradianx.util.UtilKt;

import java.time.Duration;
import java.time.Instant;

/**
 * This worker is used to update the priority of an active adventure.
 */
public class ActiveProgressionWorker extends Worker {
    public static final String TAG = ActiveProgressionWorker.class.getSimpleName();
    private final AdventureDao mAdventureDao;

    public ActiveProgressionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mAdventureDao = AppDatabase.getInstance(context).adventureDao();
    }

    @NonNull
    @Override
    public Result doWork() {

        int id = getInputData().getInt("id", -1);
        if (id < 1) {
            Log.e(TAG, "---> worker failed");
            return Result.failure();
        }
        Adventure adventure = mAdventureDao.loadAdventureById( id);

        Instant last = Instant.parse( adventure.getLast());
        Instant now = Instant.parse( UtilKt.getRidOfMillis( Instant.now().toString()));

        Duration duration = Duration.between(last, now);

        double oldPriority = adventure.getPriority();
        double newPriority = oldPriority - ( duration.getSeconds() * adventure.getDecay());

        mAdventureDao.updatePriority(id, newPriority);
        mAdventureDao.updateLast(id, now.toString());

        Data outputData = new Data.Builder()
                .putInt("adventureId", id)
                .putString("start", last.toString())
                .putString("end", now.toString())
                .build();

        return Result.success( outputData);
    }
}
