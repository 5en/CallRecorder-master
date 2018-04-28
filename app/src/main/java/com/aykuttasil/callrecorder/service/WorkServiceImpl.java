package com.aykuttasil.callrecorder.service;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;

import com.aykuttasil.callrecord.CallRecord;

/**
 * Created by john on 2018/4/27.
 */

public class WorkServiceImpl extends AbsWorkService {
    private final String TAG = WorkServiceImpl.class.getSimpleName();
    CallRecord callRecord;
    @Override
    public void startWork(Intent intent) {
        callRecord = new CallRecord.Builder(this)
                .setRecordFileName("CallRecorderTestFile")
                .setRecordDirName("CallRecorderTest")
                .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                .setShowSeed(true)
                .build();
        callRecord.enableSaveFile();

        callRecord.startCallRecordService();
        //
        Log.d(TAG,"--->startWork");
    }

    @Override
    public IBinder onBind(Intent intent, Void alwaysNull) {
        return null;
    }

    @Override
    public void onServiceKilled(Intent intent) {
        Log.d(TAG,"--->onServiceKilled");
        if (callRecord!=null){
            callRecord.stopCallReceiver();
        }
    }
}
