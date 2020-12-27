package com.example.funclient;



import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.example.funserveraidl.AIDLFunServer;

import java.io.ByteArrayOutputStream;


public class MainActivity extends Activity {

    private AIDLFunServer Aidlserver;                            //instance of the AIDL class generated
    private boolean mIsBound = false;
    private Button mOpen ;
    private ImageView mImage;
    private Button mPlay;
    private Button mPause;
    private Button mResume;
    private Button mStop;
    private EditText imageNumber;
    public Bitmap bitmap;
    private String key;
    private EditText songNumber;
    private String songKey;

    private Intent serviceIntent;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("MainActivity","onCreate");
        Log.i("MainActivity","imagenumber:"+key);
        Log.i("MainActivity","songnumber"+songKey);
        mPlay = (Button) findViewById(R.id.play);
        mPause = (Button) findViewById(R.id.pause);
        mResume = (Button) findViewById(R.id.resume);
        mStop = (Button) findViewById(R.id.stop);

        mImage = (ImageView) findViewById(R.id.fun_image);
        imageNumber = (EditText) findViewById(R.id.image_number);
        mOpen = (Button) findViewById(R.id.open);
        songNumber = (EditText) findViewById(R.id.song_number);
        mPlay.setOnClickListener(new View.OnClickListener() {                                           // when you click the play button
            @Override
            public void onClick(View view) {                                    // when you click on the Play button
                Log.i("Main","song n:"+songKey);
                songKey = songNumber.getText().toString();
                Log.i("Main","song n1:"+songKey);
                if(!songKey.isEmpty() && songKey != null && (songKey.equals("1") || songKey.equals("2") || songKey.equals("3"))){
                    try{
                        if(mIsBound){
                            Aidlserver.playSong(Integer.parseInt(songKey));
                        }else {
                            Toast.makeText(getApplicationContext(),"Service is not bound",Toast.LENGTH_LONG).show();
                        }
                    }
                    catch(RemoteException e){
                        e.toString();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please enter a valid song number",Toast.LENGTH_LONG).show();
                }
            }
        });
        mPause.setOnClickListener(new View.OnClickListener() {                                            // when you click the pause button
            @Override
            public void onClick(View view) {                                    // when you click on the Pause button
                songKey = songNumber.getText().toString();
                try{
                    if(mIsBound){
                        if(!songKey.isEmpty() && songKey != null && (songKey.equals("1") || songKey.equals("2") || songKey.equals("3"))){

                            Aidlserver.pauseSong(Integer.parseInt(songKey));
                        }else{
                            Toast.makeText(getApplicationContext(),"Enter a valid song number to pause",Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"Service is not bound",Toast.LENGTH_LONG).show();
                    }
                }
                catch(RemoteException e){
                    e.toString();
                }
            }
        });

        mResume.setOnClickListener(new View.OnClickListener() {                                       //when you click the Resume button
            @Override
            public void onClick(View view) {                                    // when you click on the Resume button

                songKey = songNumber.getText().toString();
                try{
                    if(mIsBound){
                        if(!songKey.isEmpty() && songKey != null && (songKey.equals("1") || songKey.equals("2") || songKey.equals("3"))){
                            Aidlserver.resumeSong(Integer.parseInt(songKey));
                        }else{
                            Toast.makeText(getApplicationContext(),"Enter a valid song number to Resume",Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"Service is not bound",Toast.LENGTH_LONG).show();
                    }
                }
                catch(RemoteException e){
                    e.toString();
                }

            }
        });
        mStop.setOnClickListener(new View.OnClickListener() {                                               // when you click on the stop button
            @Override

            public void onClick(View view) {                                    // when you click on the Stop button
                songKey = songNumber.getText().toString();
                try{
                    if(mIsBound){
                        if(!songKey.isEmpty() && songKey != null && (songKey.equals("1") || songKey.equals("2") || songKey.equals("3"))){
                            Aidlserver.stopSong(Integer.parseInt(songKey));
                        }else{
                            Toast.makeText(getApplicationContext(),"Enter a valid song number to Stop",Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"Service is not bound",Toast.LENGTH_LONG).show();
                    }
                }
                catch(RemoteException e){
                    e.toString();
                }

            }
        });

        mOpen.setOnClickListener(new View.OnClickListener() {                                     // when you click on the SHOW button
            @Override
            public void onClick(View view) {                                    // when you wan to open the image
                key = imageNumber.getText().toString();

                if(!key.isEmpty() && key != null && (key.equals("1") || key.equals("2") || key.equals("3"))) {

                    try {
                        if(mIsBound) {
                            bitmap = Aidlserver.getImage(Integer.parseInt(key));
                            mImage.setImageBitmap(bitmap);
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Service is not bound",Toast.LENGTH_LONG).show();
                        }

                    } catch (RemoteException e) {
                        e.toString();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please enter a valid image number",Toast.LENGTH_LONG).show();
                }
            }
        });
        serviceIntent = new Intent(AIDLFunServer.class.getName());                             // intent for service
        ResolveInfo info = getPackageManager().resolveService(serviceIntent, 0);
        serviceIntent.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));
        startForegroundService(serviceIntent);                               // Starting the foreground service
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!mIsBound){                                           // checking the bind in on start and if the client is not bind to the server, it will perform the binding action
            boolean b = false;
            Intent i = new Intent(AIDLFunServer.class.getName());

            ResolveInfo info = getPackageManager().resolveService(i,0);
            i.setComponent(new ComponentName(info.serviceInfo.packageName,info.serviceInfo.name));

            b = bindService(i, this.mConnection, Context.BIND_AUTO_CREATE);
            if(b){
                Log.i("MainActivity:","Service bounded");
            }else{
                Log.i("MainActivity:","Service not bounded");
            }
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        if(songNumber.getText() == null){
            if(songKey != null){
                songNumber.setText(songKey);
            }
        }
        if(mImage.getDrawable() == null){
            if(bitmap != null){
                mImage.setImageBitmap(bitmap);
            }
        }
        if(imageNumber.getText() == null){
            if(key != null){
                imageNumber.setText(key);
            }
        }
        mPause.setEnabled(true);
        mResume.setEnabled(true);
        mStop.setEnabled(true);
    }
    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        super.onPause();

        if(mIsBound){
            unbindService(this.mConnection);                    // unbinding from the service
            mIsBound = false;
        }

    }

    private final ServiceConnection mConnection = new ServiceConnection() {                                          // Implementation of service connection between client and server
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            Aidlserver = AIDLFunServer.Stub.asInterface(iBinder);
            mIsBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("Main:","Service disconnected");
            Aidlserver = null;
            mIsBound = false;

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        key = null;
        songKey = null;
        bitmap = null;
       if(mIsBound){
            unbindService(this.mConnection);
            mIsBound = false;

        }else{
            try {
                if(songNumber.getText() != null) {
                    Aidlserver.stopSong(Integer.parseInt(songNumber.getText().toString()));
                }
            }catch (RemoteException e){
                e.toString();
            }
        }
        try {
            Log.i("Mainactivity:","stopping service");
            Aidlserver.stopServ();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        startForegroundService(serviceIntent);
        stopService(serviceIntent);
    }
}