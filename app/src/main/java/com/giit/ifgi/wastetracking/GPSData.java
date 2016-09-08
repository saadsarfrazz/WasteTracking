package com.giit.ifgi.wastetracking;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by SAAD on 9/6/2016.
 */
public class GPSData {

    private static final String TAG = "GPSData";

    private Double latitude;
    private Double longitude;
    private Double height;
    private String time;

    private int phoneid;

    HttpPost httppost = new HttpPost("http://projects.gi-at-school.de/waste-tracking/insert.php");
    HttpClient httpclient = new DefaultHttpClient();



    GPSData(double lat, double lon, double ht, String t,int ph_id){
        latitude    =   lat;
        longitude   =   lon;
        height      =   ht;
        time        =   t;
        phoneid     =   ph_id;
    }

    public void updateDataToServer() throws IOException {
        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>(4);



        postParameters.add(new BasicNameValuePair("longitude", longitude.toString()));
        postParameters.add(new BasicNameValuePair("latitude", latitude.toString()));
        postParameters.add(new BasicNameValuePair("time", time));
        postParameters.add(new BasicNameValuePair("device", ""+phoneid));
        Log.d(TAG,"Uploading time is "+time);
        httppost.setEntity(new UrlEncodedFormEntity(postParameters));
        // Execute HTTP Post Request
        HttpResponse response = httpclient.execute(httppost);
        Log.d(TAG,"Entry Uploaded on Server");
    }
}
