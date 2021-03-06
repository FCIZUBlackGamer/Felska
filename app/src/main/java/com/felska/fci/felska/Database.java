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

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Login";

    private static final String TABLE_NAME = "TABLE_Local";

    private static final String UID = "id";

    private static final String USER_ID = "user_id";

    private static final String EMAIL = "email";

    private static final String STATE = "state";

    private static final int DATABASE_VERSION = 2;
    Context cont;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table " +TABLE_NAME +
            "( "+UID+" integer primary key , "+USER_ID+" varchar(255) not null, "+EMAIL+" varchar(20) not null , "+STATE+" varchar(20) not null );";

    // Database Deletion
    private static final String DATABASE_DROP = "drop table if exists "+TABLE_NAME+";";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.cont = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(DATABASE_CREATE);
            db.execSQL("insert into "+TABLE_NAME+" ( "+UID+", "+USER_ID+", "+EMAIL+", "+STATE+" ) values ( '1', 't', 't', '0');");
            Toast.makeText(cont,"database created", Toast.LENGTH_SHORT).show();
        }catch (SQLException e)
        {
            Toast.makeText(cont,"database doesn't created " +e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            db.execSQL(DATABASE_DROP);
            onCreate(db);
            Toast.makeText(cont,"database upgraded", Toast.LENGTH_SHORT).show();
        }catch (SQLException e)
        {
            Toast.makeText(cont,"database doesn't upgraded " +e.toString(), Toast.LENGTH_SHORT).show();
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

    public Cursor ShowData ()
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from "+TABLE_NAME+" ;",null);
        return cursor;
    }

    public boolean UpdateData (String id, String user_id, String email, String state)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UID,id);
        contentValues.put(USER_ID,user_id);
        contentValues.put(EMAIL,email);
        contentValues.put(STATE,state);
        sqLiteDatabase.update(TABLE_NAME,contentValues,"id = "+Integer.parseInt( id ),null);

        return true;
    }

    public int DeleteData (String id)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(TABLE_NAME,"ID = ?",new String[] {id});
    }

}
