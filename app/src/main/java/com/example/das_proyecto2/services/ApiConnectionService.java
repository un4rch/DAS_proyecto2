package com.example.das_proyecto2.services;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiConnectionService extends Worker {

    public static final String TAG = "HttpWorker";
    private static final String API_URL = "http://34.136.205.220:8000/create_user";

    public ApiConnectionService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        HttpURLConnection urlConnection = null;
        try {
            // Create URL object
            URL url = new URL(API_URL);
            // Create HttpURLConnection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            //urlConnection.connect();
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //Preparar los parametros
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("email", "example");
            String parametros = builder.build().getEncodedQuery();

            // Se incluyen los parámetros en la petición HTTP
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

            // Check if the request was successful (status code 200)
            int responseCode = urlConnection.getResponseCode();
            String line, result = "";
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                inputStream.close();
            } else {
                // Request failed
                Log.e(TAG, "HTTP request failed with response code: " + responseCode);
                return Result.failure();
            }
            Data resultados = new Data.Builder()
                    .putString("datos", result)
                    .build();

            // Devolver que todo ha ido bien
            return Result.success(resultados);
        } catch (Exception e) {
            // Se imprime la excepcion
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return Result.failure();
    }
}
