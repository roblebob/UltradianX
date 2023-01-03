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

        if (appStateDao.loadValueByKey(CLOCKIFY_WORKSPACE) == null) {
            // TODO mechanism which creates a new clockify workspace if necessary
            appStateDao.insert( new AppState( CLOCKIFY_WORKSPACE, "63ad4e7d46839705993badc2"));  // workspace called UlradianX));
            try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
        }

        List<History> historyList = historyDao.loadEntireHistory();
        historyList.forEach( history -> {

            Adventure adventure = validate( adventureDao.loadAdventureById( history.getAdventureId()));

            try {

                Request request = new Request.Builder()
                        .addHeader("content-type", "application/json")
                        .addHeader("X-Api-Key", getApplicationContext().getString(R.string.clockify_api_key))
                        .url(API_BASE_ENDPOINT + "/workspaces" + "/" + appStateDao.loadValueByKey(CLOCKIFY_WORKSPACE) + "/time-entries")
                        .post( RequestBody.create(MEDIA_TYPE,
                                "{\n" +
                                        "  \"start\": \"" + history.getStart() + "\",\n" +
                                        //"  \"billable\": \"false\",\n" +
                                        //"  \"description\": \"Writing documentation\",\n" +
                                        "  \"projectId\": \"" + adventure.getClockify() + "\",\n" +
                                        //"  \"taskId\": \"5b1e6b160cb8793dd93ec120\",\n" +
                                        "  \"end\": \"" + history.getEnd() + "\"" +
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

                historyDao.delete( history);
            }
            catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,  "Error in processing " + adventure.toString() + "  (updating time entries)", e);
            }
        });

        return Result.success();
    }


    /**
     * function validating an adventure, by guaranteeing a valid clockify id for it
     *
     * @param adventure to be considered
     * @return adventure
     */
    private Adventure validate( Adventure adventure) {

        // check if adventure has a clockify entry
        if (adventure.getClockify() != null) { return adventure; }


        // check if clockify has a project corresponding to the adventure title
        try {
            Response response = client.newCall(
                    new Request.Builder()
                            .addHeader("content-type", "application/json")
                            .addHeader("X-Api-Key", getApplicationContext().getString(R.string.clockify_api_key))
                            .url(API_BASE_ENDPOINT + "/workspaces" + "/" + appStateDao.loadValueByKey(CLOCKIFY_WORKSPACE) + "/projects")
                            .build()
            ).execute();

            JSONArray jsonArray = new JSONArray( response.body().string());
            for (int i=0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if (jsonObject.getString("name") .equals( adventure.getTitle())) {

                    adventure.setClockify( jsonObject.getString("id"));
                    adventureDao.update( adventure);
                    return adventure;
                }
            }


            // create new project associated with the adventure at hand
            response = client.newCall(
                    new Request.Builder()
                            .addHeader("content-type", "application/json")
                            .addHeader("X-Api-Key", getApplicationContext().getString(R.string.clockify_api_key))
                            .url(API_BASE_ENDPOINT + "/workspaces" + "/" + appStateDao.loadValueByKey(CLOCKIFY_WORKSPACE) + "/projects")
                            .post( RequestBody.create(MEDIA_TYPE, "{\"name\": \"" + adventure.getTitle() +"\"}"))
                            .build()
            ).execute();

            adventure.setClockify( new JSONObject( response.body().string()) .getString("id"));
            adventureDao.update( adventure);
            return adventure;
        }
        catch (IOException | JSONException e) {
            Log.e(TAG,  "Error validating adventure " + adventure.getTitle(), e);
            return adventure;
        }
    }
}
