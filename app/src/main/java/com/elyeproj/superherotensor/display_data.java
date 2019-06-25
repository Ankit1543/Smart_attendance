package com.elyeproj.superherotensor;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class display_data extends AppCompatActivity {

    Cursor c;
    public DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DatabaseHelper(this);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        HashMap thumbs=
                (HashMap) bundle.getSerializable("my_map");

        List<String> my_array_list = new ArrayList<String>();

        for (Object i:thumbs.keySet()){

            c = db.getAttendance((String) i);

            if(c != null && c.getCount()>0){
                // update att
                c.moveToFirst();
                db.updateattendance((String) i,c.getInt(0)+(int)thumbs.get(i));
                c.close();
            }
            else {
                // insert new student
                db.insertStudent((String) i,(int)thumbs.get(i));
            }

        }

        // getting all records from database

        c = db.getAllStudents();
        c.moveToFirst();
        do{
            my_array_list.add(c.getString(1)+"   "+c.getInt(2));
        }while (c.moveToNext());

        ListView studentNameList = findViewById(R.id.student_name);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                my_array_list );
        studentNameList.setAdapter(arrayAdapter);

    }


}
