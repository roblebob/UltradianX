package com.roblebob.ultradianx.repository.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.roblebob.ultradianx.repository.model.Adventure;
import com.roblebob.ultradianx.repository.model.AdventureDao;
import com.roblebob.ultradianx.repository.model.AppDatabase;

public class UpdateAdventureWorker extends Worker {
    public static final String TAG = UpdateAdventureWorker.class.getSimpleName();

    private final AdventureDao mAdventureDao;

    public UpdateAdventureWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mAdventureDao = AppDatabase.getInstance(context).adventureDao();
    }

    @NonNull
    @Override
    public Result doWork() {

        int id = getInputData().getInt("id", -1);
        if (id < 0) {
            Log.e(TAG, "---> worker UpdatePassiveAdventure failed");
            return Result.failure();
        }


        Adventure adventure = mAdventureDao.loadAdventureById( id);








        double priority = getInputData().getDouble("priority", Double.MIN_VALUE);
        String last = getInputData().getString("last");



        mAdventureDao.updatePriority(id, priority);
        mAdventureDao.updateLast(id, last);

        return Result.success();
    }
}
