package com.example.blindassistancesystem;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, SensorEventListener {
    private static final String TAG="MainActivity";

    private Mat mRgba;
    List<String>object_list;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    float p = 1000.0000000F;
    float q=1000.0000000F;
    float r=1000.00000000F;

    float x_axis,y_axis,z_axis;



    private Mat mGray;
    private CameraBridgeViewBase mOpenCvCameraView;
    private objectDetectorClass objectDetectorClass;
    String object=" ";
    TextToSpeech textToSpeech;
    int x =0;
    private BaseLoaderCallback mLoaderCallback =new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface
                        .SUCCESS:{
                    Log.i(TAG,"OpenCv Is loaded");
                    mOpenCvCameraView.enableView();
                }
                default:
                {
                    super.onManagerConnected(status);

                }
                break;
            }
        }
    };

    public CameraActivity(){
        Log.i(TAG,"Instantiated new "+this.getClass());
    }

    @Override
    public void onBackPressed() {
        //  x=1;
        // onDestroy();
        // Intent intent =new Intent(getApplicationContext(),MainActivity2.class);
        //startActivity(intent);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int MY_PERMISSIONS_REQUEST_CAMERA=0;
        // if camera permission is not given it will ask for it on device
        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(CameraActivity.this, new String[] {Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }

        object_list=new ArrayList<>();

        setContentView(R.layout.activity_camera);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mOpenCvCameraView=(CameraBridgeViewBase) findViewById(R.id.frame_Surface);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        try{
            // input size is 300 for this model
            objectDetectorClass=new objectDetectorClass(getAssets(),"ssd_mobilenet.tflite","labelmap.txt",300);
            Log.d("MainActivity","Model is successfully loaded");
        }
        catch (IOException e){
            Log.d("MainActivity","Getting some error");
            e.printStackTrace();
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        if (OpenCVLoader.initDebug()){
            //if load success
            Log.d(TAG,"Opencv initialization is done");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        else{
            //if not loaded
            Log.d(TAG,"Opencv is not loaded. try again");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0,this,mLoaderCallback);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView !=null){
            mOpenCvCameraView.disableView();
        }
        mSensorManager.unregisterListener(this);
    }

    public void onDestroy(){
        super.onDestroy();
        if(mOpenCvCameraView !=null){
            mOpenCvCameraView.disableView();
        }

        if(textToSpeech!=null)
        {
            textToSpeech.shutdown();
            textToSpeech.stop();
        }

    }



    public void onCameraViewStarted(int width ,int height){
        mRgba=new Mat(height,width, CvType.CV_8UC4);
        mGray =new Mat(height,width,CvType.CV_8UC1);
    }
    public void onCameraViewStopped(){
        mRgba.release();
    }
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){

        mRgba=inputFrame.rgba();
        mGray=inputFrame.gray();
        // Before watching this video please watch previous video of loading tensorflow lite model

        // now call that function
        Mat out=new Mat();


        out=objectDetectorClass.recognizeImage(mRgba);
        String s=objectDetectorClass.getObjectName();





        if( object_list.contains(s) && Math.abs(p-x_axis)<1.5 && Math.abs(q-y_axis)<1.5 && Math.abs(r-z_axis)<1.5 )
        {


        }


        else
        {
            if(object_list.contains(s))
            {
                object_list.clear();
            }
            try
            {
                Thread.sleep(500);
            }
            catch (Exception e)
            {

            }
            int ppp=1;

            object_list.clear();


            textToSpeech =new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status == TextToSpeech.SUCCESS  )
                    {
                        int lang=textToSpeech.setLanguage(Locale.ENGLISH);

                        textToSpeech.setSpeechRate(1.0f);

                        object=s;

                        int speech =textToSpeech.speak(s,TextToSpeech.QUEUE_FLUSH,null);
                    }



                }
            });
            object_list.add(s);






            p=x_axis;
            q=y_axis;
            r=z_axis;
        }












        return out;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        x_axis=event.values[0];
        y_axis=event.values[1];
        z_axis=event.values[2];

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}