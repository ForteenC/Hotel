package com.dbw.hotel;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dbw.hotel.model.HotelOrder;
import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by DBW on 2017/1/4.
 * 订单的Adapter类
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context mContext;
    private List<HotelOrder> orders;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.order_item_container);
            textView = (TextView) itemView.findViewById(R.id.order_item_content);
        }
    }

    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.order_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderAdapter.ViewHolder holder, int position) {
        for (HotelOrder order : orders) {

            //设置TextView
            StringBuilder builder = new StringBuilder();
            builder.append("订单号：").append(order.getOrderID()).append("\n")
                    .append("用户名：").append(order.getCustomer()).append("\n")
                    .append("酒店：").append(order.getHotel()).append("\n")
                    .append("入住时间:").append(order.getBeginTime()).append("——").append(order.getEndTime()).append("\n")
                    .append("价格：").append(order.getOrderPrice());
            holder.textView.setText(builder.toString());
        }

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }


    public OrderAdapter(List<HotelOrder> orderList){
        orders = orderList;
    }
}
