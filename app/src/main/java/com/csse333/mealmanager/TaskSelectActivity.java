package com.csse333.mealmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
                // TODO: check this logic
                Intent logOutIntent = new Intent(this, LoginActivity.class);
                startActivity(logOutIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class DineInTask extends AsyncTask<Void, Void, Boolean> {

        DineInTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String query = "Recipe";

            //"http://meal-manager.csse.srose-hulman.edu/Recipe"
            ServerConnections serverConnections = new ServerConnections();
            mReturnedJSON = serverConnections.getRequest(query);
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
            mReturnedJSON = serverConnections.getRequest(query);
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
            mReturnedJSON = serverConnections.getRequest(query);
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
