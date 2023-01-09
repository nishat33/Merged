package com.example.blindassistancesystem;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Dashboard extends AppCompatActivity implements GestureDetector.OnGestureListener {


    CardView CameraButton, Place, ReadText;
    TextToSpeech textToSpeech;
    ImageButton Call, Emergency, Setting,Notify;
    Button button;
    String message;
    String cur_loc;
    Button police_line;
    Button emergency;
    CardView object_button;
    Button healcare;
     private ArrayList<Contact> contacts;

    private float x1, x2, y1, y2;
    private static int MIN_DISTANCE = 150;
    public static int REQUEST_CODE=1;
    public static int CALL_STATIC=1;
    private GestureDetector gestureDetector;

    FusedLocationProviderClient fusedLocationProviderClient;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toast.makeText(getApplicationContext(),"Code is" + REQUEST_CODE,Toast.LENGTH_LONG).show();
        if(REQUEST_CODE==1) {
            instruction();
        }
        this.gestureDetector = new GestureDetector(Dashboard.this, this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        )
        {
            getlocation();
        } else {
            ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
        }



        if (ContextCompat.checkSelfPermission(Dashboard.this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(Dashboard.this, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Dashboard.this,
                    new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.SYSTEM_ALERT_WINDOW},
                    1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            Toast.makeText(this, "no allowed", Toast.LENGTH_SHORT).show();
            // it is not enabled. Ask the user to do so from the settings.
        }else {
            Toast.makeText(this, "allowed", Toast.LENGTH_SHORT).show();
            // good news! It works fine even in the background.
        }







        // Toast.makeText(getApplicationContext(),num,Toast.LENGTH_LONG).show();




        Call = (ImageButton) findViewById(R.id.CallButton);
        Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 final  Dialog dialog=new Dialog(v.getRootView().getContext());
                 View view = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.dialog_box,null);
                 police_line=(Button)view.findViewById(R.id.Police_Line);
                 emergency=(Button)view.findViewById(R.id.EmergencY);
                 healcare=(Button)view.findViewById(R.id.health_care);
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                {
                    police_line.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v2) {
                            String phone_number="01848-391188";
                            Intent phone_intent = new Intent(Intent.ACTION_CALL);
                            phone_intent.setData(Uri.parse("tel:" + phone_number));
                            startActivity(phone_intent);
                        }
                    });
                    emergency.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v2) {

                            String phone_number="999";
                            Intent phone_intent = new Intent(Intent.ACTION_CALL);
                            phone_intent.setData(Uri.parse("tel:" + phone_number));
                            startActivity(phone_intent);
                        }
                    });

                    healcare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v2) {
                            String phone_number="01719-506944";
                            Intent phone_intent = new Intent(Intent.ACTION_CALL);
                            phone_intent.setData(Uri.parse("tel:" + phone_number));
                            startActivity(phone_intent);
                        }
                    });
                }
                else{
                    ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.CALL_PHONE}, 3);
                    Toast.makeText(getApplicationContext(),"permission nai",Toast.LENGTH_LONG).show();
                }




                  dialog.setContentView(view);
                  dialog.setCancelable(true);

                  dialog.show();

            }
        });

        ReadText=findViewById(R.id.Message);

        ReadText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Intent intent= new Intent(Dashboard.this,TextRead.class);
                startActivity(intent);

            }
        });


        Setting = (ImageButton) findViewById(R.id.Settings);
        Place=(CardView) findViewById(R.id.places);
        Notify=findViewById(R.id.noti_button);
        object_button=(CardView) findViewById(R.id.objectbutton);

        object_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //Intent intent=getPackageManager().getLaunchIntentForPackage("com.example.imagepro");
                Intent intent=new Intent(Dashboard.this,MainActivity2.class);
                startActivity(intent);
            }
        });



        Notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Uri SMS_INBOX = Uri.parse("content://sms/inbox");
                int x=0;
                Cursor cursor = getContentResolver().query(SMS_INBOX, null, "read=0", null, null);

                while (cursor.moveToNext())
                {
                    x++;
                }

                String pp=Integer.toString(x);
                String ss = "You Have "+x+ " Unread Text , If You Wanna Read Swipe Up And Say Text";
                Toast.makeText(Dashboard.this,"You Have : "+x+" Unread Text ",Toast.LENGTH_LONG).show();
                textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int i) {
                                    if (i == TextToSpeech.SUCCESS) {
                                        int lang = textToSpeech.setLanguage(Locale.ENGLISH);

                                        textToSpeech.setSpeechRate(1.0f);
                                        int speech = textToSpeech.speak(ss, TextToSpeech.QUEUE_FLUSH, null);
                                    }
                                }
                            });
                    }
        });


        Place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this,Maps.class);
                startActivity(intent);
            }
        });
        Emergency = (ImageButton) findViewById(R.id.AlertButton);

        Emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                sendtext();
                DatabaseHelper databaseHelper=new DatabaseHelper(getApplicationContext());

                ArrayList<Contact> arrayList=databaseHelper.fetchAlldata();
                String phone_number=arrayList.get(0).getNumber();
                Intent phone_intent = new Intent(Intent.ACTION_CALL);
                phone_intent.setData(Uri.parse("tel:" + phone_number));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                {
                    startActivity(phone_intent);
                }
                else{
                    ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.CALL_PHONE}, 3);
                    Toast.makeText(getApplicationContext(),"permission nai",Toast.LENGTH_LONG).show();
                }


            }
        });

        CameraButton =
                findViewById(R.id.TextButton);
        CameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, textDetection.class);
                startActivity(intent);
            }
        });
        Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, Settings.class);
                startActivity(intent);
            }
        });



    }



    private void sendtext() {



//        String Mobile="01714766854";
        DatabaseHelper databaseHelper=new DatabaseHelper(getApplicationContext());

        ArrayList<Contact> arrayList=databaseHelper.fetchAlldata();

        Integer size=arrayList.size();

        for(int i=0;i<size;i++){

            String Mobile=arrayList.get(i).getNumber();
            Toast.makeText(Dashboard.this,Mobile,Toast.LENGTH_LONG).show();
            String text = message;
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(Mobile, null, text, null, null);
                Toast.makeText(getApplicationContext(), "Message Sent",
                        Toast.LENGTH_LONG).show();
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(),e.getMessage().toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }


//        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
//        SmsManager smsManager = SmsManager.getDefault();
//        if (ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
//            smsManager.sendTextMessage(Mobile, null, text, pendingIntent, null);
//            Toast.makeText(Dashboard.this, "Message sent", Toast.LENGTH_LONG).show();
//        } else {
//            ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.SEND_SMS},90);
//            Toast.makeText(Dashboard.this, "permission nai", Toast.LENGTH_LONG).show();
//        }



    }

    @SuppressLint("MissingPermission")
    private void getlocation() {
//        address.setText("hello");

//
       fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
           @Override
           public void onComplete(@NonNull Task<Location> task) {
               Location location = task.getResult();
               if (location != null) {

                   try {
                       Geocoder geocoder = new Geocoder(Dashboard.this, Locale.getDefault());
                       List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
                       cur_loc="Your Current Location Is "+ addresses.get(0).getAddressLine(0).toString();
                    message="Send help, I am in danger, I am currently in \n"+ "Locality: "+ addresses.get(0).getLocality()+","+addresses.get(0).getSubLocality()+","+ "\nAddress: " +addresses.get(0).getAddressLine(0);
                  //address.setText(message);
                    String phone = "01816579009";
                   }
                   catch (IOException e) {
                       e.printStackTrace();
                   }

               }

           }
       });
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
                            if (textToSpeech != null) {
                                textToSpeech.shutdown();
                            }
                            voiceautomation();
                        }
                        else{
                            textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int i) {
                                    if(i==TextToSpeech.SUCCESS){
                                        int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                                        String s = "Welcome to 'Third Eye'. Swipe up to open voice command and " +
                                                "speak \"read\" to scan and read aloud a text. "+
                                                "speak \"text\" to read the unread messages. "+
                                                "speak \"location\" to learn your current location. " +
                                                "speak \"maps\" to open google maps." +
                                                "speak \"settings\" to open settings." +
                                                "speak 'notification' to learn total unread texts. " +
                                                "speak 'emergency contact' to go to emergency contact" +
                                                "speak 'help' to send your location to your emergency contact." +
                                                "speak 'police' to call police line." +
                                                "speak 'emergency helpline' to call 999." +
                                                "speak 'healthcare' to call healthcare service.";

                                        textToSpeech.setSpeechRate(1.0f);
                                        int speech = textToSpeech.speak(s,TextToSpeech.QUEUE_FLUSH,null);
                                    }
                                }
                            });
                        }
                    }
            }
        return super.onTouchEvent(event);
    }

    private void instruction() {
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i==TextToSpeech.SUCCESS){
                    int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                    String s = "Swipe up to open voice command. Swipe down to get a demonstration of the app.";
                    textToSpeech.setSpeechRate(1.0f);
                    int speech = textToSpeech.speak(s,TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });
    };


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
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode()==KeyEvent.KEYCODE_POWER){
            String Mobile="+8801748703341";
            String text=message;
            Intent intent=new Intent(getApplicationContext(),Dashboard.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,0);
            SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(Mobile,null,text,pendingIntent,null);
                Toast.makeText(Dashboard.this, "Message sent", Toast.LENGTH_LONG).show();

        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data !=null){
            ArrayList<String> arrayList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (arrayList.get(0).toString().toLowerCase().equals("read")){
                Intent intent = new Intent(Dashboard.this, textDetection.class);
                //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(intent);
            }
            else if (arrayList.get(0).toString().toLowerCase().equals("settings")){
                Intent intent = new Intent(Dashboard.this, Settings.class);
                //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(intent);
            }
             else if (arrayList.get(0).toString().toLowerCase().equals("text")){
                Intent intent = new Intent(Dashboard.this, TextRead.class);
                //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(intent);
            }
             else if(arrayList.get(0).toString().toLowerCase().equals("help"))
            {
                DatabaseHelper helper = new DatabaseHelper(getApplicationContext());


                ArrayList<Contact>x=helper.fetchAlldata();
                String num=helper.getNumber().toString();

                if(num.equals("Nothing"))
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Emergency Contact",Toast.LENGTH_LONG).show();
                }

                else
                {
                    String phone_number=num;
                    sendtext();
                    Intent phone_intent = new Intent(Intent.ACTION_CALL);

                    // Set data of Intent through Uri by parsing phone number
                    phone_intent.setData(Uri.parse("tel:" + phone_number));

                    // start Intent
                    startActivity(phone_intent);
                }
            }

             else if (arrayList.get(0).toString().equals("location")){
                Toast.makeText(getApplicationContext(), cur_loc, Toast.LENGTH_LONG).show();
                 textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                    public void onInit(int i) {
                        if(i==TextToSpeech.SUCCESS){
                            int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                            textToSpeech.setSpeechRate(1.0f);
                            int speech = textToSpeech.speak(cur_loc,TextToSpeech.QUEUE_FLUSH,null);
                        }
                    }
                });
            }
            else if(arrayList.get(0).toString().toLowerCase().equals("notification"))
            {
                final Uri SMS_INBOX = Uri.parse("content://sms/inbox");
                int x=0;
                Cursor cursor = getContentResolver().query(SMS_INBOX, null, "read=0", null, null);

                while (cursor.moveToNext())
                {
                    x++;
                }

                String pp=Integer.toString(x);
                String ss = "You Have "+x+ " Unread Text , If You Wanna Read Swipe Up And Say Text";
                Toast.makeText(Dashboard.this,"You Have : "+x+" Unread Text ",Toast.LENGTH_LONG).show();
                textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        if (i == TextToSpeech.SUCCESS) {
                            int lang = textToSpeech.setLanguage(Locale.ENGLISH);

                            textToSpeech.setSpeechRate(1.0f);
                            int speech = textToSpeech.speak(ss, TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                });
            }

                else if (arrayList.get(0).toString().toLowerCase().equals("healthcare")){
                String phone_number="01719-506944";
                 Intent phone_intent = new Intent(Intent.ACTION_CALL);

                    // Set data of Intent through Uri by parsing phone number
                     phone_intent.setData(Uri.parse("tel:" + phone_number));

                // start Intent
                startActivity(phone_intent);
            }

             else if (arrayList.get(0).toString().toLowerCase().equals("call HealthCare")){
                            String phone_number="01719-506944";
                            Intent phone_intent = new Intent(Intent.ACTION_CALL);

                            // Set data of Intent through Uri by parsing phone number
                            phone_intent.setData(Uri.parse("tel:" + phone_number));

                             // start Intent 01848-391188
                            startActivity(phone_intent);

            }

                 else if (arrayList.get(0).toString().toLowerCase().equals("call police")){
                     String phone_number="01848-391188";
                 Intent phone_intent = new Intent(Intent.ACTION_CALL);

            // Set data of Intent through Uri by parsing phone number
            phone_intent.setData(Uri.parse("tel:" + phone_number));

            // start Intent 01848-391188
            startActivity(phone_intent);

            }

                    else if (arrayList.get(0).toString().toLowerCase().equals("call police line")){
                     String phone_number="01848-391188";
                 Intent phone_intent = new Intent(Intent.ACTION_CALL);

            // Set data of Intent through Uri by parsing phone number
            phone_intent.setData(Uri.parse("tel:" + phone_number));

            // start Intent 01848-391188
            startActivity(phone_intent);

            }
                    else if (arrayList.get(0).toString().toLowerCase().equals("call emergency helpline")){
                     String phone_number="999";
                 Intent phone_intent = new Intent(Intent.ACTION_CALL);

            // Set data of Intent through Uri by parsing phone number
            phone_intent.setData(Uri.parse("tel:" + phone_number));

            // start Intent 01848-391188
            startActivity(phone_intent);

            }

                             else if (arrayList.get(0).toString().toLowerCase().equals("emergency helpline")){
                     String phone_number="999";
                 Intent phone_intent = new Intent(Intent.ACTION_CALL);

            // Set data of Intent through Uri by parsing phone number
            phone_intent.setData(Uri.parse("tel:" + phone_number));

            // start Intent 01848-391188
            startActivity(phone_intent);

            } else if(arrayList.get(0).toString().toLowerCase().equals("maps")){
                                 Intent intent = new Intent(Dashboard.this, Maps.class);
                                 startActivity(intent);


            }
             else if(arrayList.get(0).toString().toLowerCase().equals("emergency contact")){
                Intent intent = new Intent(Dashboard.this, EmergencyContact.class);
                startActivity(intent);
            }
            else if(arrayList.get(0).toString().toLowerCase().equals("object")){
                Intent intent=getPackageManager().getLaunchIntentForPackage("com.example.imagepro");
                startActivity(intent);
            }




        }

    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    void showToast(Context context, String message){
        Toast toast=Toast.makeText(context,message,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
    }
}