package com.csse333.mealmanager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.util.HashMap;

public class RestDetailActivity extends Activity {

    private String mEmail;
    private HashMap<String, Object> mDetails;
    private ProgressDialog pDialog;
    private getMenuItems mItemsTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_details);
        addActionBar(getActionBar());

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        mEmail = extras.getString("user_id");
        mDetails = (HashMap<String, Object>) extras.get("rest_info");
        //recipe.put("rest_id", id);
        screenSetUp();

        Button menuList = (Button) findViewById(R.id.rest_details_button);
        menuList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //placeholder
            }
        });
    }


    public void addActionBar(ActionBar actionBar) {
        final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
                R.layout.action_bar, null);

        // Set up your ActionBar
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);

        final Button actionBarLogout = (Button) findViewById(R.id.menu_item_log_out);
        actionBarLogout.setMaxHeight(actionBar.getHeight());
        actionBarLogout.setMaxWidth(10);
        actionBarLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logOutIntent = new Intent(RestDetailActivity.this, LoginActivity.class);
                startActivity(logOutIntent);
                finish();
            }
        });

        findViewById(R.id.menu_item_search).setVisibility(View.GONE);
        findViewById(R.id.menu_search_bar).setVisibility(View.GONE);
        findViewById(R.id.radio_group).setVisibility(View.GONE);
    }
    private void screenSetUp() {
        ((TextView) findViewById(R.id.name)).setText(mDetails.get("name").toString());

        String phoneString = mDetails.get("phone").toString();
        if (phoneString.equals("")) {
            setGone((TextView) findViewById(R.id.phone_number));
        } else {
            ((TextView) findViewById(R.id.phone_number)).setText(phoneString);
        }

        String emailString = mDetails.get("email").toString();
        if (emailString.equals("")) {
            setGone((TextView) findViewById(R.id.email));
        } else {
            ((TextView) findViewById(R.id.email)).setText(emailString);
        }

        ((TextView) findViewById(R.id.address)).setText(mDetails.get("address").toString());

        String typeString = mDetails.get("type").toString();
        if (typeString.equals("")) {
            setGone((TextView) findViewById(R.id.type));
        } else {
            ((TextView) findViewById(R.id.type)).setText(typeString);
        }

        if ((boolean) mDetails.get("kid_friendly")) {
            ((TextView) findViewById(R.id.kid_friendly)).setText("Kid Friendly");
        } else {
            setGone((TextView) findViewById(R.id.kid_friendly));
        }

        if ((boolean) mDetails.get("has_bar")) {
            ((TextView) findViewById(R.id.bar)).setText("Bar");
        } else {
            setGone((TextView) findViewById(R.id.bar));
        }

        if ((boolean) mDetails.get("outdoor_seating")) {
            ((TextView) findViewById(R.id.outdoor_seating)).setText("Outdoor seating available");
        } else {
            setGone((TextView) findViewById(R.id.outdoor_seating));
        }

        String priceString = mDetails.get("price").toString();
        if (priceString.equals("")) {
            setGone((TextView) findViewById(R.id.price));
        } else {
            ((TextView) findViewById(R.id.price)).setText(priceString);
        }
    }

    private void setGone(TextView v) {
        v.setVisibility(View.GONE);
    }

    private class getMenuItems extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(RestDetailActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
        }

    }
}
