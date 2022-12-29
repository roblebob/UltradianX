package com.roblebob.ultradianx.repository.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.roblebob.ultradianx.R;
import com.roblebob.ultradianx.repository.model.AdventureDao;
import com.roblebob.ultradianx.repository.model.AppDatabase;
import com.roblebob.ultradianx.repository.model.AppState;
import com.roblebob.ultradianx.repository.model.AppStateDao;
import com.roblebob.ultradianx.repository.model.History;
import com.roblebob.ultradianx.repository.model.HistoryDao;
import com.roblebob.ultradianx.util.UtilKt;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ClockifyWorker extends Worker {
    public static final String TAG = ClockifyWorker.class.getSimpleName();

    public static final String CLOCKIFY_USER = "clockify_user_id";
    public static final String CLOCKIFY_WORKSPACE = "clockify_workspace";

    public static final String API_BASE_ENDPOINT = "https://api.clockify.me/api/v1";
    public static final String API_BASE_ENDPOINT_FOR_REPORTS = "https://reports.api.clockify.me/v1";
    public static final String API_BASE_ENDPOINT_FOR_TIME_OFF = "https://pto.api.clockify.me/v1";


    private final AppStateDao appStateDao;
    private final AdventureDao adventureDao;
    private final HistoryDao historyDao;

    final OkHttpClient client = new OkHttpClient();
    final MediaType MEDIA_TYPE = MediaType.parse("application/json, charset=utf-8");


    ClockifyWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParameters) {
        super(appContext, workerParameters);
        AppDatabase appDatabase = AppDatabase.getInstance( appContext);
        appStateDao = appDatabase.appStateDao();
        adventureDao = appDatabase.adventureDao();
        historyDao = appDatabase.historyDao();
    }

    @NonNull
    @Override
    public Result doWork() {

//        if (appStateDao.loadValueByKey(CLOCKIFY_USER) == null) {
//            appStateDao.insert( new AppState( CLOCKIFY_USER, "62595214a59c3f5bb60280c2"));
//            try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
//        }

        if (appStateDao.loadValueByKey(CLOCKIFY_WORKSPACE) == null) {
            appStateDao.insert( new AppState( CLOCKIFY_WORKSPACE, "63ad4e7d46839705993badc2"));  // workspace called UlradianX));
            try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
        }

        Log.e(TAG, appStateDao.loadValueByKey(CLOCKIFY_USER) + "   " + appStateDao.loadValueByKey(CLOCKIFY_WORKSPACE));


//        List<History> historyList = historyDao.loadEntireHistory();
//        historyList.remove( 0);
//
//        historyList.forEach( history -> {
//
//            int adventureId = history.getAdventureId();
//            String adventureTitle = adventureDao.loadAdventureTitleById( adventureId);
//
//            String start = history.getStart();
//            String end   = history.getEnd();
//
//            Duration duration = Duration.between( Instant.parse(start), Instant.parse(end));
//
//            Log.e(TAG, " --> " +
//                    "title: " + adventureTitle + "   " +
//                    "start: " + start + "   " +
//                    "end: "   + end + "   " +
//                    "dur: "   + duration
//            );
//        });








        String result;

        try {

            String projectName = "Android";




            String X = "/workspaces" + "/" + appStateDao.loadValueByKey(CLOCKIFY_WORKSPACE)
                    + "/time-entries"
            ;


            String Y = "{\n" +
                    "  \"start\": \"2022-12-29T12:24:00.000Z\",\n" +
                    //"  \"billable\": \"false\",\n" +
                    //"  \"description\": \"Writing documentation\",\n" +
                    "  \"projectId\": \"63ad76d7d4809211c45cf93c\",\n" +
                    //"  \"taskId\": \"5b1e6b160cb8793dd93ec120\",\n" +
                    "  \"end\": \"2022-12-29T12:30:00.000Z\"" +
                    //"  \"tagIds\": [],\n" +
                    //"  \"customFields\": []\n" +
                    "}";


            Request request = new Request.Builder()
                    .addHeader("content-type", "application/json")
                    .addHeader("X-Api-Key", getApplicationContext().getString(R.string.clockify_api_key))
                    .url(API_BASE_ENDPOINT + X)
                    .post( RequestBody.create(MEDIA_TYPE, Y))
                    .build();




            Response response = client.newCall(request).execute();


            Headers responseHeaders = response.headers();

            for (int i = 0; i < responseHeaders.size(); i++) {
                Log.e(TAG + " Header", responseHeaders.name(i) + ": " + responseHeaders.value(i));
            }


            result = response.body().string();
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,  "Error, while gathering response" , e);
            return Result.failure();
        }


        Log.e(TAG, "---->\n " + result.replace(",", "\n,"));


        return Result.success();
    }
}
