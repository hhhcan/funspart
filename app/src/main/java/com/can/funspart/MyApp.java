package com.can.funspart;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.multidex.MultiDex;

import com.apkfuns.logutils.LogLevel;
import com.apkfuns.logutils.LogUtils;
import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import cn.share.can.cyghttp.app.CygApplication;

public class MyApp extends CygApplication {

    public static String IMEI;
    public static String PACKAGE_NAME;
    public static String VERSION_NAME;
    public static String CHANNEL_ID;
    public static String ANDROID_ID;
    public static String SERIAL_NO;

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getInstance();
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(getApplicationContext())
                .setDownsampleEnabled(true)   // 对图片进行自动缩放
                .setResizeAndRotateEnabledForNetwork(true)    // 对图片进行自动缩放
                .setBitmapsConfig(Bitmap.Config.RGB_565) //  //图片设置RGB_565，减小内存开销  fresco默认情况下是RGB_8888
                //other settings
                .build();
        Fresco.initialize(this, config);

        LogUtils.getLogConfig()
                .configAllowLog(BuildConfig.DEBUG)
                .configTagPrefix(this.getPackageName())
                .configShowBorders(true)
                .configFormatTag("%d{HH:mm:ss:SSS} %t %c{-5}")
                .configLevel(LogLevel.TYPE_VERBOSE);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory();
        }
        Glide.get(this).trimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //内存低的时候，清理Glide的缓存
        Glide.get(this).clearMemory();
    }

}
