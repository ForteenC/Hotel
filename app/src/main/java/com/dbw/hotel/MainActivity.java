package com.dbw.hotel;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import com.dbw.hotel.model.Hotel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.io.IOException;
import java.util.List;


public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private FloatingActionButton mFloatButton;
    private List<Hotel> mHotelList;
    private HotelAdapter hotelAdapter;
    private RecyclerView mRecyclerView;
    private ViewStub mViewStub;
    private SwipeRefreshLayout mSwipeRefresh;
    public static final String HOST = "http://webdbw.top/resources";
    private final String mUrl = HOST+"/json/hotel_data.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //设置ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mFloatButton = (FloatingActionButton) findViewById(R.id.fab);
        mViewStub = (ViewStub) findViewById(R.id.vs_recycle_view);

        ActionBar actionBar = getSupportActionBar();                                //获取到了ActionBar的实例，但其实是toolbar的实例
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);                              //让导航按钮显示出来
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu_white_24dp);             //设置导航按钮的图标
        }

        //设置navigationView
        mNavigationView.setCheckedItem(R.id.nav_order);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_order:
                        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_about:
                        Toast.makeText(MainActivity.this, "作者：邓博文", Toast.LENGTH_SHORT).show();
                        break;
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });


        //设置悬浮按钮点击监听
        mFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "点击了悬浮按钮", Toast.LENGTH_SHORT).show();
            }
        });


        //初始化酒店
        initHotels();

    }

    /**
     * 初始化顾客的信息
     */
    private void initCustomers() {
        //在这里实现顾客的信息获取
    }

    /**
     * 对酒店信息进行初始化
     */
    private void initHotels(){
        //开启子线程从网络上请求酒店信息
        HttpAsyncTask initTask = new HttpAsyncTask();
        initTask.execute(mUrl);
    }




    /**
     * 用来进行网络请求的task，请求下来一个json并解析成一个数组返回
     */
    private class HttpAsyncTask extends AsyncTask<String, Integer, List<Hotel>> {

        @Override
        protected List<Hotel> doInBackground(String... strings) {

            //从网页上请求数据
            String url = strings[0];
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String responseData = response.body().string();

                //将Json解析为一个Hotel数组
                Gson gson = new Gson();
                mHotelList = gson.fromJson(responseData, new TypeToken<List<Hotel>>() {}.getType());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return mHotelList;
        }

        @Override
        protected void onPostExecute(List<Hotel> hotelList) {
            if (hotelList!=null ){
                initRecycleView(hotelList);
            }else {
                Toast.makeText(MainActivity.this,"获取酒店信息失败，请重试",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 用酒店的数组来初始化RecycleView
     * @param hotelList
     */
    private void initRecycleView(List<Hotel> hotelList) {

        //替换ViewStub
        mViewStub.inflate();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        //设置下拉刷新
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshHotels();
            }
        });


        //初始化RecycleView
        GridLayoutManager layoutManager = new GridLayoutManager(this,2); //以两列显示
        mRecyclerView.setLayoutManager(layoutManager);
        hotelAdapter = new HotelAdapter(this,hotelList);
        mRecyclerView.setAdapter(hotelAdapter);

    }

    /**
     * 在这里刷新recycleView
     */
    private void refreshHotels() {
        Toast.makeText(this, "下拉刷新", Toast.LENGTH_SHORT).show();
        mSwipeRefresh.setRefreshing(false);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_backup:
                Toast.makeText(this, "点击了设置按钮", Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);                          //从start的位置显示drawerLayout
                break;
            default:
                break;
        }
        return true;
    }

}

