package com.example.blindassistancesystem;


import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity implements GestureDetector.OnGestureListener {


    static {
        if(OpenCVLoader.initDebug()){
            Log.d("MainActivity: ","Opencv is loaded");
        }
        else {
            Log.d("MainActivity: ","Opencv failed to load");
        }
    }
    private Button real,storage;
    Button b;
    private CardView camera_button;
    private CardView storage_button;

    private float x1, x2, y1, y2;
    private static int MIN_DISTANCE = 150;
    public static int REQUEST_CODE;
    private GestureDetector gestureDetector;
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main2);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i==TextToSpeech.SUCCESS){
                    int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                    String s = "For Real time object detection, swipe up and say detect";
                    textToSpeech.setSpeechRate(1.0f);
                    int speech = textToSpeech.speak(s,TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });
        this.gestureDetector = new GestureDetector(MainActivity2.this, this);



        //  b=findViewById(R.id.button_1);







        // b.setOnClickListener(new View.OnClickListener() {
        //   @Override
        // public void onClick(View v) {
        //        final  Dialog dialog=new Dialog(v.getRootView().getContext());
        // View view = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.dialog_box,null);


        // real=(Button)view.findViewById(R.id.Real_Time_Button);
        // storage=(Button)view.findViewById(R.id.Storage_Button);
//
        //   dialog.setContentView(view);
        // dialog.setCancelable(true);

        // real.setOnClickListener(new View.OnClickListener() {
        // @Override
        // public void onClick(View v2) {
        //Intent intent=new Intent(MainActivity.this,CameraActivity.class);
        // startActivity(intent);
        // }
        // });

        // storage.setOnClickListener(new View.OnClickListener() {
        //  @Override
        //public void onClick(View v3) {
        // Intent intent=new Intent(MainActivity.this,Storage_Prediction.class);
        // startActivity(intent);

        // }
        //});

        /////// AlertDialog alertDialog=new AlertDialog();



        //dialog.show();


        //}
        //});

        camera_button=findViewById(R.id.camera);
        storage_button=findViewById(R.id.storage);





        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        storage_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Storage_Prediction.class);
                startActivity(intent);
            }
        });








        // select device and run
        // we successfully loaded model
        // before next tutorial
        // as we are going to predict in Camera Activity
        // Next tutorial will be about predicting using Interpreter

        // camera_button=findViewById(R.id.button_1);
        // camera_button.setOnClickListener(new View.OnClickListener() {
        // @Override
        // public void onClick(View v) {
        //    Intent intent=new Intent(MainActivity.this, MainActivity2.class);
        //     startActivity(intent);
        // }
        //  });




    }

    @Override
    public void onBackPressed() {
        finish();
    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {

        gestureDetector.onTouchEvent(event);
        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                x1=event.getX();
                y1=event.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2= event.getX();
                y2= event.getY();

                float valueX= x2-x1;
                float valueY=y2-y1;
                if(Math.abs(valueY)>MIN_DISTANCE) {
                    if(y1>y2){
                        voiceautomation();
                    }
                }
        }
        return super.onTouchEvent(event);
    }


    private void voiceautomation() {
        Intent voice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        voice.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "What do you want...");
        startActivityForResult(voice, 1);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> arrayList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (arrayList.get(0).toString().toLowerCase().equals("detect")) {
                Intent intent = new Intent(MainActivity2.this, CameraActivity.class);
                //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}