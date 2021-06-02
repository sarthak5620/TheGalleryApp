package com.example.android.thegalleryapp;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RedirectedURL extends AsyncTask<String, Void, String> {
    private OnCompleteListener listener;

/*
*
* Fetch the redirected url through a custom listener
 */
    public RedirectedURL fetchRedirectedURL(OnCompleteListener listener1){
        listener=listener1;
        return this;
    }

/*
*
* Execute the method on getting url
*/
    @Override
    protected void onPostExecute(String s) {
        listener.onFetched(s);
    }

    /*
     *
     *  Send the request using doInBackground method
     */
    @Override
    protected String doInBackground(String... strings) {
        return getRedirectUrl(strings[0]);
    }

    /*
    *
    * To get the url getRedirected url method used and handling exceptions
     */
    private String getRedirectUrl(String url){
        URL uTemp = null;
        String redUrl;
        HttpURLConnection connection = null;

        try{
            uTemp = new URL(url);
        } catch (MalformedURLException exp){
            exp.printStackTrace();
        }

        try{
            connection = (HttpURLConnection) uTemp.openConnection();
        } catch (IOException e){
            e.printStackTrace();
        }

        try{
            connection.getResponseCode();
        } catch (IOException e){
            e.printStackTrace();
        }

        redUrl = connection.getURL().toString();
        connection.disconnect();

        return redUrl;

    }

    /*
     **
     * method to implement of interface
     */
    interface OnCompleteListener{
        void onFetched(String redirectedUrl);
    }
}
