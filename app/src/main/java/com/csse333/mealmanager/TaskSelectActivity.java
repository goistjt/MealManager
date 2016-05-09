package com.csse333.mealmanager;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONObject;

public class TaskSelectActivity extends Activity {

    private String mEmail;
    private DineInTask mDineInTask = null;
    private DineOutTask mDineOutTask = null;
    private ShoppingListTask mShoppingListTask = null;
    private JSONObject mReturnedJSON = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_select);
        addActionBar(getActionBar());

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        mEmail = extras.getString("user_id");
        System.out.println(mEmail);

        Button dineIn = (Button) findViewById(R.id.dine_in_button);
        dineIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDineInTask = new DineInTask();
                mDineInTask.execute((Void) null);
            }
        });

        Button dineOut = (Button) findViewById(R.id.dine_out_button);
        dineOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDineOutTask = new DineOutTask();
                mDineOutTask.execute((Void) null);
            }
        });

        Button shoppingList = (Button) findViewById(R.id.shopping_list_button);
        shoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShoppingListTask = new ShoppingListTask();
                mShoppingListTask.execute((Void) null);
            }
        });
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
                Intent logOutIntent = new Intent(TaskSelectActivity.this, LoginActivity.class);
                startActivity(logOutIntent);
                finish();
            }
        });

        findViewById(R.id.menu_search_layout).setVisibility(View.GONE);
    }

    public class DineInTask extends AsyncTask<Void, Void, Boolean> {

        DineInTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String query = "Recipe";

            //"http://meal-manager.csse.srose-hulman.edu/Recipe"
            ServerConnections serverConnections = new ServerConnections();
            mReturnedJSON = serverConnections.getRequest(query, TaskSelectActivity.this);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mDineInTask = null;

            if (success) {
                Intent intent = new Intent(TaskSelectActivity.this, DineInActivity.class);
                intent.putExtra("user_id", mEmail);
                intent.putExtra("recipes", mReturnedJSON.toString());
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            mDineInTask = null;
        }
    }

    public class DineOutTask extends AsyncTask<Void, Void, Boolean> {

        DineOutTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String query = "Restaurant";

            //"http://meal-manager.csse.srose-hulman.edu/Restaurant"
            ServerConnections serverConnections = new ServerConnections();
            mReturnedJSON = serverConnections.getRequest(query, TaskSelectActivity.this);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mDineOutTask = null;

            if (success) {
                Intent intent = new Intent(TaskSelectActivity.this, DineOutActivity.class);
                intent.putExtra("user_id", mEmail);
                intent.putExtra("restaurants", mReturnedJSON.toString());
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            mDineOutTask = null;
        }
    }

    public class ShoppingListTask extends AsyncTask<Void, Void, Boolean> {

        ShoppingListTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String query = String.format("ShoppingList?email=%s", mEmail);

            //"http://meal-manager.csse.srose-hulman.edu/ShoppingList"
            ServerConnections serverConnections = new ServerConnections();
            mReturnedJSON = serverConnections.getRequest(query, TaskSelectActivity.this);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mShoppingListTask = null;

            if (success) {
                Intent intent = new Intent(TaskSelectActivity.this, ShoppingListActivity.class);
                intent.putExtra("user_id", mEmail);
                intent.putExtra("ingredients", mReturnedJSON.toString());
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            mShoppingListTask = null;
        }
    }
}
