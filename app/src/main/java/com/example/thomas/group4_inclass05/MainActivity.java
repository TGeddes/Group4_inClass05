package com.example.thomas.group4_inclass05;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ScrollView scrollView = findViewById(R.id.sView);

        if(isConnected()){
            Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
            new GetDataAsync().execute("https://newsapi.org/v2/sources?apiKey=53d845dc510b4069b2affea39f142fc7");
        }
        else
            Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();

    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    private class GetDataAsync extends AsyncTask<String, Void, ArrayList<News>> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected ArrayList<News> doInBackground(String... params) {
            HttpURLConnection connection = null;
            ArrayList<News> result = new ArrayList<>();

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    JSONObject root = new JSONObject(json);
                    JSONArray news = root.getJSONArray("sources");

                    for(int i = 0; i < news.length(); i++){
                        JSONObject newsJson = news.getJSONObject(i);
                        News news1 = new News();
                        news1.id = newsJson.getString("id");
                        news1.name = newsJson.getString("name");
                        news1.description = newsJson.getString("description");
                        news1.url = newsJson.getString("url");
                        news1.category = newsJson.getString("category");
                        news1.language = newsJson.getString("language");
                        news1.country = newsJson.getString("country");

                        JSONObject logosJson = newsJson.getJSONObject("urlsToLogos");
                        UrlLogos logos = new UrlLogos();
                        logos.small = logosJson.getString("small");
                        logos.medium = logosJson.getString("medium");
                        logos.large = logosJson.getString("large");
                        news1.logos = logos;

                        JSONObject sortJson = newsJson.getJSONObject("sortsBysAvailable");
                        SortAvailable sort = new SortAvailable();
                        sort.top = sortJson.getString("top");
                        news1.available = sort;

                        result.add(news1);

                    }
                }
        } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finally {
                if (connection != null){
                    connection.disconnect();
                }
            }
            return  result;
            }

        @Override
        protected void onPostExecute(ArrayList<News> result) {
            if(result.size() > 0){
                Log.d("demo", result.toString());
            }
            else{
                Log.d("demo", "empty result");
            }

            dialog.dismiss();
        }
    }

}
