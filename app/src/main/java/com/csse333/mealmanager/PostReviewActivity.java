package com.csse333.mealmanager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

public class PostReviewActivity extends Activity {
    int mRestID;
    String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_review);

        mRestID = getIntent().getExtras().getInt("rest_id");
        mEmail = getIntent().getExtras().getString("user_id");

        final RatingBar ratingBar = (RatingBar) findViewById(R.id.post_review_ratingBar);
        final EditText reviewText = (EditText) findViewById(R.id.post_review_editText);
        Button postButton = (Button) findViewById(R.id.post_review_button);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rating = ratingBar.getNumStars();
                String review = String.valueOf(reviewText.getText());
                //TODO: Implement server call to post review then route back to home screen
            }
        });


    }
}
