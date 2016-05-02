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
    private JSONObject mReturnedJSON = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_list_view);

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
                String recipe_id = recipeList.get(position).get("recipe_id").toString();
                //mRecipeTask = new RecipeTask(recipe_id);
                //mRecipeTask.execute();
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
            pDialog = new ProgressDialog(DineInActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                // Getting JSON Array node
                recipes = mRecipes.getJSONArray("Recipes");

                // looping through All Contacts
                for (int i = 0; i < recipes.length(); i++) {
                    JSONObject r = recipes.getJSONObject(i);

                    int id = r.getInt("recipe_id");
                    String name = r.getString("name");
                    String instr = r.getString("cooking_instr");
                    String time = r.getString("total_time");
                    String type = r.getString("type");
                    String vegan = (r.getBoolean("vegan")) ? "vegan" : "";
                    String dairy = (r.getBoolean("dairy_free")) ? "dairy free" : "";
                    String gluten = (r.getBoolean("gluten_free")) ? "gluten free" : "";

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

        String recipe_id;
        RecipeTask(String id) {
            recipe_id = id;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String query = String.format("RecipeDetails?recipe_id=%s", recipe_id);

            //"http://meal-manager.csse.srose-hulman.edu/RecipeDetails"
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
                intent.putExtra("details", mReturnedJSON.toString());
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            mRecipeTask = null;
        }
    }
}
