package com.csse333.mealmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class PostReviewActivity extends Activity {
    String mRestID;
    String mEmail;
    boolean hasLeftReview;
    JSONObject mReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_review);

        mRestID = getIntent().getExtras().getString("rest_id");
        mEmail = getIntent().getExtras().getString("user_id");
        hasLeftReview = "edit".equals(getIntent().getExtras().getString("type"));
        if (hasLeftReview) {
            try {
                if (getIntent().getExtras().getString("review") != null) {
                    mReview = new JSONObject(getIntent().getExtras().getString("review"));
                    mReview = new JSONObject(mReview.get("reviews").toString());
                    System.out.println(mReview.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        final RatingBar ratingBar = (RatingBar) findViewById(R.id.post_review_ratingBar);
        final EditText reviewText = (EditText) findViewById(R.id.post_review_editText);
        Button postButton = (Button) findViewById(R.id.post_review_button);

        if (hasLeftReview) {
            try {
                ratingBar.setRating(mReview.getInt("rating"));
                reviewText.setText(mReview.getString("content"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            postButton.setText("Edit Review");
        }
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rating = (int) ratingBar.getRating();
                String review = String.valueOf(reviewText.getText());
                if (hasLeftReview) {
                    new UpdateReviewTask(rating, review).execute();
                } else {
                    new PostReviewTask(rating, review).execute();
                }
            }
        });
    }

    private boolean printStatusMessage(int status) {
        CharSequence text;
        switch (status) {
            case 601:
                text = "You have already left a review for this restaurant";
                break;
            case 602:
                text = "An error occurred in the database";
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
        Toast.makeText(PostReviewActivity.this, text, Toast.LENGTH_SHORT).show();

        System.out.println(status);
        return (status != 200);
    }

    public class UpdateReviewTask extends AsyncTask<Void, Void, Boolean> {

        private int rating;
        private String content;

        UpdateReviewTask(int rating, String content) {
            this.rating = rating;
            this.content = content;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String charset = "UTF-8";
            String query = "";
            try {
                query = String.format("Review?email=%s&rest_id=%s&rating=%s&content=%s",
                        URLEncoder.encode(mEmail, charset),
                        URLEncoder.encode(""+mRestID, charset),
                        URLEncoder.encode(""+rating, charset),
                        URLEncoder.encode(content, charset));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            final ServerConnections sc = new ServerConnections();
            JSONObject jo = sc.updateRequest(query);
            if (jo == null) {
                PostReviewActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        printStatusMessage(sc.getStatusCode());
                    }
                });
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                CharSequence text = "Review updated!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                toast.show();
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    public class PostReviewTask extends AsyncTask<Void, Void, Boolean> {

        private int rating;
        private String content;

        PostReviewTask(int rating, String content) {
            this.rating = rating;
            this.content = content;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String charset = "UTF-8";
            String query = "";
            try {
                query = String.format("Review?email=%s&rest_id=%s&rating=%s&content=%s",
                        URLEncoder.encode(mEmail, charset),
                        URLEncoder.encode(""+mRestID, charset),
                        URLEncoder.encode(""+rating, charset),
                        URLEncoder.encode(content, charset));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            final ServerConnections sc = new ServerConnections();
            JSONObject jo = sc.postRequest(query);
            if (jo == null) {
                PostReviewActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        printStatusMessage(sc.getStatusCode());
                    }
                });
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                CharSequence text = "Review added!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                toast.show();
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}
