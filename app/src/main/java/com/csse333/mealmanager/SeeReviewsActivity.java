package com.csse333.mealmanager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
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
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SeeReviewsActivity extends Activity {

    private JSONObject mReviews;
    ArrayList<HashMap<String, String>> reviewList;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_reviews);
        addActionBar(getActionBar());

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        try {
            mReviews = new JSONObject(extras.getString("reviews"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(mReviews.toString());
        boolean empty = false;
        try {
            empty = mReviews.get("reviews").toString().equals("[]");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (empty) {
            ((TextView) findViewById(R.id.info)).setText(R.string.see_reviews_no_reviews);
        } else {
            findViewById(R.id.info).setVisibility(View.GONE);
            reviewList = new ArrayList<>();
            listDataHeader = new ArrayList<>();
            listDataChild = new HashMap<>();

            expListView = (ExpandableListView) findViewById(R.id.expandableListView);

            new getReviews().execute();
        }
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
                Intent logOutIntent = new Intent(SeeReviewsActivity.this, LoginActivity.class);
                ComponentName cn = logOutIntent.getComponent();
                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                startActivity(mainIntent);
                finish();
            }
        });

        findViewById(R.id.menu_search_layout).setVisibility(View.GONE);
        findViewById(R.id.menu_item_clear_shopping_list).setVisibility(View.GONE);
    }

    private class getReviews extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                // Getting JSON Array node
                JSONArray jsonIngredient = mReviews.getJSONArray("reviews");

                // looping through All reviews
                for (int i = 0; i < jsonIngredient.length(); i++) {
                    JSONObject r = jsonIngredient.getJSONObject(i);

                    String reviewer = r.getString("name");
                    String rating = r.getString("rating");
                    String content = r.getString("content");

                    // tmp hashmap for single review
                    HashMap<String, String> review = new HashMap<>();

                    // adding each child node to HashMap key => value
                    //review.put("rating", rating);
                    review.put("content", content);

                    // adding review to review list
                    reviewList.add(review);

                    // adding review to expandable list view
                    List<String> details = new ArrayList<>();
                    details.add(content);

                    listDataHeader.add(reviewer + ": " + rating + " stars");
                    listDataChild.put(listDataHeader.get(i), details);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            /**
             * Updating parsed JSON data into ListView
             * */
            listAdapter = new ExpandableListAdapter(SeeReviewsActivity.this, listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);
        }

    }
}
