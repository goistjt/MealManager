package com.csse333.mealmanager;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONObject;


public class AddIngredientActivity extends Activity {
    String mEmail;
    JSONObject mReturnedJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingr);
        addActionBar(getActionBar());

        mEmail = getIntent().getExtras().getString("user_id");

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
                Intent logOutIntent = new Intent(AddIngredientActivity.this, LoginActivity.class);
                ComponentName cn = logOutIntent.getComponent();
                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                startActivity(mainIntent);
                finish();
            }
        });

        findViewById(R.id.menu_search_layout).setVisibility(View.GONE);
        findViewById(R.id.menu_item_clear_shopping_list).setVisibility(View.GONE);
    }
}
