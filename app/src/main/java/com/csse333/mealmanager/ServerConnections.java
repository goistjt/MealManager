package com.csse333.mealmanager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ServerConnections {
    String url = "http://meal-manager.csse.rose-hulman.edu/";
    String charset = "UTF-8";

    public JSONObject postRequest(String query) {
        URLConnection connection = null;
        try {
            connection = new URL(url).openConnection();
            connection.setDoOutput(true); // Triggers POST.
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            try (OutputStream output = connection.getOutputStream()) {
                output.write(query.getBytes(charset));
            }

            HttpURLConnection conn = (HttpURLConnection) connection;
            int status = conn.getResponseCode();
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

    public JSONObject getRequest(String query) {
        HttpURLConnection connection = null;
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
        // TODO: Fill in error display
        System.out.println(status);
        return (status != 200);
    }
}
