package com.roblebob.ultradianx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.roblebob.ultradianx.ui.MainFragment;
import com.roblebob.ultradianx.ui.OverviewFragment;
import com.roblebob.ultradianx.ui.adapter.OverviewRVAdapter;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);

        boolean active = sharedPreferences.getBoolean("active", false);

        Log.e(TAG, " -------> " + active);

        if (active) {
            getTheme().applyStyle(R.style.Theme_UltradianX_active, true);
        } else {
            getTheme().applyStyle(R.style.Theme_UltradianX_passive, true);
        }

        setContentView(R.layout.activity_main);

//        Window w = getWindow();
//        w.clearFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        //w.setStatusBarColor(Color.TRANSPARENT);
//
//        w.setFlags( WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//


//        if (savedInstanceState == null) {
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.container, MainFragment.newInstance())
//                    .commitNow();
//        }
    }


}