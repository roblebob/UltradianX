package com.roblebob.ultradianx.repository.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.roblebob.ultradianx.repository.model.AppDatabase;
import com.roblebob.ultradianx.repository.model.History;
import com.roblebob.ultradianx.repository.model.HistoryDao;
import com.roblebob.ultradianx.util.UtilKt;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class HistoryWorker extends Worker {
    public static final String TAG = HistoryWorker.class.getSimpleName();


    private final HistoryDao historyDao;

    public HistoryWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        historyDao = AppDatabase.getInstance(context).historyDao();
    }

    @NonNull
    @Override
    public Result doWork() {

        Data data = getInputData();

        int adventureId = data.getInt("adventureId", -1);
        String start = data.getString("start");
        String end = data.getString("end");

        //Log.e(TAG, "----> " + "id: " + adventureId + "   " + "start: " + start + "   " + "end: " + end + "   ");

        List<History> historyList = historyDao.loadHistoryByAdventureId( adventureId);


        if ( historyList.size() > 0  &&
                Duration.between(
                        Instant.parse( historyList.get(0).getEnd()),
                        Instant.parse(start)
                ).getSeconds() < 1) {


            historyList.get(0).setEnd( end);

            historyDao.update(historyList.get(0));
        } else {
            historyDao.insert( new History( adventureId, start, end));
        }


        //Log.e(TAG, UtilKt.historyList2String( historyDao.loadEntireHistory()));


        return Result.success();
    }
}
