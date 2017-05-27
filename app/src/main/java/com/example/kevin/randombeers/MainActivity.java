package com.example.kevin.randombeers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        new BeerInfoRetrievalTask().execute("http://api.brewerydb.com/v2/beer/random?key=cc83335205f3ba0857a4e8d335ff7050");
    }

    //checks to see if there is a label in JSON, gets it if there is and sends it to async label task or if there is none applies not found image
    public void getLabel(String result)
    {
        ImageView labelView = (ImageView) findViewById(R.id.imageView_label);
        try
        {
            JSONObject obj = new JSONObject(result);
            JSONObject subObj = obj.getJSONObject("data");
            if (!subObj.isNull("labels"))
            {
                String labelURL = obj.getJSONObject("data").getJSONObject("labels").optString("large");
                try
                {
                    new labelTask().execute(labelURL);
                }
                catch (Exception e)
                {
                    Log.e("IMAGE ERROR", e.toString());
                }
            }
            else
            {
                labelView.setImageDrawable(getDrawable(R.drawable.not_found));
                labelView.setVisibility(View.VISIBLE);
            }
        }
        catch (JSONException e)
        {
            Log.e("JSON_EXCEPTION", e.toString());
        }
    }

    //async task to make API call and process json
    private class BeerInfoRetrievalTask extends AsyncTask<String, Void, String >{
        ProgressBar p = (ProgressBar) findViewById(R.id.progressBar) ;
        TextView t =(TextView) findViewById(R.id.responseView);
        TextView title = (TextView) findViewById(R.id.textView);
        ImageView labelView = (ImageView) findViewById(R.id.imageView_label);
        protected void onPreExecute(){
            p.setVisibility(View.VISIBLE);
            labelView.setVisibility(View.INVISIBLE);
            t.setText("");
            title.setText("");
        }

        protected String doInBackground(String... urls){
            HttpURLConnection connection = null;
            try
            {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader in = new BufferedReader( new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = in.readLine()) != null)
                {
                    sb.append(line).append('\n');
                }
                int status = connection.getResponseCode();
                if (status != 200)
                {
                    throw new IOException("Post failed with error code " + status);
                }
                in.close();
                return sb.toString();
            }
            catch (Exception e)
            {
                Log.e("ERROR", e.toString());
                return null;
            }
            finally {
                if(connection != null)
                {
                    connection.disconnect();
                }
            }
        }

        protected void onPostExecute(String result){
            try
            {
                JSONObject obj = new JSONObject(result);
                JSONObject subObj = obj.getJSONObject("data");
                String BeerName = subObj.getString("name");
                getLabel(result);
                //description sometimes doesn't exist, and sometimes its embedded within the JSON, so checks to find it
                if(subObj.isNull("description"))
                {
                    if(!subObj.isNull("style"))
                    {
                        if(subObj.getJSONObject("style").isNull("description"))
                        {
                            t.setText(String.format(getString(R.string.no_description)));
                        }
                        else
                        {
                            t.setText(subObj.getJSONObject("style").optString("description"));
                        }
                    }
                }
                else
                {
                    t.setText(subObj.optString("description"));
                }
                title.setText(BeerName);
                p.setVisibility(View.GONE);
            }
            catch (Exception e)
            {
                Log.e("ERROR", e.toString());
            }
        }
    }

    //async task for retrieving the beer label
    private class labelTask extends AsyncTask<String, Void, Bitmap> {
        ImageView labelView = (ImageView) findViewById(R.id.imageView_label);
        protected Bitmap doInBackground(String... urls) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                Log.e("LABEL_ERROR", e.toString());
                return null;
            }
        }
        protected void onPostExecute(Bitmap result) {
            labelView.setVisibility(View.VISIBLE);
            labelView.setImageBitmap(result);
        }
    }
}


