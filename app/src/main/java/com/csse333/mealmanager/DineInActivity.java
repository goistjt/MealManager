package com.csse333.mealmanager;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DineInActivity extends ListActivity {

    private ProgressDialog pDialog;
    private String mEmail;
    private JSONObject mRecipes;
    private ListView mListView;
    ArrayList<HashMap<String, Object>> recipeList;
    JSONArray recipes = null;
    private RecipeTask mRecipeTask = null;
    private RecipeSearchTask mRecipeSearchTask = null;
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
            mRecipes = new JSONObject(getIntent().getStringExtra("recipes"));
            System.out.println(mRecipes);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        recipeList = new ArrayList<>();
        mListView = getListView();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> recipe = recipeList.get(position);
                mRecipeTask = new RecipeTask(recipe);
                mRecipeTask.execute();
            }
        });

        new getRecipes(mRecipes).execute();
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
                Intent logOutIntent = new Intent(DineInActivity.this, LoginActivity.class);
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
                mRecipeSearchTask = new RecipeSearchTask(query, searchBy[0]);
                mRecipeSearchTask.execute();
            }
        });
    }

    private class getRecipes extends AsyncTask<Void, Void, Void> {

        private JSONObject currRecipes;

        public getRecipes(JSONObject rec) {
            currRecipes = rec;
            recipeList = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(DineInActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                // Getting JSON Array node
                recipes = currRecipes.getJSONArray("Recipes");

                // looping through All Contacts
                for (int i = 0; i < recipes.length(); i++) {
                    JSONObject r = recipes.getJSONObject(i);

                    int id = r.getInt("recipe_id");
                    String author_name = r.getString("author");
                    String name = r.getString("name");
                    String instr = r.getString("cooking_instr");
                    String time = r.getString("total_time");
                    String type = r.getString("type");
                    boolean vegan = r.getBoolean("vegan");
                    boolean vegetarian = r.getBoolean("vegetarian");
                    boolean dairy = r.getBoolean("dairy_free");
                    boolean gluten = r.getBoolean("gluten_free");

                    // tmp hashmap for single contact
                    HashMap<String, Object> recipe = new HashMap<>();

                    // adding each child node to HashMap key => value
                    recipe.put("recipe_id", id);
                    recipe.put("author_name", author_name);
                    recipe.put("name", name);
                    recipe.put("instr", instr);
                    recipe.put("total_time", time);
                    recipe.put("type", type);
                    recipe.put("vegan", vegan);
                    recipe.put("vegetarian", vegetarian);
                    recipe.put("dairy", dairy);
                    recipe.put("gluten", gluten);

                    // adding contact to contact list
                    recipeList.add(recipe);
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
                    DineInActivity.this,
                    recipeList,
                    R.layout.activity_dine_in,
                    new String[]{"name", "total time", "type"},
                    new int[]{R.id.name, R.id.total_time, R.id.type});
            setListAdapter(adapter);

        }
    }

    private class RecipeTask extends AsyncTask<Void, Void, Boolean> {

        HashMap<String, Object> mRecipe;

        RecipeTask(HashMap<String, Object> recipe) {
            mRecipe = recipe;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String query = String.format("Ingredients?recipe_id=%s", mRecipe.get("recipe_id"));

            //"http://meal-manager.csse.srose-hulman.edu/Ingredients"
            ServerConnections serverConnections = new ServerConnections();
            mReturnedJSON = serverConnections.getRequest(query);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRecipeTask = null;

            if (success) {
                Intent intent = new Intent(DineInActivity.this, RecipeDetailActivity.class);
                intent.putExtra("user_id", mEmail);
                intent.putExtra("recipe_details", mRecipe);
                intent.putExtra("ingredients", mReturnedJSON.toString());
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            mRecipeTask = null;
        }
    }

    private class RecipeSearchTask extends AsyncTask<Void, Void, Boolean> {

        private String mSearchString;
        private String mSearchType;

        RecipeSearchTask(String searchString, String searchType) {
            mSearchString = searchString;
            mSearchType = searchType;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String query = String.format("Recipe?%s=%s", mSearchType, mSearchString);

            //"http://meal-manager.csse.srose-hulman.edu/Recipe"
            ServerConnections serverConnections = new ServerConnections();
            mReturnedJSON = serverConnections.getRequest(query);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRecipeSearchTask = null;

            new getRecipes(mReturnedJSON).execute();
        }

        @Override
        protected void onCancelled() {
            mRecipeSearchTask = null;
        }
    }
}
