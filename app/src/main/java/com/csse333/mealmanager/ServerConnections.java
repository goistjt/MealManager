package com.csse333.mealmanager;

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
    int statusCode;

    public JSONObject postRequest(String query) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url + query).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept-Charset", charset);

            this.statusCode = connection.getResponseCode();
            if (this.statusCode != 200) {
                return null;
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

            this.statusCode = connection.getResponseCode();
            if (this.statusCode != 200) {
                return null;
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

    public boolean deleteRequest(String query) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url + query).openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Accept-Charset", charset);

            this.statusCode = connection.getResponseCode();
            if (this.statusCode != 200) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

}
