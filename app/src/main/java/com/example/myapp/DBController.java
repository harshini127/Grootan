package com.example.myapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DBController extends SQLiteOpenHelper {
    private static final String LOGCAT = null;

    public static String tableName = "tblProducts";
    public static String colCompany = "Company";
    public static String colProduct = "Product";
    public static String colPrice = "Price";

    public DBController(Context applicationcontext) {

        super(applicationcontext, "mydb.db", null, 1); // creating DATABASE

        Log.d(LOGCAT, "Created");

    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        String query;

        query = "CREATE TABLE IF NOT EXISTS " + tableName + "( " + colCompany + " TEXT, " + colProduct +
                " TEXT PRIMARY KEY, " + colPrice + " TEXT)";
        Log.e("create Query", query);
        database.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS " + tableName;
        database.execSQL(query);
        onCreate(database);
    }

    public ArrayList<HashMap<String, String>> getAllProducts() {

        ArrayList<HashMap<String, String>> productList;
        productList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM " + tableName;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            do {
//Id, Company,Name,Price
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("a", cursor.getString(0));
                map.put("b", cursor.getString(1));
                map.put("c", cursor.getString(2));
                productList.add(map);
                Log.e("dataofList", cursor.getString(0) + "," + cursor.getString(1) + "," + cursor.getString(2));
            } while (cursor.moveToNext());
        }
        return productList;

    }

}