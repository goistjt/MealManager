package com.csse333.mealmanager;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

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
    JSONArray rests = null;
    private RestaurantSearchTask mRestaurantSearchTask = null;
    private MenuItemsTask mMenuItemTask = null;
    private JSONObject mReturnedJSON = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_list_view);
        addActionBar(getActionBar());

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
                mMenuItemTask = new MenuItemsTask(rest);
                mMenuItemTask.execute();
            }
        });

        new getRestaurants(mRestaurants).execute();
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
                Intent logOutIntent = new Intent(DineOutActivity.this, LoginActivity.class);
                ComponentName cn = logOutIntent.getComponent();
                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                startActivity(mainIntent);
                finish();
            }
        });

        final EditText searchText = (EditText) findViewById(R.id.menu_search_bar);
        final String[] searchBy = {""};

        Spinner spinner = (Spinner) findViewById(R.id.menu_search_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.search_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchBy[0] = parent.getItemAtPosition(position).equals("Name") ? "name" : "type";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // default selection
                searchBy[0] = "name";
            }
        });

        final Button actionBarSearch = (Button) findViewById(R.id.menu_item_search);
        actionBarSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                String query = searchText.getText().toString();
                mRestaurantSearchTask = new RestaurantSearchTask(query, searchBy[0]);
                mRestaurantSearchTask.execute();
            }
        });
    }

    private class getRestaurants extends AsyncTask<Void, Void, Void> {

        private JSONObject currRests;

        public getRestaurants(JSONObject rest) {
            currRests = rest;
            restList = new ArrayList<>();
        }

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
                rests = currRests.getJSONArray("Restaurants");

                // looping through All Contacts
                for (int i = 0; i < rests.length(); i++) {
                    JSONObject r = rests.getJSONObject(i);

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

    private class MenuItemsTask extends AsyncTask<Void, Void, Boolean> {

        HashMap<String, Object> mRest;

        MenuItemsTask(HashMap<String, Object> rest) {
            mRest = rest;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String query = String.format("RestMenu?rest_id=%s", mRest.get("rest_id"));

            //"http://meal-manager.csse.srose-hulman.edu/RestMenu"
            ServerConnections serverConnections = new ServerConnections();
            mReturnedJSON = serverConnections.getRequest(query);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mMenuItemTask = null;

            if (success) {
                Intent intent = new Intent(DineOutActivity.this, RestDetailActivity.class);
                intent.putExtra("user_id", mEmail);
                intent.putExtra("rest_info", mRest);
                intent.putExtra("menu_items", mReturnedJSON.toString());
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            mMenuItemTask = null;
        }
    }

    private class RestaurantSearchTask extends AsyncTask<Void, Void, Boolean> {

        private String mSearchString;
        private String mSearchType;

        RestaurantSearchTask(String searchString, String searchType) {
            mSearchString = searchString;
            mSearchType = searchType;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String query = String.format("Restaurant?%s=%s", mSearchType, mSearchString);

            //"http://meal-manager.csse.srose-hulman.edu/Restaurant"
            ServerConnections serverConnections = new ServerConnections();
            mReturnedJSON = serverConnections.getRequest(query);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRestaurantSearchTask = null;

            new getRestaurants(mReturnedJSON).execute();
        }

        @Override
        protected void onCancelled() {
            mRestaurantSearchTask = null;
        }
    }
}
