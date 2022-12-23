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
import java.util.Objects;

public class UpdateActiveAdventureWorker extends Worker {
    public static final String TAG = UpdateActiveAdventureWorker.class.getSimpleName();

    private final AdventureDao mAdventureDao;

    public UpdateActiveAdventureWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mAdventureDao = AppDatabase.getInstance(context).adventureDao();
    }

    @NonNull
    @Override
    public Result doWork() {


        Adventure newAdventure = new Adventure( getInputData());
        Adventure oldAdventure = mAdventureDao.loadAdventureById( newAdventure.getId());

        if (!newAdventure.getLast().equals(oldAdventure.getLast())) {
            Log.e(TAG, "---->  inconsistency warning regarding last !!!!!");
            Log.e(TAG, "---> worker failed");
            return Result.failure();
        }

        Instant oldLast = Instant.parse( oldAdventure.getLast());
        Instant newLast = Instant.parse( UtilKt.getRidOfMillis( Instant.now().toString()));

        Duration duration = Duration.between(oldLast, newLast);

        if (!Objects.equals(newAdventure.getPriority(), oldAdventure.getPriority())) {
            Log.e(TAG, "---->  inconsistency warning regarding priority !!!!!");
            Log.e(TAG, "---> worker failed");
            return Result.failure();
        }

        double oldPriority = oldAdventure.getPriority();
        double newPriority = oldPriority - ( duration.getSeconds() * newAdventure.getDecay());

        newAdventure.setLast( newLast.toString());
        newAdventure.setPriority( newPriority);

        mAdventureDao.update( newAdventure);

        return Result.success();
    }
}
