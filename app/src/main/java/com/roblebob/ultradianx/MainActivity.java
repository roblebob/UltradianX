package com.roblebob.ultradianx;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.roblebob.ultradianx.ui.MainFragment;
import com.roblebob.ultradianx.ui.OverviewFragment;
import com.roblebob.ultradianx.ui.adapter.OverviewRVAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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