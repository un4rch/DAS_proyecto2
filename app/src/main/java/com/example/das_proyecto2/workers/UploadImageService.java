package com.example.das_proyecto2.workers;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class UploadImageService extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        String apiUrl = params[0]; // URL of the server API
        String email = params[1];
        //String password = params[2];
        String imagePath = params[2]; // Path to the image file
        return postImage(apiUrl, email, imagePath);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d("UploadImageService", "Response from server: " + result);
    }

    private String postImage(String urlString, String email, String imagePath) {
        String boundary = UUID.randomUUID().toString();
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                // Write email and password fields
                writeFormField(outputStream, "email", email, boundary);

                // Write image file
                writeFileField(outputStream, "image", imagePath, "image/jpeg", boundary);

                // End of multipart/form-data
                outputStream.writeBytes("--" + boundary + "--\r\n");
                outputStream.flush();
            }

            // Read the response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // Success
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                return "HTTP error code: " + responseCode;
            }
        } catch (Exception e) {
            Log.e("UploadImageService", "Exception in postImage", e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void writeFormField(DataOutputStream outputStream, String name, String value, String boundary) throws Exception {
        outputStream.writeBytes("--" + boundary + "\r\n");
        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n");
        outputStream.writeBytes(value + "\r\n");
    }

    private void writeFileField(DataOutputStream outputStream, String fieldName, String filePath, String fileType, String boundary) throws Exception {
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        outputStream.writeBytes("--" + boundary + "\r\n");
        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + file.getName() + "\"\r\n");
        outputStream.writeBytes("Content-Type: " + fileType + "\r\n\r\n");

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        fileInputStream.close();
        outputStream.writeBytes("\r\n");
    }
}
