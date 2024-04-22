package com.example.das_proyecto2.workers;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CreateUserService extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        String apiUrl = params[0]; // URL of the Python API
        String email = params[1];
        String password = params[2];
        return postRequest(apiUrl, email, password);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // Handle the response here if necessary
        Log.d("ApiService", "Response from server: " + result);
    }

    private String postRequest(String urlString, String email, String password) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            // Create the data
            String jsonInputString = "{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}";

            // Send the POST out
            try (DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream())) {
                os.write(jsonInputString.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            // Read the response
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // Success
                InputStream is = urlConnection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                }
                rd.close();
                return response.toString();
            } else {
                return "HTTP error code: " + responseCode;
            }
        } catch (Exception e) {
            Log.e("ApiService", "Exception in postRequest", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
