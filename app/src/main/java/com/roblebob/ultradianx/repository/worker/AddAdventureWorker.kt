package com.roblebob.ultradianx.repository.worker;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.roblebob.ultradianx.repository.model.Adventure;
import com.roblebob.ultradianx.repository.model.AdventureDao;
import com.roblebob.ultradianx.repository.model.AppDatabase;

/**
 * This worker is used to add a new adventure to the database.
 * It checks if the title already exists, and if so, it fails.
 * Otherwise it adds the adventure to the database.
 *
 */
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

        Adventure candidate = new Adventure( getInputData());

        if (mAdventureDao.countAdventuresWithTitle(candidate.title) != 0) {

            Toast.makeText(getApplicationContext(), "Failed, title already exists!"  , Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Failed, title already exists!");
            return Result.failure();
        }

        mAdventureDao.insert( candidate);

        return Result.success();
    }
}
