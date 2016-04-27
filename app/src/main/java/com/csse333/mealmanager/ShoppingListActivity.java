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

public class ShoppingListActivity extends ListActivity {

    private ProgressDialog pDialog;
    private String mEmail;
    private JSONObject mIngredients;
    private ListView mListView;
    ArrayList<HashMap<String, Object>> ingredientList;
    JSONArray ingredients = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_list_view);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        mEmail = extras.getString("user_id");
        try {
            mIngredients = new JSONObject(getIntent().getStringExtra("ingredients"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ingredientList = new ArrayList<>();
        mListView = getListView();

        new getRecipes().execute();
    }

    private class getRecipes extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ShoppingListActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                // Getting JSON Array node
                ingredients = mIngredients.getJSONArray("Shopping List");

                // looping through All Contacts
                for (int i = 0; i < ingredients.length(); i++) {
                    JSONObject r = ingredients.getJSONObject(i);

                    String name = r.getString("name");

                    // tmp hashmap for single contact
                    HashMap<String, Object> recipe = new HashMap<>();

                    // adding each child node to HashMap key => value
                    recipe.put("name", name);

                    // adding contact to contact list
                    ingredientList.add(recipe);
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
            if (ingredientList.size() == 0) {
                TextView tv = (TextView) findViewById(R.id.empty);
                tv.setText(R.string.empty_shopping_list);
            } else {
                ListAdapter adapter = new SimpleAdapter(
                        ShoppingListActivity.this,
                        ingredientList,
                        R.layout.activity_shopping_list,
                        new String[]{"name"},
                        new int[]{R.id.name});
                setListAdapter(adapter);
            }
        }

    }

}
