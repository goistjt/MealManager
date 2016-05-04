package com.csse333.mealmanager;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RecipeDetailActivity extends Activity {

    private String mEmail;
    private JSONObject mIngredients;
    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> ingredientList;
    HashMap<String, Object> mRecipes;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        mEmail = extras.getString("user_id");
        mRecipes = (HashMap<String, Object>) extras.get("recipe_details");
        try {
            mIngredients = new JSONObject(extras.getString("ingredients"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        screenSetUp();

        ingredientList = new ArrayList<>();
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        expListView = (ExpandableListView) findViewById(R.id.expandableListView);

        Button addToList = (Button) findViewById(R.id.add_to_shopping_list_button);
        addToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mIngredientsTask = new getIngredients();
                //mIngredientsTask.execute();
            }
        });

        new getIngredients().execute();
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

    private void screenSetUp() {
        ((TextView) findViewById(R.id.name)).setText(mRecipes.get("name").toString());

        String authorString = mRecipes.get("author_name").toString();
        if (authorString.equals("null")) {
            setGone((TextView) findViewById(R.id.author));
        } else {
            ((TextView) findViewById(R.id.author)).setText(authorString);
        }

        ((TextView) findViewById(R.id.total_time)).setText(mRecipes.get("total_time").toString() + " minutes");

        TextView cooking_instr = (TextView) findViewById(R.id.cooking_instr);
        cooking_instr.setText(mRecipes.get("instr").toString());
        cooking_instr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : open a popup with the full recipe
            }
        });

        ((TextView) findViewById(R.id.type)).setText(mRecipes.get("type").toString());

        String tags = "";

        if ((boolean) mRecipes.get("vegan")) {
            tags += "Vegan";
        }
        if ((boolean) mRecipes.get("vegetarian")) {
            tags += " Vegetarian";
        }
        if ((boolean) mRecipes.get("gluten")) {
            tags += " Gluten-Free";
        }
        if ((boolean) mRecipes.get("dairy")) {
            tags += " Dairy-Free";
        }
        if (tags.contains(" ")) {
            tags = tags.replace(" ", ", ");
            if (tags.startsWith(", ")) {
                tags = tags.substring(1);
            }
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

    private class getIngredients extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(RecipeDetailActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                // Getting JSON Array node
                JSONArray jsonIngredient = mIngredients.getJSONArray("Ingredients");

                // looping through All Contacts
                for (int i = 0; i < jsonIngredient.length(); i++) {
                    JSONObject r = jsonIngredient.getJSONObject(i);

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
                    ingredientList.add(ingredient);

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
            listAdapter = new ExpandableListAdapter(RecipeDetailActivity.this, listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);
        }

    }
}
