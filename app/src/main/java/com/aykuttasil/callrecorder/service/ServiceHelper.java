package com.aykuttasil.callrecorder.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.HashMap;

/**
 * Created by john on 2018/4/27.
 */

public class ServiceHelper {
    static Class<? extends AbsWorkService> mServiceClass;
    public static final int DEFAULT_WAKE_UP_INTERVAL = 6 * 60 * 1000;
    static Context mContext;
    static boolean mIsInitialized = false;
    static HashMap<Class<?extends Service>,ServiceConnection>  mBindConnectionMap= new HashMap<>();
    /**
     * 定时唤醒
     * 初始化基本service类，
     * */

    public  static  void initialize(Context app,Class<? extends AbsWorkService> childClass){
        mContext =  app;
        mServiceClass = childClass;
        mIsInitialized = true;
    }

    public static void startServiceMayBind(final Class serviceClass){

        if (!mIsInitialized){
            throw  new NullPointerException("未初始化ServiceHelper");
        }
        final Intent intent = new Intent(mContext,serviceClass);
        startServiceSagely(intent);
        ServiceConnection serviceConnection = mBindConnectionMap.get(serviceClass);
        if (serviceConnection == null){
            mContext.bindService(intent, new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    mBindConnectionMap.put(serviceClass,this);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    mBindConnectionMap.remove(serviceClass);
                    startServiceSagely(intent);
                    if (!mIsInitialized) return;
                    mContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
                }
            },Context.BIND_AUTO_CREATE);
        }

    };



    private static void startServiceSagely(Intent intent){
        try { mContext.startService(intent); } catch (Exception ignored) {}
    }

}
