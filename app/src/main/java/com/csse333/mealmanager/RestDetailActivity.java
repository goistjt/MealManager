package com.csse333.mealmanager;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RestDetailActivity extends Activity {

    private String mEmail;
    private String mType;
    private HashMap<String, Object> mDetails;
    private JSONObject mMenuItems;
    private String mRestId;
    private boolean mReviewed;
    private JSONObject mReview;
    private JSONObject mReviews;
    private boolean mFinished = false;

    ArrayList<HashMap<String, Object>> menuItemList;
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
        mType = extras.getString("type");
        mDetails = (HashMap<String, Object>) extras.get("rest_info");
        mRestId = mDetails.get("rest_id").toString();
        try {
            mMenuItems = new JSONObject(extras.get("menu_items").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        screenSetUp();
        menuItemList = new ArrayList<>();

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        expListView = (ExpandableListView) findViewById(R.id.expandableListView);

        new getMenuItems().execute();
        new getReviewTask().execute();

        Button seeReviews = (Button) findViewById(R.id.rest_details_see_reviews_button);
        seeReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask<Void, Void, Boolean> task = new getAllReviewsTask();
                task.execute();
                try {
                    task.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(RestDetailActivity.this, SeeReviewsActivity.class);
                intent.putExtra("rest_id", mRestId);
                if (mReviews.isNull("reviews")) {
                    intent.putExtra("reviews", "");
                } else {
                    intent.putExtra("reviews", mReviews.toString());
                }
                startActivity(intent);
            }
        });

        Button enjoyRestaurant = (Button) findViewById(R.id.rest_details_like_button);
        if (mType.equals("like")) {
            enjoyRestaurant.setVisibility(View.GONE);
        } else {
            enjoyRestaurant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String rest_id = (int) mDetails.get("rest_id") + "";
                    new LikeRestTask(rest_id).execute();
                }
            });
        }
    }

    static final int LEAVE_REVIEW_REQUEST = 1;
    public void updateButton() {
        final Button leaveReview = (Button) findViewById(R.id.rest_details_review_button);
        if (mReviewed) {
            leaveReview.setText("Edit Review");
            leaveReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RestDetailActivity.this, PostReviewActivity.class);
                    intent.putExtra("rest_id", mRestId);
                    intent.putExtra("user_id", mEmail);
                    intent.putExtra("type", "edit");
                    intent.putExtra("review", mReview.toString());
                    startActivityForResult(intent, LEAVE_REVIEW_REQUEST);
                }
            });
        } else {
            leaveReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RestDetailActivity.this, PostReviewActivity.class);
                    intent.putExtra("rest_id", mRestId);
                    intent.putExtra("user_id", mEmail);
                    intent.putExtra("type", "create");
                    intent.putExtra("review", "");
                    startActivityForResult(intent, LEAVE_REVIEW_REQUEST);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("in onActivityResult");
        if (requestCode == LEAVE_REVIEW_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                // the user left a review
                AsyncTask<Void, Void, Boolean> task = new getReviewTask();
                task.execute();
                try {
                    task.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                updateButton();
            }
        }
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
                ComponentName cn = logOutIntent.getComponent();
                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                startActivity(mainIntent);
                finish();
            }
        });

        findViewById(R.id.menu_search_layout).setVisibility(View.GONE);
        findViewById(R.id.menu_item_clear_shopping_list).setVisibility(View.GONE);
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

        String ratingString = mDetails.get("rating").toString();
        if (ratingString.equals("")) {
            setGone((TextView) findViewById(R.id.rating));
        } else {
            String price = "Average Rating: " + ratingString;
            ((TextView) findViewById(R.id.rating)).setText(price);
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
        protected Void doInBackground(Void... arg0) {
            try {
                // Getting JSON Array node
                JSONArray jsonMenuItem = mMenuItems.getJSONArray("Menu");

                // looping through All menu items
                for (int i = 0; i < jsonMenuItem.length(); i++) {
                    JSONObject r = jsonMenuItem.getJSONObject(i);

                    String price = r.getString("price");
                    String name = r.getString("name");
                    String type = r.getString("type");
                    boolean vegan = r.getBoolean("vegan");
                    boolean vegetarian = r.getBoolean("vegetarian");
                    boolean dairy = r.getBoolean("dairy_free");
                    boolean gluten = r.getBoolean("gluten_free");

                    // tmp hashmap for single ingredient
                    HashMap<String, Object> ingredient = new HashMap<>();

                    // adding each child node to HashMap key => value
                    ingredient.put("price", price);
                    ingredient.put("type", type);
                    ingredient.put("vegan", vegan);
                    ingredient.put("vegetarian", vegetarian);
                    ingredient.put("dairy", dairy);
                    ingredient.put("gluten", gluten);
                    ingredient.put("name", name);

                    // adding to menu item list
                    menuItemList.add(ingredient);

                    // adding to expandable list view
                    List<String> details = new ArrayList<>();
                    details.add("Average Price: " + price);
                    details.add("Type: " + type);

                    String tags = "";
                    if (vegan) {
                        tags += "Vegan";
                    }
                    if (vegetarian) {
                        tags += " Vegetarian";
                    }
                    if (dairy) {
                        tags += " Dairy-Free";
                    }
                    if (gluten) {
                        tags += " Gluten-Free";
                    }
                    if (tags.contains(" ")) {
                        tags = tags.replace(" ", ", ");
                        if (tags.startsWith(", ")) {
                            tags = tags.substring(1);
                        }
                    }
                    if (!tags.equals("")) {
                        details.add(tags);
                    }

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
            /**
             * Updating parsed JSON data into ListView
             * */
            listAdapter = new ExpandableListAdapter(RestDetailActivity.this, listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);
        }

    }

    private boolean printStatusMessage(int status) {
        CharSequence text;
        switch (status) {
            case 601:
                text = "Restaurant already liked";
                break;
            case 602:
                text = "Restaurant id must be an integer";
                break;
            case 701:
                text = "Email or rest_id missing";
                break;
            case 666:
                // suspected injection attack = 666
                text = "Your input cannot contain SQL!";
                break;
            default:
                text = "An error occurred";
                break;
        }
        Toast.makeText(RestDetailActivity.this, text, Toast.LENGTH_SHORT).show();

        System.out.println(status);
        return (status != 200);
    }

    private class LikeRestTask extends AsyncTask<Void, Void, Boolean> {

        JSONObject mReturnedJSON;
        private String rest_id;

        LikeRestTask(String id) {
            this.rest_id = id;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // post
            String charset = "UTF-8";
            String query = "";
            try {
                query = String.format("RestEnjoy?email=%s&rest_id=%s",
                        URLEncoder.encode(mEmail, charset),
                        URLEncoder.encode(rest_id, charset));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            final ServerConnections serverConnections = new ServerConnections();
            mReturnedJSON = serverConnections.postRequest(query);
            if (mReturnedJSON == null) {
                RestDetailActivity.this.runOnUiThread(new Runnable() {
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
                CharSequence text = "Restaurant Liked!";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class getAllReviewsTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // post
            String charset = "UTF-8";
            String query = "";
            try {
                query = String.format("Review?rest_id=%s",
                        URLEncoder.encode(mRestId, charset));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            final ServerConnections serverConnections = new ServerConnections();
            mReviews = serverConnections.getRequest(query);
            if (mReviews == null) {
                RestDetailActivity.this.runOnUiThread(new Runnable() {
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
            System.out.println("finished");
            mFinished = true;
        }
    }

    private class getReviewTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // post
            String charset = "UTF-8";
            String query = "";
            try {
                query = String.format("Review?email=%s&rest_id=%s",
                        URLEncoder.encode(mEmail, charset),
                        URLEncoder.encode(mRestId, charset));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            final ServerConnections serverConnections = new ServerConnections();
            mReview = serverConnections.getRequest(query);
            if (mReview == null) {
                RestDetailActivity.this.runOnUiThread(new Runnable() {
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
            if (success && !mReview.toString().equals("{}")) {
                mReviewed = true;
            }
            System.out.println("reviewed: " + mReviewed + " - " + mReview);
            updateButton();
        }
    }
}
