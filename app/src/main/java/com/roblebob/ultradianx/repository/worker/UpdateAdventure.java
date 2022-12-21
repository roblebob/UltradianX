package com.roblebob.ultradianx.repository.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.roblebob.ultradianx.repository.model.AdventureDao;
import com.roblebob.ultradianx.repository.model.AppDatabase;

public class UpdateAdventure extends Worker {
    public static final String TAG = UpdateAdventure.class.getSimpleName();

    private final AdventureDao mAdventureDao;

    public UpdateAdventure(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mAdventureDao = AppDatabase.getInstance(context).adventureDao();
    }

    @NonNull
    @Override
    public Result doWork() {

        int id = getInputData().getInt("id", -1);
        double priority = getInputData().getDouble("priority", Double.MIN_VALUE);
        String last = getInputData().getString("last");

        if (id < 0 || priority == Double.MIN_VALUE || last == null || last.isEmpty()) {
            Log.e(TAG, "---> worker UpdatePassiveAdventure failed");
            return Result.failure();
        }

        mAdventureDao.updatePriority(id, priority);
        mAdventureDao.updateLast(id, last);

        return Result.success();
    }
}
