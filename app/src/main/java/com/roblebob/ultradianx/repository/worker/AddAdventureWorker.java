package com.roblebob.ultradianx.repository.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.roblebob.ultradianx.repository.model.Adventure;
import com.roblebob.ultradianx.repository.model.AdventureDao;
import com.roblebob.ultradianx.repository.model.AppDatabase;

public class AddAdventureWorker extends Worker {
    public static final String TAG = AddAdventureWorker.class.getSimpleName();

    private final AdventureDao mAdventureDao;

    public AddAdventureWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mAdventureDao = AppDatabase.getInstance(context).adventureDao();
    }

    @NonNull
    @Override
    public Result doWork() {

        mAdventureDao.insert( new Adventure( getInputData()));

        return Result.success();
    }
}
