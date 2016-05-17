package com.csse333.mealmanager;

import android.app.Activity;
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

public class AddRecipeActivity extends Activity {
    String mEmail;
    JSONObject mReturnedJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        mEmail = getIntent().getExtras().getString("user_id");
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
        Toast.makeText(AddRecipeActivity.this, text, Toast.LENGTH_SHORT).show();

        System.out.println(status);
        return (status != 200);
    }

}
