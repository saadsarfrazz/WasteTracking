package com.giit.ifgi.wastetracking;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by SAAD on 9/6/2016.
 */
public class LocationService extends Service {

    String TAG = "LocationService";

    private DatabaseHelper dbH;

    private static int PhoneID;

    protected LocationManager locationManager;
    private LocationListener locL;
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    /**
     * Starting point when service starts
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        locL = new MyLocationListener();

        //Setting Location Listeners
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATES,
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                locL
        );

        dbH = new DatabaseHelper(getApplicationContext());
        dbH.open();//to create database first time
        dbH.close();

        //Init PhoneID
        PhoneID = intent.getIntExtra("PhoneID",0);
        Log.d(TAG,"Phone id is "+PhoneID);



        //        Testing Database here
//        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
//        db.open();
//        db.createEntry(23.34,45.55,2300.3, "2016-06-09 2:00:00");

//        try {
//            db.updateDataToServer();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        db.close();



//        Testing web connection



//        GPSData data = new GPSData(23.34,45.55,2300.3,"2016-07-09 00:00:00");
//
//        try {
//            data.updateDataToServer();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Log.d(TAG," Network Available: "+isNetworkAvailable());

//        try {
//            testServer();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return START_STICKY; //Used to refer to explicitly started service
    }

//    private void testServer() throws IOException {
//
//        HttpClient httpclient = new DefaultHttpClient();
//        HttpPost httppost = new HttpPost("http://projects.gi-at-school.de/waste-tracking/insert.php");
//
//        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>(4);
//
//
//        postParameters.add(new BasicNameValuePair("longitude", "22.222"));
//        postParameters.add(new BasicNameValuePair("latitude", "31.222"));
//        postParameters.add(new BasicNameValuePair("time", "2016-00-09 00:00:40"));
//        postParameters.add(new BasicNameValuePair("device", "3"));
//
//        httppost.setEntity(new UrlEncodedFormEntity(postParameters));
//
//        // Execute HTTP Post Request
//        HttpResponse response = httpclient.execute(httppost);
//
//    }

    /**
     * Method execute in last when service stops
     */
    public void onDestroy() {

        super.onDestroy();

        //Delete all Entries from Database
        dbH.open();
        dbH.deleteAllRows();
        dbH.close();

        //Stop listening to location listener
        locationManager.removeUpdates(locL);

        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();

    }

    private class MyLocationListener implements LocationListener {


        /**
         * This method run each time when there is change in location after mentioned frequency
         * @param location
         */
        public void onLocationChanged(Location location) {

            Log.d(TAG," Location changed");

            try {

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                double height= location.getAltitude();
                long time = location.getTime();
                Date date = new Date(time);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                sdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
                String formattedDate = sdf.format(date);

                if(isNetworkAvailable()){
                    //Send all stored data from db to server
                    //DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                    dbH.open();
                    dbH.updateDataToServer();
                    dbH.close();
                    //Sending current data to Server
                    Toast.makeText(LocationService.this, "Network Available for upload", Toast.LENGTH_SHORT).show();
                    GPSData data = new GPSData(latitude,longitude,height,formattedDate,PhoneID);
                    data.updateDataToServer();
                }else{
                    //Save data to database
                    Toast.makeText(LocationService.this, "NoNetwork:Saving locally", Toast.LENGTH_SHORT).show();
                   // DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                    dbH.open();
                    dbH.createEntry(latitude,longitude,height,formattedDate,PhoneID);
                    dbH.close();
                }

            }catch (IOException e) {
                // TODO Auto-generated catch block
            }

            String message = String.format(
                    "New Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );
            Log.d("LocationService: ",location.getLatitude()+" "+location.getLongitude());
            Toast.makeText(LocationService.this, message, Toast.LENGTH_SHORT).show();
        }

        public void onStatusChanged(String s, int i, Bundle b) {
            Toast.makeText(LocationService.this, "Provider status changed",
                    Toast.LENGTH_SHORT).show();
        }

        public void onProviderDisabled(String s) {
            Toast.makeText(LocationService.this,
                    "Provider disabled by the user. GPS turned off",
                    Toast.LENGTH_SHORT).show();
        }

        public void onProviderEnabled(String s) {
            Toast.makeText(LocationService.this,
                    "Provider enabled by the user. GPS turned on",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Check if mobile is connected to internet
     * @return
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

}
