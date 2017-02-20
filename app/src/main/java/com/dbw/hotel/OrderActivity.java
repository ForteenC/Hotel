package com.dbw.hotel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dbw.hotel.model.HotelOrder;
import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by DBW on 2017/1/4.
 * 订单活动类
 */
public class OrderActivity extends BaseActivity {

    private static final String TAG = "OrderActivity";

//    private RecyclerView mRecycleView;
    private LinearLayout container;
    private List<HotelOrder> orders;
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        container = (LinearLayout) findViewById(R.id.order_container);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.order_collapsing_toolbar);
        toolbar = (Toolbar) findViewById(R.id.order_toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.order_recycleView);

        collapsingToolbar.setTitle("订单详情");

        //连接数据库，获取所有订单
        orders = DataSupport.findAll(HotelOrder.class);
        OrderAdapter adapter = new OrderAdapter(orders);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
