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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    EditText title, pt, productname, productprice;
    Button bt, btR, btCalc, addproduct, removeproduct;
    String str, qry2;
    SQLiteDatabase db;
    Cursor cr;
    ArrayAdapter<String> ad;
    Spinner sp, sp1, sp2;
    TextView total;

    @Override
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

        th.setCurrentTab(0); //最初のタブをtab1に設定

        //タブ1の処理
        title = findViewById(R.id.title); //xmlからidがtitleのオブジェクトを取得して代入
        pt = findViewById(R.id.editText);
        bt = findViewById(R.id.button);
        btR = findViewById(R.id.buttonR);

        bt.setOnClickListener(new ButtonClickListener());
        btR.setOnClickListener(new ButtonClickListener());

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

        //DBにtab2Listというテーブルがなければflagがtrueになる
        String qry3 = "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='tab2List';";
        cr = db.rawQuery(qry3, null);
        cr.moveToFirst();
        String result = cr.getString(0);
        Boolean flag = result.contains("0");

        //もしflagがtrueならばcreate tableやinsertを実行する
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
            int n = cr.getColumnIndex("name");//データをテーブルの要素ごとに取得
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

        btCalc.setOnClickListener(new ButtonClickListener());

        //データベースへのデータ追加処理
        productname = findViewById(R.id.productname);
        productprice = findViewById(R.id.productprice);
        addproduct = findViewById(R.id.addproduct);
        removeproduct = findViewById(R.id.removeproduct);

        addproduct.setOnClickListener(new ButtonClickListener());
        removeproduct.setOnClickListener(new ButtonClickListener());

        db.close();
    }

    //ボタンのイベント処理クラス
    class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v){
            //タブ１のメモの保存
            if(v == bt){
                try{
                    FileOutputStream fos =
                            openFileOutput(title.getText().toString()+".txt", Context.MODE_PRIVATE);
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
                    bw.write(pt.getText().toString());
                    bw.flush();
                    fos.close();
                    title.setText("");
                    pt.setText("");
                }catch(Exception e){}
            }

            //タブ1のメモの読み込み
            if(v == btR){
                try {
                    FileInputStream fis = openFileInput(title.getText().toString() + ".txt"); //ファイル名「Sample.txt」を入力ストリームとして開く
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));  //入力ストリームをバッファードリーダにつなげる
                    StringBuffer sb = new StringBuffer();  //読み込んだ文字をためるためのバッファーの生成
                    String str;
                    while((str = br.readLine()) != null)  //ファイルを1行づつ読み込み
                        sb.append(str);  //読み込んだ分をバッファ上でつなぎ合わせる
                    pt.setText(sb);  //エディットテキストに出力
                } catch (Exception e) {}
            }

            //タブ2の選択されたスピナの合計金額計算
            if(v == btCalc){
                int spInt =
                        Integer.parseInt(sp.getSelectedItem().toString().replaceAll("[^0-9]", ""));
                int spInt1 =
                        Integer.parseInt(sp1.getSelectedItem().toString().replaceAll("[^0-9]", ""));
                int spInt2 =
                        Integer.parseInt(sp2.getSelectedItem().toString().replaceAll("[^0-9]", ""));

                total.setText("合計金額：" + Integer.toString(spInt + spInt1 +spInt2) + "円");
            }

            //DBに新たな商品を追加
            if(v == addproduct){
                db = SQLiteDatabase.openOrCreateDatabase(str, null);
                String pn = productname.getText().toString();
                int pp = Integer.parseInt(productprice.getText().toString());
                String qry4 = "INSERT INTO tab2List(name, price) VALUES (" + "'" + pn + "'" + "," + pp + ")";
                db.execSQL(qry4);
                cr = db.rawQuery(qry2, null);
                cr.moveToLast();

                //データをテーブルから取得
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

            //DBから商品を削除する
            if(v == removeproduct){
                db = SQLiteDatabase.openOrCreateDatabase(str, null);
                String pn = productname.getText().toString();
                int pp = Integer.parseInt(productprice.getText().toString());
                String row = pn + "(" + pp + "円)";
                String qry5 = "DELETE FROM tab2List WHERE name = " + "'" + pn + "'";
                db.execSQL(qry5);
                ad.remove(row);
                sp.setAdapter(ad);
                sp1.setAdapter(ad);
                sp2.setAdapter(ad);
                productname.setText("");
                productprice.setText("");
                db.close();
            }
        }
    }
}