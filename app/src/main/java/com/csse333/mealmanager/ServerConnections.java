package com.csse333.mealmanager;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerConnections {
    String url = "http://meal-manager.csse.rose-hulman.edu/";
    String charset = "UTF-8";
    Context context;

    public JSONObject postRequest(String query, Context context) {
        HttpURLConnection connection = null;
        this.context = context;
        try {
            connection = (HttpURLConnection) new URL(url + query).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept-Charset", charset);

            int status = connection.getResponseCode();
            if (printStatusMessage(status)) {
                return new JSONObject();
            }

            InputStream response = connection.getInputStream();
            BufferedReader bR = new BufferedReader(  new InputStreamReader(response));
            String line = "";

            StringBuilder responseStrBuilder = new StringBuilder();
            while((line =  bR.readLine()) != null){
                responseStrBuilder.append(line);
            }
            response.close();
            return new JSONObject(responseStrBuilder.toString());

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public JSONObject getRequest(String query, Context context) {
        HttpURLConnection connection = null;
        this.context = context;
        try {
            connection = (HttpURLConnection) new URL(url + query).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Charset", charset);

            int status = connection.getResponseCode();
            if (printStatusMessage(status)) {
                return new JSONObject();
            }

            InputStream response = connection.getInputStream();
            BufferedReader bR = new BufferedReader(  new InputStreamReader(response));
            String line = "";

            StringBuilder responseStrBuilder = new StringBuilder();
            while((line =  bR.readLine()) != null){
                responseStrBuilder.append(line);
            }
            response.close();
            return new JSONObject(responseStrBuilder.toString());

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
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
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this.context, text, duration);
        toast.show();

        System.out.println(status);
        return (status != 200);
    }
}
