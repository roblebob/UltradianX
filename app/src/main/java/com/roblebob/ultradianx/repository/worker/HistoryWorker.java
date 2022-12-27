package com.roblebob.ultradianx.repository.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.roblebob.ultradianx.repository.model.AppDatabase;
import com.roblebob.ultradianx.repository.model.HistoryDao;

public class HistoryWorker extends Worker {
    public static final String TAG = HistoryWorker.class.getSimpleName();


    private HistoryDao historyDao;

    public HistoryWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        historyDao = AppDatabase.getInstance(context).historyDao();
    }

    @NonNull
    @Override
    public Result doWork() {

        // TODO get the data from previous worker (adventureId, start, end)

        Data data = getInputData();

        Log.e(TAG, "----> "
                + "id: " + data.getInt("adventureId", -1) + "   "
                + "start: " + data.getString("start") + "   "
                + "end: " + data.getString("end") + "   "
        );




        return Result.success();
    }
}
