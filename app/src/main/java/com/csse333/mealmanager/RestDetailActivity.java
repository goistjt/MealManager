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
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RestDetailActivity extends Activity {

    private String mEmail;
    private HashMap<String, Object> mDetails;
    private JSONObject mMenuItems;
    private ProgressDialog pDialog;
    private getMenuItems mItemsTask = null;

    ArrayList<HashMap<String, String>> menuItemList;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_details);
        addActionBar(getActionBar());

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        mEmail = extras.getString("user_id");
        mDetails = (HashMap<String, Object>) extras.get("rest_info");
        mMenuItems = (JSONObject) extras.get("menu_items");
        screenSetUp();

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        expListView = (ExpandableListView) findViewById(R.id.expandableListView);

        new getMenuItems().execute();
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

        findViewById(R.id.menu_search_layout).setVisibility(View.GONE);
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

        String priceString = mDetails.get("price").toString();
        if (priceString.equals("")) {
            setGone((TextView) findViewById(R.id.price));
        } else {
            String price = "Average Price: " + priceString;
            ((TextView) findViewById(R.id.price)).setText(price);
        }

        String tags = "";
        if ((boolean) mDetails.get("kid_friendly")) {
            tags += "Kid Friendly";
        }
        if ((boolean) mDetails.get("has_bar")) {
            tags += ", Bar";
        }
        if ((boolean) mDetails.get("outdoor_seating")) {
            tags += ", Outdoor seating available";
        }
        if (tags.startsWith(", ")) {
            tags = tags.substring(1);
        }
        if (!tags.isEmpty()) {
            ((TextView) findViewById(R.id.tags)).setText(tags);
        } else {
            setGone((TextView) findViewById(R.id.tags));
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
            try {
                // Getting JSON Array node
                JSONArray jsonMenuItem = mMenuItems.getJSONArray("Menu");

                // looping through All menu items
                // TODO : fix this call
                for (int i = 0; i < jsonMenuItem.length(); i++) {
                    JSONObject r = jsonMenuItem.getJSONObject(i);

                    String name = r.getString("name");
                    String ingr_units = r.getString("units");
                    String num_units = r.getString("num_of_units");
                    String unit = r.getString("unit");
                    String fat = r.getString("fat");
                    String sugar = r.getString("sugar");
                    String sodium = r.getString("sodium");
                    String fiber = r.getString("fiber");
                    String protein = r.getString("protein");
                    String calories = r.getString("calories");

                    // tmp hashmap for single ingredient
                    HashMap<String, String> ingredient = new HashMap<>();

                    // adding each child node to HashMap key => value
                    ingredient.put("ingr_units", ingr_units);
                    ingredient.put("num_units", num_units);
                    ingredient.put("unit", unit);
                    ingredient.put("fat", fat);
                    ingredient.put("sugar", sugar);
                    ingredient.put("sodium", sodium);
                    ingredient.put("fiber", fiber);
                    ingredient.put("protein", protein);
                    ingredient.put("calories", calories);
                    ingredient.put("name", name);

                    // adding ingredient to ingredient list
                    menuItemList.add(ingredient);

                    // adding ingredient to expandable list view
                    List<String> details = new ArrayList<>();
                    details.add("amount: " + num_units + " " + unit);
                    //details.add("units: " + unit);
                    details.add("calories: " + calories);
                    //details.add("nutrition content units: " + ingr_units);
                    details.add("fat: " + fat + "g per " + ingr_units);
                    details.add("sugar: " + sugar + "g per " + ingr_units);
                    details.add("sodium: " + sodium + "g per " + ingr_units);
                    details.add("fiber: " + fiber + "g per " + ingr_units);
                    details.add("protein: " + protein + "g per " + ingr_units);
                    listDataHeader.add(name);
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
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            listAdapter = new ExpandableListAdapter(RestDetailActivity.this, listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);
        }

    }
}
