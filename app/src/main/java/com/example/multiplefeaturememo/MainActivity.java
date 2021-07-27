package com.example.multiplefeaturememo;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    EditText title;
    EditText pt;
    Button bt;
    Spinner sp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        TabHost th = (TabHost)findViewById(android.R.id.tabhost);  //TabHostオブジェクト取得
        th.setup();  //TabHostのセットアップ

        TabSpec tab1 = th.newTabSpec("tab1");  //tab1のセットアップ
        tab1.setIndicator("メモ");  //タブ名の設定
        tab1.setContent(R.id.tab1);   //タブに使うリニアレイアウトの指定
        th.addTab(tab1);  //タブホストにタブを追加

        TabSpec tab2 = th.newTabSpec("tab2");  //tab2のセットアップ
        tab2.setIndicator("計算");
        tab2.setContent(R.id.tab2);
        th.addTab(tab2);

        TabSpec tab3 = th.newTabSpec("tab3"); //tab3のセットアップ
        tab3.setIndicator("タブ3");
        tab3.setContent(R.id.tab3);
        th.addTab(tab3);

        th.setCurrentTab(0); //最初のタブをtab1に設定

        //タブ1の処理
        title = findViewById(R.id.title); //xmlからidがtitleのオブジェクトを取得して代入
        pt = findViewById(R.id.editText); //xmlから
        bt = findViewById(R.id.button);

        bt.setOnClickListener(new TSaveClickListener());

        //タブ2の処理
        String str = "data/data/" + getPackageName() + "/Tab2.db";
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(str, null);

        String qry0 = "CREATE TABLE tab2List" + "(id INTEGER PRIMARY KEY, name STRING, price INTEGER)";
        String[] qry1 = {"INSERT INTO tab2List(name, price) VALUES ('トマト',100)",
                "INSERT INTO tab2List(name, price) VALUES ('きゅうり',80)",
                "INSERT INTO tab2List(name, price) VALUES ('じゃがいも',110)",
                "INSERT INTO tab2List(name, price) VALUES ('なす',90)"};

        String qry2 = "SELECT * FROM tab2List";


        File dbF = new File(str);
        Boolean fbFlag = dbF.exists();

        if(fbFlag){
        }else{
            db.execSQL(qry0);
            for(int i=0; i<qry1.length; i++){
                db.execSQL(qry1[i]);
            }
        }


        Cursor cr = db.rawQuery(qry2, null);
        ArrayAdapter<String> ad = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        sp = findViewById(R.id.spinner);

        while(cr.moveToNext()) {  //カーソルを一つづつ動かしデータを取得
            int i = cr.getColumnIndex("id");  //データをテーブルの要素ごとに取得
            int n = cr.getColumnIndex("name");
            int p = cr.getColumnIndex("price");
            int id = cr.getInt(i);
            String name = cr.getString(n);
            int price = cr.getInt(p);
            String row = id + ":" + name + "(" + price + "円)";
            ad.add(row);
        }
        sp.setAdapter(ad);

        db.close();
    }

    class TSaveClickListener implements View.OnClickListener {
        public void onClick(View v){
            if(v == bt){
                try{
                    FileOutputStream fos = openFileOutput(title.getText().toString()+".txt", Context.MODE_PRIVATE);
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
                    bw.write(pt.getText().toString());
                    bw.flush();
                    fos.close();
                }catch(Exception e){}
            }
        }
    }
}