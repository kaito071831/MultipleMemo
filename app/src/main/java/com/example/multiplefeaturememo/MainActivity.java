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

    private EditText title, pt, totaltitle, productname, productprice;
    private Button bt, btR, btCalc, totalprint, addproduct, removeproduct;
    private String str, qry2;
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private Cursor cr;
    private ArrayAdapter<String> ad;
    private Spinner[] sp = new Spinner[3];
    private TextView total;

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
        //スピナー選択と合計表示,テキスト出力の処理
        sp[0] = findViewById(R.id.spinner);
        sp[1] = findViewById(R.id.spinner1);
        sp[2] = findViewById(R.id.spinner2);
        total = findViewById(R.id.total);
        totaltitle = findViewById(R.id.totaltitle);
        totalprint = findViewById(R.id.totalprint);
        btCalc = findViewById(R.id.totalflag);

        totalprint.setOnClickListener(new ButtonClickListener());

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        qry2 = "SELECT * FROM tab2List";

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
        for(Spinner s : sp){
            s.setAdapter(ad);
        }

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
                    FileInputStream fis = openFileInput(title.getText().toString() + ".txt"); //ファイル名「メモのタイトル.txt」を入力ストリームとして開く
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));  //入力ストリームをバッファードリーダにつなげる
                    StringBuffer sb = new StringBuffer();  //読み込んだ文字をためるためのバッファーの生成
                    String str;
                    while((str = br.readLine()) != null)  //ファイルを1行づつ読み込み
                        sb.append(str + "\n");  //読み込んだ分をバッファ上でつなぎ合わせる
                    pt.setText(sb);  //エディットテキストに出力
                } catch (Exception e) {}
            }

            //タブ2の選択されたスピナの合計金額計算
            if(v == btCalc){
                int[] spInt = new int[3];
                for(int i=0; i<spInt.length; i++){
                    spInt[i] = Integer.parseInt(sp[i].getSelectedItem().toString().replaceAll("[^0-9]", ""));
                }
                int totalnum = 0;
                for (int i : spInt) {
                    totalnum += i;
                }
                total.setText("合計金額：" + Integer.toString(totalnum) + "円");
            }

            //商品と合計金額をメモに出力
            if(v == totalprint){
                try{
                    FileOutputStream fos =
                            openFileOutput(totaltitle.getText().toString()+".txt", Context.MODE_PRIVATE);
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
                    for(Spinner s : sp){
                        bw.write(s.getSelectedItem().toString());
                        bw.newLine();
                    }
                    bw.write(total.getText().toString());
                    bw.newLine();
                    bw.flush();
                    fos.close();
                    totaltitle.setText("");
                }catch (Exception e){}
            }

            //DBに新たな商品を追加
            if(v == addproduct){
                db = dbHelper.getWritableDatabase();
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
                db = dbHelper.getWritableDatabase();
                String pn = productname.getText().toString();
                int pp = Integer.parseInt(productprice.getText().toString());
                String row = pn + "(" + pp + "円)";
                String qry5 = "DELETE FROM tab2List WHERE name = " + "'" + pn + "'";
                db.execSQL(qry5);
                ad.remove(row);
                for(Spinner s : sp){
                    s.setAdapter(ad);
                }
                productname.setText("");
                productprice.setText("");
                db.close();
            }
        }
    }
}