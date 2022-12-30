package com.roblebob.ultradianx.repository.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.roblebob.ultradianx.R;
import com.roblebob.ultradianx.repository.model.Adventure;
import com.roblebob.ultradianx.repository.model.AdventureDao;
import com.roblebob.ultradianx.repository.model.AppDatabase;
import com.roblebob.ultradianx.repository.model.AppState;
import com.roblebob.ultradianx.repository.model.AppStateDao;
import com.roblebob.ultradianx.repository.model.History;
import com.roblebob.ultradianx.repository.model.HistoryDao;
import com.roblebob.ultradianx.util.UtilKt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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


        List<History> historyList = historyDao.loadEntireHistory();

        // TODO determine if deleting current (i=0) is useful
        //historyList.remove( 0);



        historyList.forEach( history -> {

            Adventure adventure = adventureDao.loadAdventureById( history.getAdventureId());
            String start = history.getStart();
            String end   = history.getEnd();

            Log.e(TAG, " --> " + "title: " + adventure.getTitle() + "   " + "start: " + start + "   " + "end: "   + end + "   " + "dur: "   + Duration.between( Instant.parse(start), Instant.parse(end)));

            if (adventure.getClockify() == null) {

                // checking if project (adventure title) exists, and if it does update adventure with its project id
                boolean projectsExists = false;

                try {
                    Request request = new Request.Builder()
                            .addHeader("content-type", "application/json")
                            .addHeader("X-Api-Key", getApplicationContext().getString(R.string.clockify_api_key))
                            .url(API_BASE_ENDPOINT + "/workspaces" + "/" + appStateDao.loadValueByKey(CLOCKIFY_WORKSPACE) + "/projects")
                            .build();

                    Response response = client.newCall(request).execute();

                    String result = response.body().string();

                    JSONArray jsonArray = new JSONArray(result);

                    for (int i=0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);


                        if (jsonObject.getString("name") .equals( adventure.getTitle())) {

                            adventure.setClockify( jsonObject.getString("id"));
                            adventureDao.update(adventure);
                            projectsExists = true;
                            break;
                        }
                    }
                }
                catch (IOException | JSONException e) { Log.e(TAG,  "Error in processing " + adventure.toString() + "  (checking if project - adventure title - exists)", e);}


                if (!projectsExists) {
                    // creating new project
                    try {
                        Request request = new Request.Builder()
                                .addHeader("content-type", "application/json")
                                .addHeader("X-Api-Key", getApplicationContext().getString(R.string.clockify_api_key))
                                .url(API_BASE_ENDPOINT + "/workspaces" + "/" + appStateDao.loadValueByKey(CLOCKIFY_WORKSPACE) + "/projects")
                                .post( RequestBody.create(MEDIA_TYPE, "{\"name\": \"" + adventure.getTitle() +"\"}"))
                                .build();

                        Response response = client.newCall(request).execute();


                        String result = response.body().string();
                        Log.e(TAG, "---->\n " + result.replace(",", "\n,"));
                    } catch (IOException e) { Log.e(TAG,  "Error in processing " + adventure.toString() + "  (creating new project", e);}
                }


                // wait for one second until server has progressed passed request
                try { TimeUnit.SECONDS.sleep(5); } catch (InterruptedException e) { e.printStackTrace(); }
            }



            // updating time entries
            try {

                Request request = new Request.Builder()
                        .addHeader("content-type", "application/json")
                        .addHeader("X-Api-Key", getApplicationContext().getString(R.string.clockify_api_key))
                        .url(API_BASE_ENDPOINT + "/workspaces" + "/" + appStateDao.loadValueByKey(CLOCKIFY_WORKSPACE) + "/time-entries")
                        .post( RequestBody.create(MEDIA_TYPE,
                                "{\n" +
                                        "  \"start\": \"" + start + "\",\n" +
                                        //"  \"billable\": \"false\",\n" +
                                        //"  \"description\": \"Writing documentation\",\n" +
                                        "  \"projectId\": \"" + adventure.getClockify() + "\",\n" +
                                        //"  \"taskId\": \"5b1e6b160cb8793dd93ec120\",\n" +
                                        "  \"end\": \"" + end + "\"" +
                                        //"  \"tagIds\": [],\n" +
                                        //"  \"customFields\": []\n" +
                                        "}"
                                )
                        )
                        .build();

                Response response = client.newCall(request).execute();

                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    Log.e(TAG + " Header", responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                String result = response.body().string();
                Log.e(TAG, "---->\n " + result.replace(",", "\n,"));
            }
            catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,  "Error in processing " + adventure.toString() + "  (updating time entries)", e);
            }

            historyDao.delete( history);
        });















        return Result.success();
    }
}
