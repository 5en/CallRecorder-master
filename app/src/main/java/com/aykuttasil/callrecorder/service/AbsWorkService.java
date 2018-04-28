package com.aykuttasil.callrecorder.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.aykuttasil.callrecorder.service.ServiceHelper.mServiceClass;

/**
 * Created by john on 2018/4/27.
 */

public abstract class AbsWorkService extends Service {

    private final String TAG = AbsWorkService.class.getSimpleName();
    public  abstract  void startWork(Intent intent);
    //如果有通信实现这个方法
    public abstract IBinder onBind (Intent intent,Void alwaysNull);

    // 外部要处理存储什么数据
    public abstract void onServiceKilled(Intent intent);
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"---->onBind");
        onStart(intent,0,0);
        return onBind(intent,null);
    }
    /**
     * 功能：
     * 1.防止重复启动， 新建map. 有key就不启动了
     * 2.启动前台进程，
     * 3.启动守护服务。
     * 4.运行定时任务。
     * 5.
     */

    protected  int onStart(Intent intent,int flag,int startId){
        //todo 启动守护服务 ，开启子进程 watch(???)

        ServiceHelper.startServiceMayBind(WatchDogService.class); //为什么不直接用service ，，service不能用New只能反射

        // 业务逻辑 ？？停止服务在这？？
         startWork(intent);
        return  START_STICKY;

    }

    @Override
    public int onStartCommand(Intent intent,int flag,int startId){
        Log.d(TAG,"---->onStartCommand");
        return onStart(intent,flag,startId);
    }

    /**
     * 最近任务列表中化掉卡片时候回调用
     * */

    @Override
    public void onTaskRemoved(Intent rootIntent){
        Log.d(TAG,"---->onTaskRemoved");
        onEnd(rootIntent);
    }

    /**
     *  服务快被杀的时候调用
     * */
    private void onEnd(Intent intent){

        onServiceKilled(intent);
        //todo 初始化判断
        //开自身线程
        ServiceHelper.startServiceMayBind(mServiceClass);
        //守护线程
        ServiceHelper.startServiceMayBind(WatchDogService.class);
    }

    @Override
    public void onDestroy(){
        Log.d(TAG,"---->onDestroy");
        onEnd(null);
    }
}
