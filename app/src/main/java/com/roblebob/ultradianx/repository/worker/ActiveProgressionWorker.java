package com.roblebob.ultradianx.repository.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.roblebob.ultradianx.repository.model.Adventure;
import com.roblebob.ultradianx.repository.model.AdventureDao;
import com.roblebob.ultradianx.repository.model.AppDatabase;
import com.roblebob.ultradianx.util.UtilKt;

import java.time.Duration;
import java.time.Instant;

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
        if (id < 0) {
            Log.e(TAG, "---> worker failed");
            return Result.failure();
        }

        Adventure adventure = mAdventureDao.loadAdventureById( id);



        //final double DECAY_RATE = 100.0 / (90.0 * 60.0) ;
        final double DECAY_RATE = 100.0 / (90.0) ;

        Instant oldLast = Instant.parse( adventure.getLast());
        Instant newLast = Instant.parse( UtilKt.getRidOfMillis( Instant.now().toString()));

        Duration duration = Duration.between(oldLast, newLast);

        double oldPriority = adventure.getPriority();
        double newPriority = oldPriority - ( duration.getSeconds() * DECAY_RATE);


        mAdventureDao.updatePriority(id, newPriority);
        mAdventureDao.updateLast(id, newLast.toString());


        return Result.success();
    }
}
