package com.giit.ifgi.wastetracking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.sql.Date;
import java.util.UUID;

/**
 * Created by SAAD on 9/6/2016.
 */
public class DatabaseHelper {

    private static final String TAG= "DatabaseHelper"; //To output debugging info

    private static final String APP_ID = "ID";
    private static final String LATITUDE = "Latitude";
    private static final String LONGITUDE = "Longitude";
    private static final String HEIGHT  =   "Height";
    private static final String CREATED_AT = "Cr_time"; //Time at which coordinates were collected

    private static  String App_ID_Value; //Will be assigned once to an application only

    private static final String DATABASE_NAME = "TrackingData";
    private static final String TABLE_NAME = "TABLE_1";
    private static final int DATABASE_VERSION = 1;

    private static int rowId;   //To delete entries from db when uploaded

    private DBHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;


    private static class DBHelper extends SQLiteOpenHelper{


        /**
         * Create a helper object to create, open, and/or manage a database.
         * This method always returns very quickly.  The database is not actually
         * created or opened until one of {@link #getWritableDatabase} or
         * {@link #getReadableDatabase} is called.
         *
         * @param context to use to open or create the database
         */
        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            createDatabaseTable(sqLiteDatabase);
        }

        private void createDatabaseTable(SQLiteDatabase db) {

            rowId=0; //init rowid -PK for database

            String query = "CREATE TABLE " + TABLE_NAME + "(" +"UID INTEGER PRIMARY KEY, " +
                    APP_ID + " TEXT NOT NULL, " +
                     CREATED_AT + " DATETIME, " + LATITUDE + " DOUBLE, " + LONGITUDE + " DOUBLE, " +
                     HEIGHT + " DOUBLE NOT NULL);";
            Log.d("Msg table Created ", query);
            db.execSQL(query);
            Log.d(TAG,"Table Created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }   //inner class DBHelper ends here


    /**
     * @param c
     */
    public DatabaseHelper(Context c) {

        ourContext = c;
    }

    /**
     * Open DB
     *
     * @return
     * @throws SQLException
     */
    public DatabaseHelper open() throws SQLException {

        try {
            ourHelper = new DBHelper(ourContext);

            ourDatabase = ourHelper.getWritableDatabase();


        } catch (Exception e) {
            Log.d(TAG, " DBHelper Open error");

        }
        return this;
    }

    /**
     * Close DB
     */
    public void close() {
        try {
            ourHelper.close();
            Log.d("Database closed ", "no exception raised");
        } catch (Exception e) {
            Log.d("Database  not closed ", "exception raised");

        }
    }

    /**
     * Stores single database entry for each pair of coordinate
     * @param lat
     * @param lon
     * @param ht
     */
    public void createEntry( double lat, double lon,double ht ,String time,int ph_id){
        try{
            ContentValues cv= new ContentValues();
            cv.put("UID",rowId);
            rowId++; //increment for next entry
            cv.put(APP_ID,ph_id);
            cv.put(LATITUDE,lat);
            cv.put(LONGITUDE,lon);
            cv.put(HEIGHT,ht);
            cv.put(CREATED_AT,time);
            ourDatabase.insert(TABLE_NAME,null,cv);
            Log.d(TAG, "DB entry successfull :rid "+ rowId);
            Toast.makeText(ourContext, "Entry saved with UID: "+rowId, Toast.LENGTH_SHORT).show();

        }catch(Exception ex){
            Log.d(TAG, "DB entry fail");
        }

    }

    public void updateDataToServer() throws IOException {
        Log.d(TAG,"Updating data to server");
        Cursor res = ourDatabase.rawQuery("select * from TABLE_1", null);

        res.moveToFirst();
        while (res.isAfterLast()==false) {
            Log.d(TAG,"In while loop");
            if (res.getString(res.getColumnIndex(LATITUDE)) != null) {
                int uid = res.getInt(res.getColumnIndex("UID"));
                int m_id = res.getInt(res.getColumnIndex(APP_ID));
                Double lat = Double.parseDouble(res.getString(res.getColumnIndex(LATITUDE)));
                Double lon = Double.parseDouble(res.getString(res.getColumnIndex(LONGITUDE)));
                Double ht = Double.parseDouble(res.getString(res.getColumnIndex(HEIGHT)));
                String dt = res.getString(res.getColumnIndex(CREATED_AT));
                GPSData data = new GPSData(lat, lon, ht, dt,m_id);
                data.updateDataToServer();
                res.moveToNext();

                //If succeeded then delete entry from db to avoid sending same entries again
                String sql_query1 = "Delete from TABLE_1 where "+"UID"+  " = '" + uid + "'";
                ourDatabase.execSQL(sql_query1);


            }
        }
    }

    public void deleteAllRows(){
        String sql_query1 = "Delete from TABLE_1";
        ourDatabase.execSQL(sql_query1);

        Log.d(TAG, "Entries deleted");
    }


}
