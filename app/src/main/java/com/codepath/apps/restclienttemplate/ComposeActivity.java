package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    public final static String TAG = "ComposeActivity";
    TwitterClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApp.getRestClient(this);
        // use client to send requests to API
        setContentView(R.layout.activity_compose);

    }

    public void onClose(View v){
        Intent data = new Intent();
        setResult(RESULT_CANCELED, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
    }

    public void onSendTweet(View v) {

        //resolve text entered
        EditText composedTweet = (EditText) findViewById(R.id.etCompose);
        //network request to statuses/update endpoint
        client.sendTweet(composedTweet.getText().toString(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Tweet newTweet = Tweet.fromJSON(response);
                    Intent data = new Intent();
                    // Pass relevant data back as a result
                    data.putExtra("tweet", Parcels.wrap(newTweet));
                    // Activity finished ok, return the data
                    setResult(RESULT_OK, data); // set result code and bundle data for response
                    finish(); // closes the activity, pass data to parent
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                logError("failed to send tweet", throwable, true);
            }
        });

        // Prepare data intent


    }

    //handle errors, log and alert
    private void logError(String message, Throwable error, boolean alertUser) {
        //always log error
        Log.e(TAG, message, error);
        //alert user to avoid silent errors
        if (alertUser) {
            //show long toast with error message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        }
    };
}
