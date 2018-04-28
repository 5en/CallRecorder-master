package com.aykuttasil.callrecorder.service;

import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import static com.aykuttasil.callrecorder.service.ServiceHelper.mServiceClass;

/**
 * Created by john on 2018/4/27.
 */
/**
 * 守护服务，运行在:watch子进程中
 */
public class WatchDogService extends Service {
    private final String TAG = WatchDogService.class.getSimpleName();
    protected static final int HASH_CODE = 2;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"---->onBind");
        onStart(intent, 0, 0);
        return null;
    }


    @Override
    public final int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"---->onStartCommand");
        return onStart(intent, flags, startId);
    }


    private final int onStart(Intent intent, int flags, int startId){
        Log.d(TAG,"---->onStart");
        if (!ServiceHelper.mIsInitialized){
            return  START_STICKY;
        }
        JobInfo.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            builder = new JobInfo.Builder(HASH_CODE, new ComponentName(ServiceHelper.mContext, JobSchedulerService.class));
//            builder.setPeriodic(ServiceHelper.DEFAULT_WAKE_UP_INTERVAL);
//            builder.setPersisted(true);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) builder.setPeriodic(JobInfo.getMinPeriodMillis(), JobInfo.getMinFlexMillis());
//            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//            scheduler.schedule(builder.build());
        }

        //Android 7.0+ 增加了一项针对 JobScheduler 的新限制，最小间隔只能是下面设定的数字


        //守护 Service 组件的启用状态, 使其不被 MAT 等工具禁用
        getPackageManager().setComponentEnabledSetting(new ComponentName(getPackageName(), ServiceHelper.mServiceClass.getName()),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        return  START_STICKY;
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
