package com.example.myapp;

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;



public class MainActivity extends ListActivity {

    TextView lbl;
    DBController controller;
    Button btnimport;
    ListView lv;
    final Context context = this;
    ListAdapter adapter;

    ArrayList<HashMap<String, String>> myList;
    public static final int requestcode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        controller = new DBController(this);
        lbl = (TextView) findViewById(R.id.txtresulttext);
        btnimport = (Button) findViewById(R.id.btnupload);
        lv = getListView();
        btnimport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
                fileintent.setType("text/csv");
                try {
                    startActivityForResult(fileintent, requestcode);
                } catch (ActivityNotFoundException e) {
                    lbl.setText("No activity can handle picking a file. Showing alternatives.");
                }
            }
        });

        myList = controller.getAllProducts();
        if (myList.size() != 0) {
            ListView lv = getListView();
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, myList,
                    R.layout.lst_template, new String[]{"a", "b", "c"}, new int[]{
                    R.id.txtproductcompany, R.id.txtproductname, R.id.txtproductprice});
            setListAdapter(adapter);
            lbl.setText("");

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data == null)

            return;
        switch (requestCode) {

            case requestcode:

                String filepath = data.getData().getPath();
                Log.e("File path", filepath);

                if (filepath.contains("/root_path"))
                    filepath = filepath.replace("/root_path", "");

                Log.e("New File path", filepath);
                controller = new DBController(getApplicationContext());
                SQLiteDatabase db = controller.getWritableDatabase();

                db.execSQL("delete from " + DBController.tableName);

                try {

                    if (resultCode == RESULT_OK) {
                        Log.e("RESULT CODE", "OK");
                        try {
                            FileReader file = new FileReader(filepath);
                            BufferedReader buffer = new BufferedReader(file);
                            ContentValues contentValues = new ContentValues();
                            String line = "";
                            db.beginTransaction();

                            while ((line = buffer.readLine()) != null) {

                                Log.e("line", line);
                                String[] str = line.split(",", 3); // defining 3 columns with null or blank field //values acceptance

//Id, Company,Name,Price

                                String company = str[0].toString();
                                String Product = str[1].toString();
                                String Price = str[2].toString();

                                contentValues.put(DBController.colCompany, company);
                                contentValues.put(DBController.colProduct, Product);
                                contentValues.put(DBController.colPrice, Price);
                                db.insert(DBController.tableName, null, contentValues);

                                lbl.setText("Successfully Updated Database.");
                                Log.e("Import", "Successfully Updated Database.");
                            }
                            db.setTransactionSuccessful();

                            db.endTransaction();

                        } catch (SQLException e) {
                            Log.e("SQLError", e.getMessage().toString());
                        } catch (IOException e) {
                            Log.e("IOException", e.getMessage().toString());

                        }
                    } else {
                        Log.e("RESULT CODE", "InValid");
                        if (db.inTransaction())

                            db.endTransaction();
                        Toast.makeText(MainActivity.this, "Only CSV files allowed.", Toast.LENGTH_LONG).show();

                    }
                } catch (Exception ex) {
                    Log.e("Error", ex.getMessage().toString());
                    if (db.inTransaction())

                        db.endTransaction();

                    Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();

                }

        }

        myList = controller.getAllProducts();

        if (myList.size() != 0) {

            ListView lv = getListView();

            ListAdapter adapter = new SimpleAdapter(MainActivity.this, myList,

                    R.layout.lst_template, new String[]{"a", "b", "c"}, new int[]{
                    R.id.txtproductcompany, R.id.txtproductname, R.id.txtproductprice});

            setListAdapter(adapter);

            lbl.setText("Data Imported");

        }
    }
}