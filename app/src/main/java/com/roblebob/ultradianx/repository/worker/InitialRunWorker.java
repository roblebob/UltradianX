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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InitialRunWorker extends Worker {
    public static final String TAG = InitialRunWorker.class.getSimpleName();

    //final double GROW = 100. / (24.0 * 60.0 * 60.0);
    final double GROW = 100. / (24.0 * 60.0);
    //final double DECAY = 100.0 / (90.0 * 60.0) ;
    final double DECAY = 100.0 / (90.0) ;

    private final AdventureDao mAdventureDao;
    private final AppStateDao mAppStateDao;
    private final String mSrcUrl;

    InitialRunWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParameters) {
        super(appContext, workerParameters);
        mAdventureDao = AppDatabase.getInstance(appContext).adventureDao();
        mAppStateDao = AppDatabase.getInstance( appContext).appStateDao();
        mSrcUrl = appContext.getString(R.string.src_url);
    }


    @NonNull
    @Override
    public Result doWork() {

        String result;

        try {
            final OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(mSrcUrl)
                    .build();

            Response response = client.newCall(request).execute();


            result = response.body().string();
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,  "Error, while gathering response" , e);
            return Result.failure();
        }


        try {
            JSONArray jsonArray = new JSONArray(result);

            for (int i=0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // title
                String title = jsonObject.getString("title");

                // tags
                String tags = jsonObject.getString("tags");

                // details
                ArrayList<String> details = new ArrayList<>();
                JSONArray jsonArrayDetails = jsonObject.getJSONArray("details");
                for (int ii = 0; ii < jsonArrayDetails.length(); ii++) {
                    details.add(jsonArrayDetails.getString(ii));
                }


                mAdventureDao.insert(new Adventure( title, tags, details, 17.0, Instant.now().toString(), GROW, DECAY));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error, while gathering json data", e);
            return Result.failure();
        }

        mAppStateDao.insert( new AppState("initialRun", "run before"));

        return Result.success();
    }
}
