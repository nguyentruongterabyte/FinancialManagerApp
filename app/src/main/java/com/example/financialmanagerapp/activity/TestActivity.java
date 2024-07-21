package com.example.financialmanagerapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.adapter.CustomAdapter;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Test
        ListView listView = findViewById(R.id.listView);
        String[] items = getResources().getStringArray(R.array.icon_array);
        @SuppressLint("Recycle") TypedArray icons = getResources().obtainTypedArray(R.array.icon_array);

        CustomAdapter adapter = new CustomAdapter(this, items, icons);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> Log.d("myLog", "Clicked item ID: " + position));
    }
}