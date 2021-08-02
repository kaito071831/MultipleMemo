package com.example.multiplefeaturememo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public  DBHelper(Context context){
        super(context, "Tab2", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE tab2List" + "(id INTEGER PRIMARY KEY, name TEXT, price INTEGER)");
        String[] qry1 = {"INSERT INTO tab2List(name, price) VALUES ('----',0)",
                "INSERT INTO tab2List(name, price) VALUES ('トマト',100)",
                "INSERT INTO tab2List(name, price) VALUES ('きゅうり',80)",
                "INSERT INTO tab2List(name, price) VALUES ('じゃがいも',110)",
                "INSERT INTO tab2List(name, price) VALUES ('なす',90)"};

        for(String q : qry1) {
            db.execSQL(q);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
