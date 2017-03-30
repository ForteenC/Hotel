package com.dbw.hotel;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.dbw.hotel.model.Hotel;
import okhttp3.*;

import java.io.IOException;

/**
 * Created by DBW on 2017/1/3.
 * 酒店详情页
 */
public class HotelDetailActivity extends BaseActivity{
    private static final String TAG = "HotelDetailActivity";

    private Hotel mHotel;                       //酒店类
    private ImageView mImageView;               //酒店的图片
    private Button mOrderButton;                //预订按钮


    private final Callback mCallback = new Callback() {

        @Override
        public void onFailure(Call call, IOException e) {
            Looper.prepare();
            Toast.makeText(HotelDetailActivity.this,"请求失败",Toast.LENGTH_SHORT).show();
            Looper.loop();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Looper.prepare();
            Toast.makeText(HotelDetailActivity.this,"接受到了信息",Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    };
    private final View.OnClickListener orderClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    String path = "/servlet/GetHotelData";
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(MainActivity.HOST+path)
                            .build();
                    client.newCall(request).enqueue(mCallback);
                    Looper.loop();
                }
            });
            thread.start();

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);


        //获取Intent中的数据
        Intent intent = getIntent();
        Bundle hotelBundle  = intent.getBundleExtra("hotelBundle");
        mHotel = (Hotel) hotelBundle.get("hotel");
        if (mHotel == null){
            this.finish();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.hd_toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.hd_collapsingToolbar);
        TextView content = (TextView) findViewById(R.id.hd_content);
        mImageView = (ImageView) findViewById(R.id.hd_image);
        mOrderButton = (Button) findViewById(R.id.hd_order);

        //设置toolbar
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //设置标题
        collapsingToolbar.setTitle(mHotel.getName());

        //设置图片
        Glide.with(this).load(mHotel.getImageUrl()).into(mImageView);

        //设置内容
        content.setText(mHotel.getContent());

        //添加预订监听
        mOrderButton.setOnClickListener(orderClickListener);


    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:                                                 //如果是返回按钮，就结束当前activity
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
