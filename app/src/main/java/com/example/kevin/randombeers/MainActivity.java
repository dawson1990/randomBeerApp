package com.example.kevin.randombeers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void nextBeer(View view){
        String url = "http://api.brewerydb.com/v2/?key=cc83335205f3ba0857a4e8d335ff7050?format=json";

    }
}
