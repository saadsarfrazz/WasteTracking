package com.giit.ifgi.wastetracking;

import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

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
    private int accuracy;

    private int phoneid;
    public int response_nu;

    HttpPost httppost = new HttpPost("http://projects.gi-at-school.de/waste-tracking/insert.php");
    HttpClient httpclient = new DefaultHttpClient();



    GPSData(double lat, double lon, double ht, String t,int ph_id,int acc){
        latitude    =   lat;
        longitude   =   lon;
        height      =   ht;
        time        =   t;
        phoneid     =   ph_id;
        accuracy    =   acc;
    }

    public void updateDataToServer() throws IOException {
//        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>(4);
//
//
//
//        postParameters.add(new BasicNameValuePair("longitude", longitude.toString()));
//        postParameters.add(new BasicNameValuePair("latitude", latitude.toString()));
//        postParameters.add(new BasicNameValuePair("time", time));
//        postParameters.add(new BasicNameValuePair("device", ""+phoneid));
//        Log.d(TAG,"Uploading time is "+time);
//        httppost.setEntity(new UrlEncodedFormEntity(postParameters));
//        // Execute HTTP Post Request
//        HttpResponse response = httpclient.execute(httppost);
//        response_nu= response.getStatusLine().getStatusCode();
//        Log.d(TAG,"Response is "+response_nu);

        JSONObject json = new JSONObject();

        try {
            json.put("longitude", longitude.toString());
            json.put("latitude", latitude.toString());
            json.put("time", time);
            json.put("device",phoneid);
            json.put("accu", accuracy);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        StringEntity se = new StringEntity(json.toString());
        httppost.setEntity(se);
        HttpResponse response = httpclient.execute(httppost);
        response_nu= response.getStatusLine().getStatusCode();
        Log.d(TAG,"Response is "+response_nu);

    }
}
