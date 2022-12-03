package com.roblebob.ultradianx;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.roblebob.ultradianx.ui.MainFragment;
import com.roblebob.ultradianx.ui.OverviewFragment;
import com.roblebob.ultradianx.ui.adapter.OverviewRVAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    //.replace(R.id.container, MainFragment.newInstance())
                    .replace(R.id.container, OverviewFragment.newInstance("",""))
                    .commitNow();
        }
    }
}