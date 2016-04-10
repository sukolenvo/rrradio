package com.dakare.radiorecord.app;

import android.app.Application;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class RecordApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(this).build());
    }
}
