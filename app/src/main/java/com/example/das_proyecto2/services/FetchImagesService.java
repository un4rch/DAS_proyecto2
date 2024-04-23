package com.example.das_proyecto2.services;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchImagesService extends AsyncTask<Void, Void, JSONArray> {

    private static final String TAG = "FetchImagesTask";
    private static final String REQUEST_URL = "http://34.136.205.220:8000/get_imgs";
    private OnImagesFetchedListener listener;

    public FetchImagesService(OnImagesFetchedListener listener) {
        this.listener = listener;
    }

    public interface OnImagesFetchedListener {
        void onImagesFetched(JSONArray images);
        void onError(String error);
    }

    @Override
    protected JSONArray doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        int retries = 20;  // Set the number of retries

        while (retries > 0) {
            try {
                URL url = new URL(REQUEST_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Connection", "close");
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();

                StringBuilder buffer = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null; // Empty stream, no parsing needed.
                }

                return new JSONArray(buffer.toString());
            } catch (java.net.ProtocolException pe) {
                retries--;
                if (retries == 0) {
                    Log.e(TAG, "ProtocolException on last attempt", pe);
                    return null;
                }
                Log.e(TAG, "ProtocolException, retrying...", pe);
            } catch (Exception e) {
                Log.e(TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
        }
        return null;
    }


    /*@Override
    protected void onPostExecute(JSONArray jsonResponse) {
        if (jsonResponse != null) {
            try {
                for (int i = 0; i < jsonResponse.length(); i++) {
                    JSONObject imageInfo = jsonResponse.getJSONObject(i);
                    String email = imageInfo.getString("email");
                    String filename = imageInfo.getString("filename");
                    int likes = imageInfo.getInt("likes");

                    // Here you could update your UI or handle the data as needed
                    Log.i(TAG, "Email: " + email + ", Filename: " + filename + ", Likes: " + likes);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing JSON", e);
            }
        } else {
            Log.e(TAG, "Received empty response");
        }
    }*/

    @Override
    protected void onPostExecute(JSONArray jsonResponse) {
        if (jsonResponse != null) {
            if (listener != null) {
                listener.onImagesFetched(jsonResponse);
            }
        } else {
            if (listener != null) {
                listener.onError("Received empty response or error during fetch.");
            }
        }
    }

}

