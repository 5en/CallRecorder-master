package com.aykuttasil.callrecorder;

import android.app.Application;

import com.aykuttasil.callrecorder.service.ServiceHelper;
import com.aykuttasil.callrecorder.service.WorkServiceImpl;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //需要在 Application 的 onCreate() 中调用一次 DaemonEnv.initialize()
        ServiceHelper.initialize(this, WorkServiceImpl.class);
        ServiceHelper.startServiceMayBind(WorkServiceImpl.class);
    }
}
