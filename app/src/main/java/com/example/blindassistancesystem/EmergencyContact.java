package com.example.blindassistancesystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Locale;

public class EmergencyContact extends AppCompatActivity {

    private ArrayList<Contact> contacts;
    private RecyclerView recycler;
    private Button addButton;
    private Button delete;
    EditText NameText;
    EditText NumberText;
    Integer nameIn=0, NumberIn=0;
    RecyclerAdapter recyclerAdapter;
    AlertDialog.Builder builder;
    TextToSpeech textToSpeech;
    private RecyclerAdapter.RecyclerViewClicklistener listener;
    private float x1, x2, y1, y2;
    private static int MIN_DISTANCE = 150;
    String InName, InNumber;
    private GestureDetector gestureDetector;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);

        recycler = findViewById(R.id.Recycler_view);
        contacts = new ArrayList<>();

        setContact();
        setAdapter();
        addButton = (Button) findViewById(R.id.addContact);
        recyclerAdapter = new RecyclerAdapter(contacts);


        NameText = findViewById(R.id.Name_Edit);
        NumberText = findViewById(R.id.Number_Edit);
        delete=findViewById(R.id.delete);
        addButton.setOnClickListener(new View.OnClickListener() {
            String name = " ";
            String number= " ";
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                name = NameText.getText().toString();
                number = NumberText.getText().toString();
                int NameLen = name.length();
                int NumberLen = number.length();
                boolean bool = onlyDigits(number, NumberLen);

                if(NameLen==0) {
                    Toast.makeText(EmergencyContact.this, "please fill in", Toast.LENGTH_LONG).show();
                    textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int i) {
                            if(i==TextToSpeech.SUCCESS){
                                int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                                String s = "Name field can not be empty";

                                textToSpeech.setSpeechRate(1.0f);
                                int speech = textToSpeech.speak(s,TextToSpeech.QUEUE_FLUSH,null);
                            }
                        }
                    });
                }
                else if (NumberLen==0){
                    Toast.makeText(EmergencyContact.this, "please fill in", Toast.LENGTH_LONG).show();
                    textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int i) {
                            if(i==TextToSpeech.SUCCESS){
                                int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                                String s = "Number field can not be empty";

                                textToSpeech.setSpeechRate(1.0f);
                                int speech = textToSpeech.speak(s,TextToSpeech.QUEUE_FLUSH,null);
                            }
                        }
                    });
                }
                else if(!bool){
                    textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int i) {
                            if(i==TextToSpeech.SUCCESS){
                                int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                                String s = "Number field can only contain digits";

                                textToSpeech.setSpeechRate(1.0f);
                                int speech = textToSpeech.speak(s,TextToSpeech.QUEUE_FLUSH,null);
                            }
                        }
                    });

                }
                else{
                    Toast.makeText(EmergencyContact.this, name+ number, Toast.LENGTH_LONG).show();
                    Contact contact = new Contact();
                    Toast.makeText(EmergencyContact.this, String.valueOf(bool),Toast.LENGTH_LONG).show();
                    contact.setName(name);
                    contact.setNumber(number);
                    DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
                    helper.AddRecord(contact);
                    Toast.makeText(EmergencyContact.this,name+" "+number,Toast.LENGTH_LONG).show();
                    contacts = helper.fetchAlldata();
                    Log.d("tag", String.valueOf(contacts.size()));
                    Toast.makeText(EmergencyContact.this, String.valueOf(contacts.size()), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(EmergencyContact.this, EmergencyContact.class);
                    startActivity(intent);
                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                    itemTouchHelper.attachToRecyclerView(recycler);
                }
            }
        });

    }



    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT|ItemTouchHelper.DOWN | ItemTouchHelper.UP) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //Remove swiped item from list and notify the RecyclerView
            int position = viewHolder.getAdapterPosition();
            contacts.remove(position);
            recyclerAdapter.notifyDataSetChanged();
            Toast.makeText(EmergencyContact.this, "on Swiped ", Toast.LENGTH_SHORT).show();


        }
    };
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
                    if(y1<y2){
                        voiceautomation();
                    }
                    else{

                    }
                }
        }
        return super.onTouchEvent(event);
    }

    private void setContact() {

        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        contacts = databaseHelper.fetchAlldata();
    }


    private void setAdapter() {
        setOnclickListener();
        recyclerAdapter = new RecyclerAdapter(contacts);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(layoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(recyclerAdapter);

    }

    private void setOnclickListener() {

    }
    public static boolean onlyDigits(String str, int n)
    {
        for (int i = 0; i < n; i++) {
            if (str.charAt(i) < '0' || str.charAt(i) > '9') {
                return false;
            }
        }
        return true;
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> arrayList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(arrayList.get(0).toString().toLowerCase().equals("name")){
                getName();
              InName  = arrayList.get(0).toString().toLowerCase();
                Toast.makeText(EmergencyContact.this,InName,Toast.LENGTH_LONG).show();

            }
            else if(arrayList.get(0).toString().toLowerCase().equals("number")){
                getNumber();
               InNumber  = arrayList.get(0).toString().toLowerCase().replaceAll("\\s", "");
               Toast.makeText(EmergencyContact.this,InNumber,Toast.LENGTH_LONG).show();
            }
            else if(arrayList.get(0).toString().toLowerCase().equals("add")){
                int NameLen = InName.length();
                int NumberLen = InNumber.length();
                boolean bool = onlyDigits(InNumber, NumberLen);

                if(NameLen==0) {
                    Toast.makeText(EmergencyContact.this, "please fill in", Toast.LENGTH_LONG).show();
                    textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int i) {
                            if(i==TextToSpeech.SUCCESS){
                                int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                                String s = "Name field can not be empty";

                                textToSpeech.setSpeechRate(1.0f);
                                int speech = textToSpeech.speak(s,TextToSpeech.QUEUE_FLUSH,null);
                            }
                        }
                    });
                }
                else if (NumberLen==0){
                    Toast.makeText(EmergencyContact.this, "please fill in", Toast.LENGTH_LONG).show();
                    textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int i) {
                            if(i==TextToSpeech.SUCCESS){
                                int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                                String s = "Number field can not be empty";

                                textToSpeech.setSpeechRate(1.0f);
                                int speech = textToSpeech.speak(s,TextToSpeech.QUEUE_FLUSH,null);
                            }
                        }
                    });
                }
                else if(!bool){
                    textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int i) {
                            if(i==TextToSpeech.SUCCESS){
                                int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                                String s = "Number field can only contain digits";

                                textToSpeech.setSpeechRate(1.0f);
                                int speech = textToSpeech.speak(s,TextToSpeech.QUEUE_FLUSH,null);
                            }
                        }
                    });

                }
                else{
                    Toast.makeText(EmergencyContact.this, InName + InNumber, Toast.LENGTH_LONG).show();
                    Contact contact = new Contact();
                    Toast.makeText(EmergencyContact.this, String.valueOf(bool),Toast.LENGTH_LONG).show();
                    contact.setName(InName);
                    contact.setNumber(InNumber);
                    DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
                    helper.AddRecord(contact);
                    Toast.makeText(EmergencyContact.this,InName+" "+InNumber,Toast.LENGTH_LONG).show();
                    contacts = helper.fetchAlldata();
                    Log.d("tag", String.valueOf(contacts.size()));
                    Toast.makeText(EmergencyContact.this, String.valueOf(contacts.size()), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(EmergencyContact.this, EmergencyContact.class);
                    startActivity(intent);
                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                    itemTouchHelper.attachToRecyclerView(recycler);
                }
            }



        }
    }
    private void getName() {
        Intent voice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        voice.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "The Name: ");
        startActivityForResult(voice, 1);
    }

    private void getNumber() {
        Intent voice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        voice.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "The Number: ");
        startActivityForResult(voice, 1);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                voiceautomation();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                voiceautomation();
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }



}




