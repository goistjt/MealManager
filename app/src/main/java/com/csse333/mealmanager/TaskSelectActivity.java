package com.csse333.mealmanager;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TaskSelectActivity extends AppCompatActivity {

    private String mEmail;
    private Button mDineIn;
    private Button mDineOut;
    private DineInTask mDineInTask = null;
    private DineOutTask mDineOutTask = null;
    private JSONObject mReturnedJSON = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_select);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        mEmail = extras.getString("user_id");
        System.out.println(mEmail);

        mDineIn = (Button) findViewById(R.id.dine_in_button);
        mDineIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDineInTask = new DineInTask();
                mDineInTask.execute((Void) null);
            }
        });

        mDineOut = (Button) findViewById(R.id.dine_out_button);
        mDineOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDineOutTask = new DineOutTask();
                mDineOutTask.execute((Void) null);
            }
        });
    }

    public class DineInTask extends AsyncTask<Void, Void, Boolean> {

        DineInTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String query = "Recipe";

            //"http://meal-manager.csse.srose-hulman.edu/Recipe"
            ServerConnections serverConnections = new ServerConnections();
            mReturnedJSON = serverConnections.getRequest(query);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mDineInTask = null;

            if (success) {
                Intent intent = new Intent(TaskSelectActivity.this, DineInActivity.class);
                intent.putExtra("user_id", mEmail);
                intent.putExtra("recipes", mReturnedJSON.toString());
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            mDineInTask = null;
        }
    }

    public class DineOutTask extends AsyncTask<Void, Void, Boolean> {

        DineOutTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String query = "Restaurant";

            //"http://meal-manager.csse.srose-hulman.edu/Restaurant"
            ServerConnections serverConnections = new ServerConnections();
            mReturnedJSON = serverConnections.getRequest(query);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mDineOutTask = null;

            if (success) {
                Intent intent = new Intent(TaskSelectActivity.this, DineInActivity.class);
                intent.putExtra("user_id", mEmail);
                intent.putExtra("restaurants", mReturnedJSON.toString());
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            mDineOutTask = null;
        }
    }
}
