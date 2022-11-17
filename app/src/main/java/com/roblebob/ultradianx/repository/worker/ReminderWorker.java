package com.roblebob.ultradianx.repository.worker;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.roblebob.ultradianx.MainActivity;

public class ReminderWorker extends Worker {
    public static final String TAG = ReminderWorker.class.getSimpleName();

    Context mContext;

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e(TAG, "!!!!running doWork()");
        Intent intent = new Intent( mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        return Result.success();
    }
}
