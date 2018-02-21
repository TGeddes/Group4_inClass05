package com.example.thomas.group4_inclass05;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

public class Article_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if(isConnected()){
            Toast.makeText(Article_Activity.this, "Connected", Toast.LENGTH_SHORT).show();
            new GetDataAsync().execute("https://newsapi.org/v2/articles?apiKey=53d845dc510b4069b2affea39f142fc7");
        }
        else
            Toast.makeText(Article_Activity.this, "Disconnected", Toast.LENGTH_SHORT).show();

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

    private class GetDataAsync extends AsyncTask<String, Void, ArrayList<Articles>> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(Article_Activity.this);
            dialog.setMessage("Loading Articles...");
            dialog.show();
        }

        protected ArrayList<Articles> doInBackground(String... params) {
            HttpURLConnection connection = null;
            ArrayList<Articles> result = new ArrayList<>();

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    JSONObject root = new JSONObject(json);
                    JSONArray articles = root.getJSONArray("articles");

                    for(int i = 0; i < articles.length(); i++){
                        JSONObject articlesJSONObjectJson = articles.getJSONObject(i);
                        Articles article = new Articles();
                        article.author = articlesJSONObjectJson.getString("author");
                        article.title = articlesJSONObjectJson.getString("title");
                        article.description = articlesJSONObjectJson.getString("description");
                        article.url = articlesJSONObjectJson.getString("url");
                        article.urlImage = articlesJSONObjectJson.getString("urlToImage");
                        article.publishDate = articlesJSONObjectJson.getString("publishedAt");

                        result.add(article);

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
        protected void onPostExecute(ArrayList<Articles> result) {
            if(result.size() > 0)
                Log.d("demo", result.toString());
            else
                Log.d("demo", "empty result");

            dialog.dismiss();
        }
    }
}