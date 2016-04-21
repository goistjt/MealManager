package com.csse333.mealmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TaskSelectActivity extends AppCompatActivity {

    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_select);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        mEmail = extras.getString("user_id");
        System.out.println(mEmail);
    }
}
