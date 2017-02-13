package cn.beeth0ven.photogallery;

import android.app.Application;
import android.content.Context;
import android.util.Log;


/**
 * Created by Air on 2017/2/2.
 */

public class MyApplication extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        MyApplication.context = this;
    }
}
