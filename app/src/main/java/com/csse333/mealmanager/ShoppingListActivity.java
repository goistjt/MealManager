package com.csse333.mealmanager;

import android.app.ActionBar;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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
            mIngredients = new JSONObject(getIntent().getStringExtra("ingredients"));
            System.out.println(mIngredients);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ingredientList = new ArrayList<>();
        mListView = getListView();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CharSequence text = "Long press to remove item from shopping list";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(ShoppingListActivity.this, text, duration);
                toast.show();
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String ingredient = (String) ingredientList.get(position).get("name");
                // TODO : uncomment this after checking server call below
                // new RemoveItemTask(ingredient).execute();
                return true;
            }
        });

        new getList().execute();
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
                Intent logOutIntent = new Intent(ShoppingListActivity.this, LoginActivity.class);
                startActivity(logOutIntent);
                finish();
            }
        });

        final Button actionBarClearList = (Button) findViewById(R.id.menu_item_clear_shopping_list);
        actionBarClearList.setMaxHeight(actionBar.getHeight());
        actionBarClearList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (HashMap<String, Object> ingr : ingredientList) {
                    String name = (String) ingr.get("name");
                    // TODO : uncomment this after verifying server call below
                    // new RemoveAllTask().execute();
                }
            }
        });

        findViewById(R.id.menu_search_layout).setVisibility(View.GONE);
    }

    private class getList extends AsyncTask<Void, Void, Void> {

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

                    String name = r.getString("ingredient_name");

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
            System.out.println("list: " + ingredientList.toString());
            System.out.println("is this working?");
            ListAdapter adapter = new SimpleAdapter(
                    ShoppingListActivity.this,
                    ingredientList,
                    R.layout.activity_shopping_list,
                    new String[]{"name"},
                    new int[]{R.id.name});
            setListAdapter(adapter);
        }

    }

    private boolean printStatusMessage(int status) {
        CharSequence text;
        switch (status) {
            case 602:
                text = "An error occurred in the Database";
                break;
            case 701:
                text = "Email or recipe id is missing";
                break;
            case 666:
                // suspected injection attack = 666
                text = "Your input cannot contain SQL!";
                break;
            default:
                text = "An error occurred";
                break;
        }
        Toast.makeText(ShoppingListActivity.this, text, Toast.LENGTH_SHORT).show();

        System.out.println(status);
        return (status != 200);
    }

    public class RemoveItemTask extends AsyncTask<Void, Void, Boolean> {

        String ingr;
        RemoveItemTask(String ingredient) {
            this.ingr = ingredient;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: verify this call
            String query = String.format("ShoppingList?email=%singr=%s", mEmail, ingr);

            //"http://meal-manager.csse.srose-hulman.edu/RemoveIngredient"
            final ServerConnections serverConnections = new ServerConnections();
            boolean mReturned = serverConnections.deleteRequest(query);
            if (!mReturned) {
                ShoppingListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        printStatusMessage(serverConnections.getStatusCode());
                    }
                });
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                new ShoppingListTask().execute();
            }
        }
    }

    public class RemoveAllTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: verify this call
            String query = String.format("ShoppingList?email=%s", mEmail);

            //"http://meal-manager.csse.srose-hulman.edu/RemoveAllIngredients"
            final ServerConnections serverConnections = new ServerConnections();
            boolean mReturned = serverConnections.deleteRequest(query);
            if (!mReturned) {
                ShoppingListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        printStatusMessage(serverConnections.getStatusCode());
                    }
                });
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                new ShoppingListTask().execute();
            }
        }
    }

    public class ShoppingListTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            String query = String.format("ShoppingList?email=%s", mEmail);

            //"http://meal-manager.csse.srose-hulman.edu/ShoppingList"
            final ServerConnections serverConnections = new ServerConnections();
            mReturnedJSON = serverConnections.getRequest(query);
            if (mReturnedJSON == null) {
                ShoppingListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        printStatusMessage(serverConnections.getStatusCode());
                    }
                });
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                new getList().execute();
            }
        }
    }
}
