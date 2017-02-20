package com.dbw.hotel;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.dbw.hotel.model.Customer;
import com.dbw.hotel.model.Hotel;
import com.dbw.hotel.model.HotelOrder;
import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.List;

/**
 * Created by DBW on 2017/1/3.
 * 酒店详情页
 */
public class HotelDetailActivity extends BaseActivity{
    private static final String TAG = "HotelDetailActivity";

    public static final String HOTEL_ID = "hotel_id";
    public static final String HOTEL_NAME = "hotel_name";
    public static final String HOTEL_IMAGE_ID = "hotel_image_id";
    public static final String HOTEL_CONTENT = "hotel_content";

    private Button orderButton;                   //预订按钮
    private String hotel_name;                    //酒店名
    private int hotel_image_id;                   //酒店图片id
    private  String hotelContent;                 //酒店信息
    private int hotel_id;                        //酒店的id

    private final View.OnClickListener orderClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //获取开始时间和结束时间
            Calendar calendar = Calendar.getInstance();
            StringBuilder beginTime = new StringBuilder();
            beginTime.append(calendar.get(Calendar.YEAR)).append("年")
                    .append(calendar.get(Calendar.MONTH)).append("月")
                    .append(calendar.get(Calendar.DAY_OF_MONTH)).append("日")
                    .append(calendar.get(Calendar.HOUR_OF_DAY)).append(":")
                    .append(calendar.get(Calendar.MINUTE)).append(":")
                    .append(calendar.get(Calendar.SECOND));
            StringBuilder endTime = new StringBuilder();
            endTime.append(calendar.get(Calendar.YEAR)).append("年")
                    .append(calendar.get(Calendar.MONTH)).append("月")
                    .append((calendar.get(Calendar.DAY_OF_MONTH)+1)).append("日")
                    .append(12).append(":")
                    .append(00).append(":")
                    .append(00);

            //生成订单ID
            String id = Math.random()*999999999+100000000 + "";

            List<Customer> list = DataSupport.findAll(Customer.class);
            Customer customer = list.get(0);
//            Customer customer = (Customer) DataSupport.select("name","邓博文").find(Customer.class);
            Log.d(TAG, "onClick: 顾客信息："+customer.getName());
            String customerName = customer.getName();

            List<Hotel> hotels = DataSupport.findAll(Hotel.class);
//            String hotel = DataSupport.find(Hotel.class,hotel_id).getName();
            String hotel = hotels.get(0).getName();

            //生成订单
            HotelOrder order = new HotelOrder();
            order.setBeginTime(beginTime.toString());
            order.setEndTime(endTime.toString());
            order.setOrderID(id);
            order.setCustomer(customerName);
            order.setHotel(hotel);
            order.setOrderPrice(200);
            order.save();
            Log.d(TAG, "onClick: save:"+order.isSaved());

//            customer.getOrders().add(order);
//            customer.save();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);

        //获取Intent中的数据
        Intent intent = getIntent();
        hotel_name = intent.getStringExtra(HOTEL_NAME);
        hotel_image_id = intent.getIntExtra(HOTEL_IMAGE_ID,0);
        hotelContent = intent.getStringExtra(HOTEL_CONTENT);
        hotel_id = intent.getIntExtra(HOTEL_ID,1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.hd_toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.hd_collapsingToolbar);
        ImageView imageView = (ImageView) findViewById(R.id.hd_image);
        TextView content = (TextView) findViewById(R.id.hd_content);
        orderButton = (Button) findViewById(R.id.hd_order);


        //设置toolbar
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //设置标题
        collapsingToolbar.setTitle(hotel_name);

        //设置图片
        Glide.with(this).load(hotel_image_id).into(imageView);

        //设置内容
        content.setText(hotelContent);

        //添加预订监听
        orderButton.setOnClickListener(orderClickListener);


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
