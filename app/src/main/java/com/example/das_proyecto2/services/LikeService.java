package com.example.das_proyecto2.services;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LikeService extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        String filename = params[0];
        HttpURLConnection urlConnection = null;

        try {
            // URL of your Flask server
            URL url = new URL("http://34.136.205.220:8000/send_like");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);

            // Create JSON object to hold the filename
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("filename", filename);

            // Send the POST out
            OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
            os.write(jsonParam.toString().getBytes());
            os.flush();
            os.close();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }
                in.close();

                // Response from server
                return sb.toString();
            } else {
                return "Error: Failed to like the image";
            }

        } catch (Exception e) {
            Log.e("LikeService", "Error in sending like", e);
            return "Error: " + e.getMessage();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.i("LikeService", "Result: " + result);
    }
}

