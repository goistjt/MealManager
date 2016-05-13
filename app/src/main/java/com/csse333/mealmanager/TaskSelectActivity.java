package com.csse333.mealmanager;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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

        Button favoriteRecipes = (Button) findViewById(R.id.favorite_recipes_button);
        favoriteRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Implement this server call and following screen
                //new FavoriteRecipesTask().execute();
            }
        });

        Button favoriteRests = (Button) findViewById(R.id.favorite_restaurants_button);
        favoriteRests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Implement this server call and following screen
                //new FavoriteRestsTask().execute();
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
        findViewById(R.id.menu_item_clear_shopping_list).setVisibility(View.GONE);
    }

    private boolean printStatusMessage(int status) {
        // TODO: Fill in the rest of the error displays
        CharSequence text = "";
        switch (status) {
            case 601:
                // email & password don't correspond = 601
                text = "Email & Password don't match";
                break;
            case 701:
                // any args are missing = 701
                text = "One or more arguments are missing";
                break;
            case 666:
                // suspected injection attack = 666
                text = "Your input cannot contain SQL!";
                break;
        }
        Toast.makeText(TaskSelectActivity.this, text, Toast.LENGTH_SHORT).show();

        System.out.println(status);
        return (status != 200);
    }

    public class DineInTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            String query = "Recipe";

            //"http://meal-manager.csse.srose-hulman.edu/Recipe"
            final ServerConnections serverConnections = new ServerConnections();
            mReturnedJSON = serverConnections.getRequest(query, TaskSelectActivity.this);
            if (mReturnedJSON == null) {
                TaskSelectActivity.this.runOnUiThread(new Runnable() {
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
            mDineInTask = null;

            if (success) {
                Intent intent = new Intent(TaskSelectActivity.this, DineInActivity.class);
                intent.putExtra("user_id", mEmail);
                intent.putExtra("recipes", mReturnedJSON.toString());
                intent.putExtra("type", "all");
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            mDineInTask = null;
        }
    }

    public class DineOutTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            String query = "Restaurant";

            //"http://meal-manager.csse.srose-hulman.edu/Restaurant"
            final ServerConnections serverConnections = new ServerConnections();
            mReturnedJSON = serverConnections.getRequest(query, TaskSelectActivity.this);
            if (mReturnedJSON == null) {
                TaskSelectActivity.this.runOnUiThread(new Runnable() {
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
            mDineOutTask = null;

            if (success) {
                Intent intent = new Intent(TaskSelectActivity.this, DineOutActivity.class);
                intent.putExtra("user_id", mEmail);
                intent.putExtra("restaurants", mReturnedJSON.toString());
                intent.putExtra("type", "all");
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            mDineOutTask = null;
        }
    }

    public class ShoppingListTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            String query = String.format("ShoppingList?email=%s", mEmail);

            //"http://meal-manager.csse.srose-hulman.edu/ShoppingList"
            final ServerConnections serverConnections = new ServerConnections();
            mReturnedJSON = serverConnections.getRequest(query, TaskSelectActivity.this);
            if (mReturnedJSON == null) {
                TaskSelectActivity.this.runOnUiThread(new Runnable() {
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

    public class FavoriteRecipesTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            String query = String.format("Likes?email=%s", mEmail);

            final ServerConnections serverConnections = new ServerConnections();
            mReturnedJSON = serverConnections.getRequest(query, TaskSelectActivity.this);
            if (mReturnedJSON == null) {
                TaskSelectActivity.this.runOnUiThread(new Runnable() {
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
            // TODO : fix this navigation
            if (success) {
                Intent intent = new Intent(TaskSelectActivity.this, DineInActivity.class);
                intent.putExtra("user_id", mEmail);
                intent.putExtra("recipes", mReturnedJSON.toString());
                intent.putExtra("type", "like");
                startActivity(intent);
            }
        }
    }

    public class FavoriteRestsTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            String query = String.format("Enjoys?email=%s", mEmail);

            final ServerConnections serverConnections = new ServerConnections();
            mReturnedJSON = serverConnections.getRequest(query, TaskSelectActivity.this);
            if (mReturnedJSON == null) {
                TaskSelectActivity.this.runOnUiThread(new Runnable() {
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
            // TODO: fix this navigation call
            if (success) {
                Intent intent = new Intent(TaskSelectActivity.this, DineOutActivity.class);
                intent.putExtra("user_id", mEmail);
                intent.putExtra("restaurants", mReturnedJSON.toString());
                intent.putExtra("type", "like");
                startActivity(intent);
            }
        }
    }

}
