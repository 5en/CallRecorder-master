package com.aykuttasil.callrecorder;

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.aykuttasil.callrecord.CallRecord;
import com.aykuttasil.callrecord.receiver.CallRecordReceiver;
import com.aykuttasil.callrecorder.audioTest.PhoneCallReceiver;
import com.aykuttasil.callrecorder.service.ServiceHelper;
import com.aykuttasil.callrecorder.service.WorkServiceImpl;

import java.io.File;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getSimpleName();

    CallRecord callRecord;
    MediaPlayer mediaPlayer;
    TextView listfile;
    PhoneCallReceiver phoneCallReceiver = new PhoneCallReceiver() ;
    //AudioC
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listfile = (TextView) findViewById(R.id.listfile);
        //callRecord = CallRecord.init(this);
        callRecord = new CallRecord.Builder(this)
                .setRecordFileName("CallRecorderTestFile")
                .setRecordDirName("CallRecorderTest")
                .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                .setShowSeed(true)
                .build();
        callRecord.enableSaveFile();
    }

    public void StartCallRecordClick(View view) {
        Log.i("CallRecord", "StartCallRecordClick");
        callRecord.startCallRecordService();


    }

    public void StopCallRecordClick(View view) {
        Log.i("CallRecord", "StopCallRecordClick");
        callRecord.stopCallReceiver();

    }
    public void StartCallListFile(View view) {
        Log.i("CallRecord", "StopCallRecordClick");
       //todo log处 MIUI/sound_recorder/call_rec目下所有文件

        File file = new File(Environment.getExternalStorageDirectory().getPath()+"/MIUI/sound_recorder/call_rec");
        if (!file.exists()){
            Log.d(TAG," MIUI/sound_recorder/call_rec doesn't exsit");
            return;
        }
        File[]arrayOfFile = file.listFiles();

        if (arrayOfFile==null){
            Log.d(TAG," MIUI/sound_recorder/call_rec have no file");
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0;i<arrayOfFile.length;i++){
            //listfile
            stringBuilder.append(arrayOfFile[i]);
        }
        listfile.setText(stringBuilder.toString());
        if (mediaPlayer==null){
            mediaPlayer = MediaPlayer.create(MainActivity.this, Uri.parse(arrayOfFile[arrayOfFile.length-1].getAbsolutePath()));

        }
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        mediaPlayer.start();
    }

    public void startCallReceiver(View view) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CallRecordReceiver.ACTION_IN);
        intentFilter.addAction(CallRecordReceiver.ACTION_OUT);
        registerReceiver(phoneCallReceiver , intentFilter);
    }

    public void stopCallReceiver(View view){

        unregisterReceiver(phoneCallReceiver);

    }

    public void startKeepServiceAlive(View view){

        ServiceHelper.initialize(this, WorkServiceImpl.class);
        ServiceHelper.startServiceMayBind(WorkServiceImpl.class);

    }

}
