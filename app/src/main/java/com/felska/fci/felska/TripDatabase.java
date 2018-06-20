package com.felska.fci.felska;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by fci on 11/03/17.
 */

public class TripDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Trip";

    private static final String TABLE_NAME = "TABLE_Local";

    private static final String TID = "id";

    private static final String TRIP_TYPE = "trip_type";

    private static final String LOCATION_FROM = "location_from";

    private static final String LOCATION_FROM_DETAILS = "location_from_details";

    private static final String LOCATION_TO = "location_to";

    private static final String LOCATION_TO_DETAILS = "location_to_details";

    private static final String TIME_START = "time_start";

    private static final String TIME_END = "time_end";

    private static final String CAR_NAME = "car_name";

    private static final String CAR_NUM = "car_num";

    private static final String OTHER_DETAILS = "other_details";

    private static final String MONEY_FROM = "money_from";

    private static final String MONEY_TO = "money_to";

    private static final int DATABASE_VERSION = 2;
    Context cont;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table " + TABLE_NAME +
            "( " + TID + " integer primary key , " + TRIP_TYPE + " varchar(255) not null, "
            + LOCATION_FROM + " varchar(225) not null , " + LOCATION_FROM_DETAILS + " varchar(225) not null , "
            + LOCATION_TO + " varchar(255) not null, " + LOCATION_TO_DETAILS + " varchar(225) not null , " +
            TIME_START + " varchar(225) not null, " + TIME_END + " varchar(225) not null, " + CAR_NAME
            + " varchar(225) not null, " + CAR_NUM + " varchar(225) not null, "
            + OTHER_DETAILS + " varchar(225) not null, " + MONEY_FROM + " varchar(225) not null, " +
            MONEY_TO + " varchar(225) not null );";

    // Database Deletion
    private static final String DATABASE_DROP = "drop table if exists " + TABLE_NAME + ";";

    public TripDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.cont = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(DATABASE_CREATE);
            db.execSQL("insert into " + TABLE_NAME + " ( " + TID + ", " + TRIP_TYPE + ", " + LOCATION_FROM + ", " +
                    LOCATION_FROM_DETAILS + ", " + LOCATION_TO + ", " +
                    LOCATION_TO_DETAILS + ", " + TIME_START + ", " + TIME_END
                    + ", " + CAR_NAME + ", " + CAR_NUM + ", " + OTHER_DETAILS + ", " + MONEY_FROM + ", " + MONEY_TO + " ) values ( '1', 't', 't', '0','1', 't', 't', '0','1', 't', 't', '0', '0');");
            Toast.makeText(cont, "database created", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            Toast.makeText(cont, "database doesn't created " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            db.execSQL(DATABASE_DROP);
            onCreate(db);
            Toast.makeText(cont, "database upgraded", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            Toast.makeText(cont, "database doesn't upgraded " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

//    public boolean InsertData (String name, String pass ,String type)
//    {
//        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(NAME,name);
//        contentValues.put(PASSWORD,pass);
//        contentValues.put(TYPE,type);
//        long result = sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
//
//        return result==-1?false:true;
//    }

    public Cursor ShowData() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME + " ;", null);
        return cursor;
    }

    public boolean Updatetrip_type(String id, String trip_type) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TID, id);
        contentValues.put(TRIP_TYPE, trip_type);
        sqLiteDatabase.update(TABLE_NAME, contentValues, "id = " + Integer.parseInt(id), null);

        return true;
    }

    public boolean Updatefrom_to(String id, String from_to_from_title, String from_to_from_desc, String from_to_to_title, String from_to_to_desc) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TID, id);
        contentValues.put(LOCATION_FROM, from_to_from_title);
        contentValues.put(LOCATION_FROM_DETAILS, from_to_from_desc);
        contentValues.put(LOCATION_TO, from_to_to_title);
        contentValues.put(LOCATION_TO_DETAILS, from_to_to_desc);
        sqLiteDatabase.update(TABLE_NAME, contentValues, "id = " + Integer.parseInt(id), null);

        return true;
    }

    public boolean Updatetime(String id, String start_time, String end_time) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TID, id);
        contentValues.put(TIME_START, start_time);
        contentValues.put(TIME_END, end_time);
        sqLiteDatabase.update(TABLE_NAME, contentValues, "id = " + Integer.parseInt(id), null);

        return true;
    }

    public boolean Updatedetails(String id, String car_name, String car_num, String details) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TID, id);
        contentValues.put(CAR_NAME, car_name);
        contentValues.put(CAR_NUM, car_num);
        contentValues.put(OTHER_DETAILS, details);
        sqLiteDatabase.update(TABLE_NAME, contentValues, "id = " + Integer.parseInt(id), null);

        return true;
    }

    public boolean Updatepayment(String id, String money_from, String money_to) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TID, id);
        contentValues.put(MONEY_FROM, money_from);
        contentValues.put(MONEY_TO, money_to);
        sqLiteDatabase.update(TABLE_NAME, contentValues, "id = " + Integer.parseInt(id), null);

        return true;
    }

    public int DeleteData(String id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(TABLE_NAME, "ID = ?", new String[]{id});
    }

}
