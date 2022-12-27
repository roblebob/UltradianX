package com.roblebob.ultradianx.repository.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.roblebob.ultradianx.repository.model.Adventure;
import com.roblebob.ultradianx.repository.model.AdventureDao;
import com.roblebob.ultradianx.repository.model.AppDatabase;
import com.roblebob.ultradianx.util.UtilKt;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class UpdatePassiveAdventureListWorker extends Worker {
    public static final String TAG = UpdatePassiveAdventureListWorker.class.getSimpleName();

    private final AdventureDao mAdventureDao;

    public UpdatePassiveAdventureListWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mAdventureDao = AppDatabase.getInstance(context).adventureDao();
    }

    @NonNull
    @Override
    public Result doWork() {

        List<Adventure> adventureList = mAdventureDao.loadAdventureList();

        Instant newLast = Instant.parse( UtilKt.getRidOfMillis( Instant.now().toString()));

        adventureList.forEach( (adventure) -> {

            Instant oldLast = Instant.parse( adventure.getLast());

            Duration duration = Duration.between(oldLast, newLast);

            double oldPriority = adventure.getPriority();
            double newPriority = oldPriority + (duration.getSeconds() * adventure.getGrow());

            adventure.setPriority( newPriority);
            adventure.setLast( newLast.toString());

            mAdventureDao.update(adventure);
        });

        return Result.success();
    }
}