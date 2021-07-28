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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {


    EditText title;
    EditText pt;
    Button bt;

    String str;
    SQLiteDatabase db;
    String qry2;
    Cursor cr;
    ArrayAdapter<String> ad;
    Spinner sp;
    Spinner sp1;
    Spinner sp2;
    TextView total;
    String spText;
    String spText1;
    String spText2;
    String intSpText;
    String intSpText1;
    String intSpText2;
    int spInt;
    int spInt1;
    int spInt2;
    int totalNum;
    String strTotal;
    Button btCalc;
    EditText productname;
    EditText productprice;
    Button addproduct;

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
        //スピナー選択と合計表示の処理
        sp = findViewById(R.id.spinner);
        sp1 = findViewById(R.id.spinner1);
        sp2 = findViewById(R.id.spinner2);
        total = findViewById(R.id.total);
        btCalc = findViewById(R.id.totalflag);

        str = "data/data/" + getPackageName() + "/Tab2.db";
        db = SQLiteDatabase.openOrCreateDatabase(str, null);

        String qry0 = "CREATE TABLE tab2List" + "(id INTEGER PRIMARY KEY, name STRING, price INTEGER)";
        String[] qry1 = {"INSERT INTO tab2List(name, price) VALUES ('----',0)",
                "INSERT INTO tab2List(name, price) VALUES ('トマト',100)",
                "INSERT INTO tab2List(name, price) VALUES ('きゅうり',80)",
                "INSERT INTO tab2List(name, price) VALUES ('じゃがいも',110)",
                "INSERT INTO tab2List(name, price) VALUES ('なす',90)"};

        qry2 = "SELECT * FROM tab2List";

        //DBにtab2Listというテーブルがなければflagがfalseになる
        String qry3 = "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='tab2List';";
        Cursor c = db.rawQuery(qry3, null);
        c.moveToFirst();
        String result = c.getString(0);
        Boolean flag = result.contains("0");

        //もしflagがfalseならばcreate tableやinsertを実行する
        if(flag){
            db.execSQL(qry0);
            for(int i=0; i<qry1.length; i++) {
                db.execSQL(qry1[i]);
            }
        }

        //テーブルを参照して中身をcrに渡す
        cr = db.rawQuery(qry2, null);

        ad = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        while(cr.moveToNext()) {  //カーソルを一つづつ動かしデータを取得
            int i = cr.getColumnIndex("id");  //データをテーブルの要素ごとに取得
            int n = cr.getColumnIndex("name");
            int p = cr.getColumnIndex("price");

            String name = cr.getString(n);
            int price = cr.getInt(p);
            String row = name + "(" + price + "円)";
            ad.add(row);
        }

        //スピナーにリストをセット
        sp.setAdapter(ad);
        sp1.setAdapter(ad);
        sp2.setAdapter(ad);

        btCalc.setOnClickListener(new CalcClickListener());

        //データベースへのデータ追加処理
        productname = findViewById(R.id.productname);
        productprice = findViewById(R.id.productprice);
        addproduct = findViewById(R.id.addproduct);

        addproduct.setOnClickListener(new AddDataClickListener());

        db.close();
    }

    //タブ１のメモの保存
    class TSaveClickListener implements View.OnClickListener {
        @Override
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

    //タブ2の選択されたスピナの合計金額計算
    class CalcClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v){
            if(v == btCalc){
                spText = sp.getSelectedItem().toString();
                spText1 = sp1.getSelectedItem().toString();
                spText2 = sp2.getSelectedItem().toString();
                intSpText = spText.replaceAll("[^0-9]", "");
                intSpText1 = spText1.replaceAll("[^0-9]", "");
                intSpText2 = spText2.replaceAll("[^0-9]", "");
                spInt = Integer.parseInt(intSpText);
                spInt1 = Integer.parseInt(intSpText1);
                spInt2 = Integer.parseInt(intSpText2);
                totalNum = spInt + spInt1 + spInt2;
                strTotal = Integer.toString(totalNum);

                total.setText("合計金額：" + strTotal + "円");
            }
        }
    }

    //DBに新たな商品を追加
    class AddDataClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(v == addproduct){
                db = SQLiteDatabase.openOrCreateDatabase(str, null);
                String pn = productname.getText().toString();
                int pp = Integer.parseInt(productprice.getText().toString());
                String qry4 = "INSERT INTO tab2List(name, price) VALUES (" + "'" + pn + "'" + "," + pp + ")";
                db.execSQL(qry4);
                cr = db.rawQuery(qry2, null);
                cr.moveToLast();
                int i = cr.getColumnIndex("id");  //データをテーブルの要素ごとに取得
                int n = cr.getColumnIndex("name");
                int p = cr.getColumnIndex("price");

                String name = cr.getString(n);
                int price = cr.getInt(p);
                String row = name + "(" + price + "円)";
                ad.add(row);
                productname.setText("");
                productprice.setText("");
                db.close();
            }
        }
    }
}