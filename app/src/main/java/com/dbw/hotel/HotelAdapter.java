package com.dbw.hotel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.cardemulation.HostNfcFService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dbw.hotel.model.Hotel;
import com.dbw.hotel.util.MyUtil;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static android.content.ContentValues.TAG;

/**
 * Created by DBW on 2016/12/29.
 * Hotel的适配器
 */
public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.ViewHolder> {

    private Context mContext;
    private List<Hotel> mHotelList;     //酒店数组

    //内存缓存
    private LruCache<String, Bitmap> mLruCache;

    //硬盘缓存
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 50;           //硬盘缓存大小为50M
    private static final int DISK_CACHE_INDEX = 0;
    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private DiskLruCache mDiskLruCache;
    private boolean mIsDiskCacheCreated = false;

    //线程池参数
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();  //CPU核心数
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;                        //核心线程数
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;                 //最大线程数
    private static final long KEEP_ALIVE = 10L;                                     //存活时间

    //产生新线程的工厂
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable,"ImageLoader#"+mCount.getAndIncrement());
        }
    };

    //线程池
    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(),
            sThreadFactory
    );

    private static final int MESSAGE_POST_RESULT = 1;

    private Handler mMainHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            LoaderResult result = (LoaderResult) msg.obj;
            ImageView imageView = result.imageView;
            String tag = (String) imageView.getTag();
            if (tag.equals(result.url)){
                imageView.setImageBitmap(result.bitmap);
            }else {
                Log.w(TAG, "set image bitmap,but url has changed, ignored!" );
            }
        }
    };

    //内部类，对需要设置的资源进行包装，以便发送给Handler进行处理
    private static class LoaderResult{
         ImageView imageView;
         String url;
         Bitmap bitmap;
         LoaderResult(ImageView imageView,String url,Bitmap bitmap){
            this.imageView = imageView;
            this.url = url;
            this.bitmap = bitmap;
        }
    }

    //Holder类
    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView hotelImage;
        TextView hotelText;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            hotelImage = (ImageView) cardView.findViewById(R.id.hotel_item_img);
            hotelText = (TextView) cardView.findViewById(R.id.hotel_item_text);
        }
    }


    //构造方法
    public HotelAdapter(Context context, List<Hotel> hotelList) {


        this.mHotelList = hotelList;
        mContext = context;

        //初始化LruCache
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };

        //初始化DiskLruCache
        File diskCacheDir = MyUtil.getDiskCacheDir(mContext, "bitmap");
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdirs();
        }
        if (MyUtil.getUsableSpace(diskCacheDir) >= DISK_CACHE_SIZE) {
            try {
                mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE);
                mIsDiskCacheCreated = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.hotel_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Hotel hotel = mHotelList.get(position);
                Bundle bundle = new Bundle();
                bundle.putParcelable("hotel", hotel);
                Intent intent = new Intent(mContext, HotelDetailActivity.class);
                intent.putExtra("hotelBundle", bundle);
                mContext.startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(final HotelAdapter.ViewHolder holder, int position) {
        final Hotel hotel = mHotelList.get(position);
        String info = hotel.getName() + "\n" +
                "酒店星级：" + hotel.getLevel() +
                "\n酒店位置：" + hotel.getLocation();
        holder.hotelText.setText(info);


        final String URL = MainActivity.HOST + hotel.getImageUrl();

        //为ImageView设置Tag，避免加载乱序
        holder.hotelImage.setTag(URL);

        hotel.setImageUrl(URL);

        final int imageWidth = holder.hotelImage.getMeasuredWidth();
        final int imageHeight = holder.hotelImage.getMeasuredHeight();

        //加载图片使用缓存策略
        bindBitmap(URL, holder.hotelImage, imageWidth, imageHeight);
    }

    /**
     * 异步加载图片，采用缓存策略，首先从内存中获取图片，如果没有
     * 则在线程池中开启子线程进行图片的获取
     * @param url   图片网址/本地地址
     * @param imageView ImageView
     * @param reqWidth  ImageView的宽
     * @param reqHeight ImageView的高
     */
    private void bindBitmap(final String url, final ImageView imageView, final int reqWidth, final int reqHeight) {
        imageView.setTag(url);
        Bitmap bitmap = null;

        bitmap = loadBitMapFromMemoryCache(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }

        //创建子线程处理加载图片的缓存问题
        Runnable loadBitmapTask = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = loadBitmap(url,reqWidth,reqHeight);
                if (bitmap!=null){
                    LoaderResult result = new LoaderResult(imageView,url,bitmap);
                    mMainHandler.obtainMessage(MESSAGE_POST_RESULT,result)
                            .sendToTarget();
                }
            }
        };
        THREAD_POOL_EXECUTOR.execute(loadBitmapTask);


    }


    @Override
    public int getItemCount() {
        return mHotelList.size();
    }

    /**
     * 加载Bitmap，先从内存中加载，如果内存中没有，就从硬盘加载，如果仍没有就从网络下载。
     *
     * @param url       url
     * @param reqWidth  width
     * @param reqHeight height
     * @return bitmap
     */
    private Bitmap loadBitmap(String url, int reqWidth, int reqHeight) {
        Bitmap bitmap = null;
        bitmap = loadBitMapFromMemoryCache(url);

        if (bitmap != null) {
            Log.d(TAG, "loadBitmapFromMemoryCache,url=" + url);
            return bitmap;
        }
        try {

            bitmap = loadBitmapFromDiskCache(url, reqWidth, reqHeight);
            if (bitmap != null) {
                Log.d(TAG, "loadBitmapFromDisk,url=" + url);
                return bitmap;
            }

            bitmap = loadBitmapFromHttp(url, reqWidth, reqHeight);
            Log.d(TAG, "loadBitmapFromHttp,url=" + url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bitmap == null && !mIsDiskCacheCreated) {
            Log.w(TAG, "encounter error,DiskLruCache is not created");
            bitmap = downloadBitmapFromUrl(url);
        }

        return bitmap;
    }


    /**
     * 从网络上下载Bitmap，不使用缓存
     *
     * @param urlString url
     * @return bitmap
     */
    private Bitmap downloadBitmapFromUrl(String urlString) {
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        BufferedInputStream in = null;

        try {
            final URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(connection.getInputStream());

            bitmap = BitmapFactory.decodeStream(in);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            MyUtil.closeInStream(in);
        }

        return bitmap;
    }

    /**
     * 从URL中下载文件到OutputStream中
     *
     * @param urlString    url
     * @param outputStream 指定的OutputStream
     * @return 成功与否
     */
    private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        HttpURLConnection connection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;

        try {

            final URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(connection.getInputStream(), IO_BUFFER_SIZE);
            out = new BufferedOutputStream(outputStream);

            int d;
            while ((d = in.read()) != -1) {
                out.write(d);
            }
            MyUtil.closeInStream(in);
            MyUtil.closeOutStream(out);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return false;
    }

    /**
     * 将图片添加入内存缓存
     *
     * @param url    每一个图片对应的url
     * @param bitmap 图片对应的Bitmap
     */
    private void addToMemoryCache(String url, Bitmap bitmap) {
        String key = MyUtil.hashKeyFromDisk(url);
        if (loadBitMapFromMemoryCache(key) != null) {
            mLruCache.put(key, bitmap);
        }
    }

    /**
     * 从内存中获取缓存的图片
     *
     * @param url 图片对应的url
     * @return 内存中存在则返回Bitmap，若不存在返回null
     */
    private Bitmap loadBitMapFromMemoryCache(String url) {
        String key = MyUtil.hashKeyFromDisk(url);
        return mLruCache.get(key);
    }


    /**
     * 从网络上下载图片，并保存到硬盘缓存
     *
     * @param url       url
     * @param reqWidth  image width
     * @param reqHeight image height
     * @return bitmap
     * @throws IOException 如果在UI线程调用该方法会抛出错误
     */
    private Bitmap loadBitmapFromHttp(String url, int reqWidth, int reqHeight) throws IOException {
        DiskLruCache.Editor editor = null;

        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("can not visit network from UI thread!");
        }

        if (mDiskLruCache == null) {
            return null;
        }

        String key = MyUtil.hashKeyFromDisk(url);
        try {
            editor = mDiskLruCache.edit(key);
            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
                if (downloadUrlToStream(url, outputStream)) {
                    editor.commit();
                } else {
                    editor.abort();
                }
            }
            mDiskLruCache.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return loadBitmapFromDiskCache(key, reqWidth, reqHeight);
    }

    /**
     * 从外部存储获取bitmap对象
     *
     * @param url       url
     * @param reqWith   imageView的宽
     * @param reqHeight imageView的高
     * @return bitmap
     */
    private Bitmap loadBitmapFromDiskCache(String url, int reqWith, int reqHeight) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.w(TAG, "load bitmap from UI Thread ,it's not recommend!");
        }
        if (mDiskLruCache == null) {
            return null;
        }
        String key = MyUtil.hashKeyFromDisk(url);
        Bitmap bitmap = null;
        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);

            if (snapshot != null) {
                FileInputStream inputStream = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
                FileDescriptor descriptor = inputStream.getFD();

                bitmap = MyUtil.decodeSampleBitmapFromFileDescriptor(descriptor, reqWith, reqHeight);
            }
            if (bitmap != null) {
                addToMemoryCache(key, bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
