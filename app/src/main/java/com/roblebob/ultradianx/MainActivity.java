package com.roblebob.ultradianx;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
//                .getBoolean("active", false)
//        ) {
//            getTheme().applyStyle( R.style.Theme_UltradianX_active, true);
//        } else {
//            getTheme().applyStyle( R.style.Theme_UltradianX_passive, true);
//        }

        setContentView( R.layout.activity_main);
    }
}