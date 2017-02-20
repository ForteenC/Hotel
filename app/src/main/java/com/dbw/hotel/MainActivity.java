package com.dbw.hotel;

import android.content.Intent;
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
import android.widget.Toast;

import com.dbw.hotel.model.Customer;
import com.dbw.hotel.model.Hotel;
import com.dbw.hotel.model.HotelOrder;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private FloatingActionButton mFloatButton;
    private List<Hotel> mHotelList;
    private HotelAdapter hotelAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //连接数据库
        Connector.getDatabase();
        initHotels();
        initCustomers();

        //设置ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mFloatButton = (FloatingActionButton) findViewById(R.id.fab);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        ActionBar actionBar = getSupportActionBar();                                //获取到了ActionBar的实例，但其实是toolbar的实例
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);                              //让导航按钮显示出来
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu_white_24dp);             //设置导航按钮的图标
        }

        //设置navigationView
        mNavigationView.setCheckedItem(R.id.nav_order);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_order:
                        Intent intent = new Intent(MainActivity.this,OrderActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_about:
                        Toast.makeText(MainActivity.this,"作者：邓博文",Toast.LENGTH_SHORT).show();
                        break;
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        //设置RecycleView
        mHotelList = DataSupport.findAll(Hotel.class);                  //从数据库将所有的Hotel查询出来
        GridLayoutManager layoutManager = new GridLayoutManager(this,2); //以两列显示
        mRecyclerView.setLayoutManager(layoutManager);
        hotelAdapter = new HotelAdapter(mHotelList);
        mRecyclerView.setAdapter(hotelAdapter);

        //设置下拉刷新
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshHotels();
            }
        });

        //设置悬浮按钮点击监听
        mFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwipeRefresh.setRefreshing(true);
                Toast.makeText(MainActivity.this,"点击了悬浮按钮",Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 在这里刷新recycleView
     * 连接数据库
     */
    private void refreshHotels() {
        Toast.makeText(this,"下拉刷新",Toast.LENGTH_SHORT).show();
        mSwipeRefresh.setRefreshing(false);
    }

    private void initCustomers(){
        List<Customer> list = DataSupport.findAll(Customer.class);
        if (list.size()>0){
            DataSupport.deleteAll(Customer.class);
        }

        Customer customer = new Customer();
        customer.setName("邓博文");
        customer.setIDCard("510623199604201013");
        customer.setSex("男");
        customer.setPhone("17801057818");
        customer.setOrders(new ArrayList<HotelOrder>());
        customer.save();
        Log.d(TAG, "initCustomers: customer save:"+customer.isSaved()+",数组大小："+list.size());
    }

    /**
     * 往数据库里存Hotel
     */
    private void initHotels(){
        List<Hotel> list =  DataSupport.findAll(Hotel.class);
        if (list.size()>0){
            DataSupport.deleteAll(Hotel.class);
        }

        for (int i = 0; i < 20; i++) {
            if (i%2 == 0){
                Hotel hotel = new Hotel();
                hotel.setName("和颐酒店");
                hotel.setLevel(4);
                hotel.setLocation("北京亚运村鸟巢店");
                hotel.setImageId(R.drawable.heyi);
                hotel.setContent("位于北京市朝阳区小营西路1号院（亚飞大厦旁），在北京商业繁华的亚运村区域内，紧邻鸟巢、水立方、国家会议中心、北京国际会议中心，奥林匹克公园，奥林匹克森林公园。酒店交通便捷，紧邻地铁五号线惠新西街北口站，步行10分钟左右即到，905路，753路、538路、运通110路、753路，928路等多条线路到达酒店门口，距首都国际机场40分钟车程即可到达。周边有华联超市，幸福超市，北辰购物中心，华堂商场，飘亮广场，中华女子学院，联合大学，对外经贸大学等大型购物场所和高等学院。 商务特色：可开发票 、无线WiFi、 叫醒服务");
                hotel.setOrders(new ArrayList<HotelOrder>());
                hotel.save();
            }else if(i%3==0){
                Hotel hotel = new Hotel();
                hotel.setName("贵州大厦");
                hotel.setLevel(3);
                hotel.setLocation("北京海淀区");
                hotel.setImageId(R.drawable.guizhou);
                hotel.setOrders(new ArrayList<HotelOrder>());
                hotel.setContent("北京贵州大厦是贵州省人民政府投资兴建的一座集客房、餐饮、会议、康乐为一体的综合大厦，地处北三环，位于和平西桥北侧，乘地铁五号线至和平西桥站下，交通便利 北京贵州大厦设有标准房、套房、豪华套房等，客房内装有中央空调、卫星闭路电视、国际国内直拨电话和安全报警等系统。其中豪华套房还配有独立的电脑花洒淋浴按摩设施，是宾客下榻、用餐、娱乐、举行会议的理想选择。 酒店设施 票务服务 理发美容室");
                hotel.save();
            }else {
                Hotel hotel = new Hotel();
                hotel.setName("169商务酒店（首都机场国展店）");
                hotel.setLocation("天竺镇天柱东路8号（近首都机场，新国展）");
                hotel.setLevel(3);
                hotel.setImageId(R.drawable.shangwu169);
                hotel.setOrders(new ArrayList<HotelOrder>());
                hotel.setContent("免费提供24小时机场接送服务。本酒店地处北京市顺义区天竺镇天柱东路8号。东邻首都国际机场T1T2航站楼与T3航站楼；西与新国际展览中心毗邻，仅五分钟车程。北京169商务酒店地上四层，外观设计庄重、大方，内部装修典雅、前位，极富文化内涵。酒店设有多种房型，房间内设程控电话、卫星电视、磁卡门锁、宽带上网等多功能的现代化智能服务。客房温馨舒适，服务周到热情，是您出差旅行优选之地。商务特色：可开发票、无线WIFI、叫醒服务、");
                hotel.save();
            }

        }

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
                Toast.makeText(this,"点击了设置按钮",Toast.LENGTH_SHORT).show();
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

