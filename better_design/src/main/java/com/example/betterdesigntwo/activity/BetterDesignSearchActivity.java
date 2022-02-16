package com.example.betterdesigntwo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.betterdesigntwo.R;

public class BetterDesignSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_better_design_search);

        Intent intent = getIntent();
        String ids = intent.getStringExtra("ids" );
        Toast.makeText(BetterDesignSearchActivity.this , ids , Toast.LENGTH_LONG).show();
        Log.d("wwoowowowowowo", ids);
    }
}
