package com.csse333.mealmanager;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RestDetailActivity extends ListActivity {

    private String mEmail;
    private JSONObject mDetails;
    private ProgressDialog pDialog;
    private ListView mListView;
    private getMenuItems mItemsTask = null;
    ArrayList<HashMap<String, Object>> detailList;
    JSONArray detail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_details);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        mEmail = extras.getString("user_id");
        try {
            mDetails = new JSONObject(extras.getString("rest_info"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        detailList = new ArrayList<>();
        mListView = getListView();

        Button menuList = (Button) findViewById(R.id.rest_details_button);
        menuList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //placeholder
            }
        });
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
            try {
                // Getting JSON Array node
                detail = mDetails.getJSONArray("Details");

                // looping through All Contacts
                for (int i = 0; i < detail.length(); i++) {
                    JSONObject r = detail.getJSONObject(i);

                    int id = r.getInt("recipe_id");
                    String name = r.getString("name");
                    String instr = r.getString("cooking_instr");
                    String time = r.getString("total_time");
                    String type = r.getString("type");
                    String vegan = (r.getInt("vegan") == 0) ? "vegan" : "";
                    String dairy = (r.getInt("dairy_free") == 0) ? "dairy free" : "";
                    String gluten = (r.getInt("gluten_free") == 0) ? "gluten free" : "";

                    // tmp hashmap for single contact
                    HashMap<String, Object> recipe = new HashMap<>();

                    // adding each child node to HashMap key => value
                    recipe.put("name", name);
                    recipe.put("total time", time);
                    recipe.put("type", type);
                    recipe.put("recipe_id", id);
                    recipe.put("vegan", vegan);
                    recipe.put("dairy", dairy);
                    recipe.put("gluten", gluten);

                    // adding contact to contact list
                    detailList.add(recipe);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
            ListAdapter adapter = new SimpleAdapter(
                    RestDetailActivity.this,
                    detailList,
                    R.layout.activity_dine_in,
                    new String[]{"name", "total time", "type"},
                    new int[]{R.id.name, R.id.total_time, R.id.type});
            setListAdapter(adapter);

        }

    }
}
