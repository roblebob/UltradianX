package com.roblebob.ultradianx.repository.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.roblebob.ultradianx.R;
import com.roblebob.ultradianx.repository.Util;
import com.roblebob.ultradianx.repository.model.AdventureDao;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ClockifyWorker extends Worker {
    public static final String TAG = ClockifyWorker.class.getSimpleName();

    public static final String API_BASE_ENDPOINT = "https://api.clockify.me/api/v1";
    public static final String API_BASE_ENDPOINT_FOR_REPORTS = "https://reports.api.clockify.me/v1";
    public static final String API_BASE_ENDPOINT_FOR_TIME_OFF = "https://pto.api.clockify.me/v1";

    Util util = new Util();

    ClockifyWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParameters) {
        super(appContext, workerParameters);
    }

    @NonNull
    @Override
    public Result doWork() {

        String title = getInputData().getString("title");
        Instant start = Instant.parse( util.getRidOfNanos( getInputData().getString("t_start")));
        Instant end   = Instant.parse( util.getRidOfNanos( getInputData().getString("t_end")));
        if (start.equals(end)) { end = end.plusSeconds(1); }
        Duration duration = Duration.between(start, end);

        Log.e(TAG, " --> " +
                "title: " + title + "   " +
                "start: " + start + "   " +
                "end: "   + end + "   " +
                "dur: "   + duration
        );


        String result;

        try {
            final OkHttpClient client = new OkHttpClient();

            // Test Case
            String userId =     "62595214a59c3f5bb60280c2";
            String workspace =  "62595214a59c3f5bb60280c3";
            String projectId =  "62595239a59c3f5bb60281d5";

            String projectName = "android";
            String time_entry = "{\"start\":\"2022-12-12T15:14:00Z\"}";



            String GET = "/workspaces/" + workspace
                    + "/user/" + userId
                    //+ "/projects/" + projectId
                    + "/time-entries" //  + time_entry
                    ;

            Request request = new Request.Builder()
                    .addHeader("X-Api-Key", getApplicationContext().getString(R.string.clockify_api_key))
                    .url(API_BASE_ENDPOINT + GET)
                    .build();

            Response response = client.newCall(request).execute();


            result = response.body().string();
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,  "Error, while gathering response" , e);
            return Result.failure();
        }


        Log.e(TAG, "---->\n " + result);


        return Result.success();
    }
}
