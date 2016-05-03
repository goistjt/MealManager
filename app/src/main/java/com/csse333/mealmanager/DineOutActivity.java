package com.csse333.mealmanager;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> rest = restList.get(position);
                Intent intent = new Intent(DineOutActivity.this, RestDetailActivity.class);
                intent.putExtra("rest_info", rest);
                startActivity(intent);
            }
        });

        new getRecipes().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_log_out:
                Intent logOutIntent = new Intent(this, LoginActivity.class);
                ComponentName cn = logOutIntent.getComponent();
                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                startActivity(mainIntent);
                startActivity(logOutIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
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

                    int id = r.getInt("rest_id");
                    String name = r.getString("rest_name");
                    String phone = r.getString("phone");
                    String email = r.getString("email");
                    String street = r.getString("street");
                    String street2 = r.getString("street_2");
                    String city = r.getString("city");
                    String state = r.getString("state");
                    String postal_code = r.getString("postal_code");
                    String type = r.getString("food_type");
                    boolean kids = r.getBoolean("kid_friendly");
                    boolean bar = r.getBoolean("has_bar");
                    boolean outdoor = r.getBoolean("outdoor_seating");
                    String price = r.getString("price_range");

                    String address = String.format("%s %s %s, %s %s", street, street2, city,
                            state, postal_code);

                    // tmp hashmap for single contact
                    HashMap<String, Object> recipe = new HashMap<>();

                    // adding each child node to HashMap key => value
                    recipe.put("rest_id", id);
                    recipe.put("name", name);
                    recipe.put("phone", phone);
                    recipe.put("email", email);
                    recipe.put("address", address);
                    recipe.put("type", type);
                    recipe.put("kid_friendly", kids);
                    recipe.put("has_bar", bar);
                    recipe.put("outdoor_seating", outdoor);
                    recipe.put("price", price);

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
