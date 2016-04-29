package com.csse333.mealmanager;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DineOutActivity extends ListActivity {

    private ProgressDialog pDialog;
    private String mEmail;
    private JSONObject mRestaurants;
    private ListView mListView;
    ArrayList<HashMap<String, Object>> restList;
    JSONArray recipes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_list_view);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        mEmail = extras.getString("user_id");
        try {
            mRestaurants = new JSONObject(getIntent().getStringExtra("restaurants"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        restList = new ArrayList<>();
        mListView = getListView();

        new getRecipes().execute();
    }

    private class getRecipes extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(DineOutActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                // Getting JSON Array node
                recipes = mRestaurants.getJSONArray("Restaurants");

                // looping through All Contacts
                for (int i = 0; i < recipes.length(); i++) {
                    JSONObject r = recipes.getJSONObject(i);

                    String name = r.getString("rest_name");
                    String time = r.getString("phone");
                    String type = r.getString("food_type");
                    int id = r.getInt("rest_id");

                    // tmp hashmap for single contact
                    HashMap<String, Object> recipe = new HashMap<>();

                    // adding each child node to HashMap key => value
                    recipe.put("name", name);
                    recipe.put("phone", time);
                    recipe.put("type", type);
                    recipe.put("rest_id", id);

                    // adding contact to contact list
                    restList.add(recipe);
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
                    DineOutActivity.this,
                    restList,
                    R.layout.activity_dine_out,
                    new String[]{"name", "phone", "type"},
                    new int[]{R.id.name, R.id.phone, R.id.type});
            setListAdapter(adapter);

        }

    }

}
