package com.example.android.wifirttscan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import javax.crypto.spec.RC2ParameterSpec;

public class SubActivity extends AppCompatActivity {
    float R1=0f;
    float R2=0f;
    float R3=0f;
    float OffSetX=500;
    float OffSetY=1000;

    /*
    AP名"Positioning-01"から"Positioning-03"まで3台の測位用RTT対応APが設置されている
    Positioning-02の位置を原点とした場合の01,03の座標(m)
    AP名と相対座標をデータベース化してAPの追加、削除を容易にできるようになればベター
    {AP名,x座標,y座標}
    将来的には3次元化して、z座標も追加？
     */
    float Pos1_pointx =6.2f;
    float Pos1_pointy =4.2f;
    float Pos2_pointx =0f;
    float Pos2_pointy =0f;
    float Pos3_pointx =5.0f;
    float Pos3_pointy =4.2f;

    float AP1_pointx =0f;
    float AP1_pointy =0f;
    float AP2_pointx =0f;
    float AP2_pointy =0f;
    float AP3_pointx =0f;
    float AP3_pointy =0f;
    String AP1_name;
    String AP2_name;
    String AP3_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        Intent i = this.getIntent();
            //AP名と距離情報の受け取り、数の冗長性が課題
        AP1_name = i.getStringExtra("AP1name");
        String AP1_range = i.getStringExtra("AP1range");
        AP2_name = i.getStringExtra("AP2name");
        String AP2_range = i.getStringExtra("AP2range");
        AP3_name = i.getStringExtra("AP3name");
        String AP3_range = i.getStringExtra("AP3range");

        /*
        ここからSwitch文3個、AP名と座標の照合
        APが増減した場合の冗長性が課題
        構造体で配列組んでAP名と距離格納しておいてループ回せば行ける？
         */
        switch(AP1_name){
            case "Positioning-01":
                AP1_pointx=Pos1_pointx;
                AP1_pointy=Pos1_pointy;
                R1 = Float.parseFloat(AP1_range);
                break;
            case "Positioning-02":
                AP1_pointx=Pos2_pointx;
                AP1_pointy=Pos2_pointy;
                R1 = Float.parseFloat(AP1_range);
                break;
            case "Positioning-03":
                AP1_pointx=Pos3_pointx;
                AP1_pointy=Pos3_pointy;
                R1 = Float.parseFloat(AP1_range);
                break;
        }
        Log.d("Draw", AP1_name+" has set");

        switch(AP2_name){
            case "Positioning-01":
                AP2_pointx=Pos1_pointx;
                AP2_pointy=Pos1_pointy;
                R2 = Float.parseFloat(AP2_range);
                break;
            case "Positioning-02":
                AP2_pointx=Pos2_pointx;
                AP2_pointy=Pos2_pointy;
                R2 = Float.parseFloat(AP2_range);
                break;
            case "Positioning-03":
                AP2_pointx=Pos3_pointx;
                AP2_pointy=Pos3_pointy;
                R2 = Float.parseFloat(AP2_range);
                break;
        }
        Log.d("Draw", AP2_name+" has set");

        switch(AP3_name){
            case "Positioning-01":
                AP3_pointx=Pos1_pointx;
                AP3_pointy=Pos1_pointy;
                R3 = Float.parseFloat(AP3_range);
                break;
            case "Positioning-02":
                AP3_pointx=Pos2_pointx;
                AP3_pointy=Pos2_pointy;
                R3 = Float.parseFloat(AP3_range);
                break;
            case "Positioning-03":
                AP3_pointx=Pos3_pointx;
                AP3_pointy=Pos3_pointy;
                R3 = Float.parseFloat(AP3_range);
                break;
        }
        Log.d("Draw", AP3_name+" has set");

        MyView myView = new MyView(this);
        setContentView(myView);

    }

    class MyView extends View {
        private Paint paint;
        private Paint txtpaint;
        //private Path path;

        private float StrokeWidth1 = 20f;
        private float dp;

        public MyView(Context context) {
            super(context);
            paint = new Paint();
            txtpaint = new Paint();
            //path = new Path();

            // スクリーンサイズからdipのようなものを作る
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            dp = getResources().getDisplayMetrics().density;
            Log.d("debug","fdp="+dp);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // 背景
            canvas.drawColor(Color.argb(255, 255, 255, 255));

            // Canvas 中心点
            float xc = canvas.getWidth() / 2;
            float yc = canvas.getHeight() / 2;

            paint.setColor(Color.argb(255, 255, 80, 80));
            paint.setStrokeWidth(StrokeWidth1);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            txtpaint.setColor(Color.argb(255, 0, 0, 0));
            txtpaint.setStrokeWidth(1);
            txtpaint.setAntiAlias(true);
            txtpaint.setStyle(Paint.Style.FILL_AND_STROKE);
            txtpaint.setTextSize(50.0f);

            // APからの距離を半径とする円を描画
            // (x1,y1,r,paint) 中心x1座標, 中心y1座標, r半径
            canvas.drawCircle(AP1_pointx*100+OffSetX, AP1_pointy*100+OffSetY, R1*100, paint);
            canvas.drawCircle(AP2_pointx*100+OffSetX, AP2_pointy*100+OffSetY, R2*100, paint);
            canvas.drawCircle(AP3_pointx*100+OffSetX, AP3_pointy*100+OffSetY, R3*100, paint);

            paint.setColor(Color.argb(255, 0, 0, 255));
            paint.setStrokeWidth(StrokeWidth1);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);

            //APの位置を示す点とAP名の表示
            canvas.drawCircle(AP1_pointx*100+OffSetX, AP1_pointy*100+OffSetY, 5, paint);
            canvas.drawText(AP1_name, AP1_pointx*100+OffSetX, AP1_pointy*100+OffSetY, txtpaint);

            canvas.drawCircle(AP2_pointx*100+OffSetX, AP2_pointy*100+OffSetY, 5, paint);
            canvas.drawText(AP2_name, AP2_pointx*100+OffSetX, AP2_pointy*100+OffSetY, txtpaint);

            canvas.drawCircle(AP3_pointx*100+OffSetX, AP3_pointy*100+OffSetY, 5, paint);
            canvas.drawText(AP3_name, AP3_pointx*100+OffSetX, AP3_pointy*100+OffSetY+50, txtpaint);

        }
        @Override
        public boolean onTouchEvent(MotionEvent event) {    //画面タップでMainActivityに戻る。将来的にはタップで再度測距して、円を描き直すとかにできればベター
            Log.d("TouchEvent", "Display Touched");
            finish();
            return true;
        }
    }
}
