package com.dbw.hotel;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.dbw.hotel.model.Hotel;

import java.util.List;

/**
 * Created by DBW on 2016/12/29.
 * Hotel的适配器
 */
public class HotelAdapter  extends RecyclerView.Adapter<HotelAdapter.ViewHolder>{

    private Context mContext;
    private List<Hotel> mHotelList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView hotelImage;
        TextView hotelText;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            hotelImage = (ImageView) cardView.findViewById(R.id.hotel_item_img);
            hotelText = (TextView) cardView.findViewById(R.id.hotel_item_text);
        }
    }

    public HotelAdapter(List<Hotel> hotelList){
        this.mHotelList = hotelList;
    }


    @Override
    public HotelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.hotel_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Hotel hotel = mHotelList.get(position);
                Intent intent = new Intent(mContext,HotelDetailActivity.class);
                intent.putExtra(HotelDetailActivity.HOTEL_NAME,hotel.getName());
                intent.putExtra(HotelDetailActivity.HOTEL_IMAGE_ID,hotel.getImageId());
                intent.putExtra(HotelDetailActivity.HOTEL_CONTENT,hotel.getContent());
                intent.putExtra(HotelDetailActivity.HOTEL_ID,position);
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(HotelAdapter.ViewHolder holder, int position) {
        Hotel hotel = mHotelList.get(position);
        String info = hotel.getName()+"\n" +
                "酒店星级："+hotel.getLevel()+
                "\n酒店位置："+hotel.getLocation();
        holder.hotelText.setText(info);
        Glide.with(mContext).load(hotel.getImageId()).into(holder.hotelImage);
    }

    @Override
    public int getItemCount() {
        return mHotelList.size();
    }
}
