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
import com.roblebob.ultradianx.util.UtilKt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InitWorker extends Worker {
    public static final String TAG = InitWorker.class.getSimpleName();
    private final AdventureDao mAdventureDao;
    private final AppStateDao mAppStateDao;
    private final String mSrcUrl;

    InitWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParameters) {
        super(appContext, workerParameters);
        mAdventureDao = AppDatabase.getInstance(appContext).adventureDao();
        mAppStateDao = AppDatabase.getInstance( appContext).appStateDao();
        mSrcUrl = appContext.getString(R.string.src_url);
    }


    @NonNull
    @Override
    public Result doWork() {

        if (mAdventureDao.countAdventures() == 0) {

            String result;

            try {
                final OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(mSrcUrl)
                        .build();

                Response response = client.newCall(request).execute();


                result = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error, while gathering request's response", e);
                return Result.failure();
            }


            try {
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    // title
                    String title = jsonObject.getString("title");

                    // tags
                    String tag = jsonObject.getString("tag");

                    // details
                    ArrayList<String> details = new ArrayList<>();
                    JSONArray jsonArrayDetails = jsonObject.getJSONArray("details");
                    for (int ii = 0; ii < jsonArrayDetails.length(); ii++) {
                        details.add(jsonArrayDetails.getString(ii));
                    }

                    // target in hole minutes
                    int target = jsonObject.getInt("target");

                    String now = UtilKt.getRidOfMillis(Instant.now().toString());
                    mAdventureDao.insert(new Adventure(false, title, tag, details, "", 17.0, now, now, target));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Error, while gathering json data", e);
                return Result.failure();
            }

            mAppStateDao.insert(new AppState("initialRun", "run before"));
        } else {
            Log.e(TAG, "Already have data, not running");
        }

        return Result.success();
    }
}
