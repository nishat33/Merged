package com.example.blindassistancesystem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TextRead extends AppCompatActivity {
    TextView textView;
    EditText number, date_edit;
    Button number_button, date_button;
    TextToSpeech textToSpeech;
    List<String>getVal;
    // String fruits[]={"Apple","Banana","Apricot","Orange","Water Melon","Apple","Banana","Apricot","Orange","Water Melon"};

    ListView listView;
    String read_text="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_message);

        ActivityCompat.requestPermissions(TextRead.this, new String[]{Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);

        listView = (ListView) findViewById(R.id.list_view_id);

        List<String> fr = new ArrayList<>();
        fr.add("Apple");
        fr.add("Banana");

        //  ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,R.layout.list_view_holder,R.id.text_view_list_view,fr);
        // listView.setAdapter(arrayAdapter);


        number = findViewById(R.id.number_edit_text);
        date_edit = findViewById(R.id.date_number);
        number_button = findViewById(R.id.number_Of_text);
        date_button = findViewById(R.id.date);
        getVal=new ArrayList<>();

        number_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Number_Clicked();
            }
        });

        date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date_Clicked();
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();

        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
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

    public void Number_Clicked() {
        final Uri SMS_INBOX = Uri.parse("content://sms/inbox");


        Cursor cursor = getContentResolver().query(SMS_INBOX, null, "read=0", null, null);
        String sms = "";
        int x = 0;
        int maxi = 0;
        int f = 1;
        String num = number.getText().toString();
        String s = date_edit.getText().toString();

        int l=s.length();

        Boolean checkPoint = false;

        for (int i = 0; i < num.length(); i++) {
            if (num.charAt(i) == '.') {
                checkPoint = true;
                break;
            }
        }
        if (checkPoint) {
            Toast.makeText(getApplicationContext(), "Please Enter Decimal Number", Toast.LENGTH_LONG).show();
        } else if (num.equals("")) {
            //textView.setText("Please Enter Number");
            Toast.makeText(getApplicationContext(), "Number Field Cannot be Empty", Toast.LENGTH_LONG).show();
            f = 0;
            maxi = 0;


        } else {
            List<String> st = new ArrayList<>();

            if (s.equals("")) {
                maxi = Integer.parseInt(num);


                //Get all lines
                while (cursor.moveToNext()) {

                    if (x >= maxi) {

                        break;
                    }

                    @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex("address"));
                    @SuppressLint("Range") String person = cursor.getString(cursor.getColumnIndex("_id"));
                    @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));
                    @SuppressLint("Range") String protocol = cursor.getString(cursor.getColumnIndex("protocol"));
                    @SuppressLint("Range") String read = cursor.getString(cursor.getColumnIndex("read"));
                    @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex("status"));
                    @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex("type"));
                    @SuppressLint("Range") String subject = cursor.getString(cursor.getColumnIndex("subject"));
                    @SuppressLint("Range") String body = cursor.getString(cursor.getColumnIndex("body"));

                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
                    Calendar calendar = Calendar.getInstance();
                    long now = Long.parseLong(date);
                    calendar.setTimeInMillis(now);

                    date = calendar.getTime().toString();

                    /// String name =getContactName(TextRead.this,"01819434610");


                    ///// String Name=getContactName( this, "01819434610");

                    ///  GetName(getApplicationContext(),"01722147952");

                    sms = " From : " + address + "\n" + "Date : " + date + "\n" + body + '\n';
                    read_text=read_text+"\n"+sms+"\n";
                    st.add(sms);
                    //sms = sms + "\n";
                    x++;


                }

                number.setText("");


                if (f != 0) {
                    // textView.setText(sms);
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_view_holder, R.id.text_view_list_view, st);
                    listView.setAdapter(arrayAdapter);

                    String finalSms = read_text;
                    textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int i) {
                            if (i == TextToSpeech.SUCCESS) {
                                int lang = textToSpeech.setLanguage(Locale.ENGLISH);

                                textToSpeech.setSpeechRate(1.0f);
                                int speech = textToSpeech.speak(finalSms, TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    });
                }

            } else {

                if(s.charAt(l-1)!=' ')
                {
                    s+=" ";
                }
                maxi = Integer.parseInt(num);

                // Toast.makeText(getApplicationContext(),num +" "+s, Toast.LENGTH_LONG).show();

                //Get all lines
                while (cursor.moveToNext()) {

                    if (x >= maxi) {

                        break;
                    }
                    //Gets the SMS information
                    @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex("address"));
                    @SuppressLint("Range") String person = cursor.getString(cursor.getColumnIndex("_id"));
                    @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));
                    @SuppressLint("Range") String protocol = cursor.getString(cursor.getColumnIndex("protocol"));
                    @SuppressLint("Range") String read = cursor.getString(cursor.getColumnIndex("read"));
                    @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex("status"));
                    @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex("type"));
                    @SuppressLint("Range") String subject = cursor.getString(cursor.getColumnIndex("subject"));
                    @SuppressLint("Range") String body = cursor.getString(cursor.getColumnIndex("body"));

                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
                    Calendar calendar = Calendar.getInstance();
                    long now = Long.parseLong(date);
                    calendar.setTimeInMillis(now);

                    date = calendar.getTime().toString();

                    /// String name =getContactName(TextRead.this,"01819434610");


                    ///// String Name=getContactName( this, "01819434610");

                    ///


                    String ppp = "";
                    String year = "";
                    String year_entered = "";
                    String ppp1 = "";


                    for (int i = 0; i < s.length(); ++i) {
                        if (s.charAt(i) == '-' | s.charAt(i) == '/') {
                            ppp += " ";
                        } else {
                            ppp += s.charAt(i);
                        }

                    }


                    for (int i = 7; i < 12; i++) {
                        year_entered += ppp.charAt(i);
                    }


                    for (int i = 30; i < 34; i++) {
                        year += date.charAt(i);
                    }
                    Toast.makeText(getApplicationContext(),year_entered +" "+year,Toast.LENGTH_LONG).show();

                    for (int i = 0; i < 7; i++) {
                        ppp1 += ppp.charAt(i);
                    }




                    if (date.contains(ppp1) ) {
                        sms = " From : " + address + "\n" + "Date : " + date + "\n" + body + '\n';
                        st.add(sms);
                        read_text=read_text+"\n "+sms+"\n";
                        x++;

                    }


                }



                if (f != 0) {
                    // textView.setText(sms);
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_view_holder, R.id.text_view_list_view, st);
                    listView.setAdapter(arrayAdapter);
                    String finalSms = sms;
                    textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int i) {
                            if (i == TextToSpeech.SUCCESS) {
                                int lang = textToSpeech.setLanguage(Locale.ENGLISH);

                                textToSpeech.setSpeechRate(1.0f);
                                int speech = textToSpeech.speak(finalSms, TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    });
                }

            }

        }


    }

    public void Date_Clicked() {
        String s=date_edit.getText().toString();
        String num=number.getText().toString();
        // Toast.makeText(getApplicationContext(),s+" "+num,Toast.LENGTH_LONG).show();
        int l=s.length();

        if(s.charAt(l-1)!=' ')
        {
            s+=" ";
        }



        final Uri SMS_INBOX = Uri.parse("content://sms/inbox");


        Cursor cursor = getContentResolver().query(SMS_INBOX, null, "read=0", null, null);
        String sms = "";
        int x = 0;
        int maxi = 0;
        int f = 1;


        if (s.equals("")) {
            //textView.setText("Please Enter Number");
            Toast.makeText(getApplicationContext(), "Enter Valid Date", Toast.LENGTH_LONG).show();
            f = 0;
            maxi = 0;
        } else {


            List<String> st = new ArrayList<>();

            if (num.equals("")) {



                while (cursor.moveToNext()) {


                    //Gets the SMS information
                    @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex("address"));
                    @SuppressLint("Range") String person = cursor.getString(cursor.getColumnIndex("_id"));
                    @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));
                    @SuppressLint("Range") String protocol = cursor.getString(cursor.getColumnIndex("protocol"));
                    @SuppressLint("Range") String read = cursor.getString(cursor.getColumnIndex("read"));
                    @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex("status"));
                    @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex("type"));
                    @SuppressLint("Range") String subject = cursor.getString(cursor.getColumnIndex("subject"));
                    @SuppressLint("Range") String body = cursor.getString(cursor.getColumnIndex("body"));

                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
                    Calendar calendar = Calendar.getInstance();
                    long now = Long.parseLong(date);
                    calendar.setTimeInMillis(now);

                    date = calendar.getTime().toString();

                    String ppp = "";
                    String year = "";
                    String year_entered = "";
                    String ppp1 = "";


                    for (int i = 0; i < s.length(); ++i) {
                        if (s.charAt(i) == '-' | s.charAt(i) == '/') {
                            ppp += " ";
                        } else {
                            ppp += s.charAt(i);
                        }

                    }

                    for (int i = 7; i < 12; i++) {
                        year_entered += ppp.charAt(i);
                    }


                    for (int i = 30; i < 34; i++) {
                        year += date.charAt(i);
                    }

                    for (int i = 0; i < 7; i++) {
                        ppp1 += ppp.charAt(i);
                    }


                    Toast.makeText(getApplicationContext(),ppp+" "+ppp1,Toast.LENGTH_LONG).show();



                    if (date.contains(ppp1)) sms = "From : " + address + ".\n         " + "Date : " + date + ".\n         " +"Message: "+ body + '\n';


                }

                //number.setText("");
                // date_edit.setText("");

                if (f != 0) {
                    // textView.setText(sms);
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_view_holder, R.id.text_view_list_view, st);
                    listView.setAdapter(arrayAdapter);

                    String finalSms = read_text;
                    textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int i) {
                            if (i == TextToSpeech.SUCCESS) {
                                int lang = textToSpeech.setLanguage(Locale.ENGLISH);

                                textToSpeech.setSpeechRate(1.0f);
                                int speech = textToSpeech.speak(finalSms, TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    });
                }

            } else {
                maxi = Integer.parseInt(num);
                // Toast.makeText(getApplicationContext(),maxi,Toast.LENGTH_LONG).show();



                while (cursor.moveToNext()) {

                    if (x >= maxi) {
                        break;
                    }
                    //Gets the SMS information
                    @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex("address"));
                    @SuppressLint("Range") String person = cursor.getString(cursor.getColumnIndex("_id"));
                    @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));
                    @SuppressLint("Range") String protocol = cursor.getString(cursor.getColumnIndex("protocol"));
                    @SuppressLint("Range") String read = cursor.getString(cursor.getColumnIndex("read"));
                    @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex("status"));
                    @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex("type"));
                    @SuppressLint("Range") String subject = cursor.getString(cursor.getColumnIndex("subject"));
                    @SuppressLint("Range") String body = cursor.getString(cursor.getColumnIndex("body"));

                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
                    Calendar calendar = Calendar.getInstance();
                    long now = Long.parseLong(date);
                    calendar.setTimeInMillis(now);

                    date = calendar.getTime().toString();

                    String ppp = "";
                    String year = "";
                    String year_entered = "";
                    String ppp1 = "";


                    for (int i = 0; i < s.length(); ++i) {
                        if (s.charAt(i) == '-' | s.charAt(i) == '/') {
                            ppp += " ";
                        } else {
                            ppp += s.charAt(i);
                        }

                    }

                    for (int i = 7; i < 12; i++) {
                        year_entered += ppp.charAt(i);
                    }


                    for (int i = 30; i < 34; i++) {
                        year += date.charAt(i);
                    }

                    for (int i = 0; i < 7; i++) {
                        ppp1 += ppp.charAt(i);
                    }

                    Toast.makeText(getApplicationContext(),year_entered+" "+year,Toast.LENGTH_LONG).show();


                    if (date.contains(ppp1) ) {
                        sms = "From : " + address + ".\n         " + "Date : " + date + ".\n         " +"Message: "+ body + '\n';
                        st.add(sms);
                        // sms = sms + "\n";
                        x++;
                        read_text=read_text+" \n"+sms+'\n';

                        Toast.makeText(getApplicationContext(),sms,Toast.LENGTH_LONG).show();

                    }
                    f=1;



                }




                if (f != 0) {
                    //   textView.setText(sms);
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_view_holder, R.id.text_view_list_view, st);
                    listView.setAdapter(arrayAdapter);

                    String finalSms = read_text;
                    textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int i) {
                            if (i == TextToSpeech.SUCCESS) {
                                int lang = textToSpeech.setLanguage(Locale.ENGLISH);

                                textToSpeech.setSpeechRate(1.0f);
                                int speech = textToSpeech.speak(finalSms, TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    });
                }

            }

        }


    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String s = "";
            String num = "";
            ArrayList<String> arrayList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            s = arrayList.get(0).toString();
            char c =s.charAt(0);

            if(c>='0' && c<='9') {
                String datee = "";
                int i = 0;

                for (i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == ' ') {
                        break;
                    } else {
                        num += s.charAt(i);
                    }
                }
                if (i >= s.length()) {
                    number.setText(num);
                    Number_Clicked();
                } else {
                    while (i < s.length()) {
                        datee += s.charAt(i);
                        i++;
                    }
                    if(datee.length()<12)
                    {
                        Toast.makeText(getApplicationContext(),"Date Not Matched",Toast.LENGTH_LONG).show();
                        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int i) {
                                if (i == TextToSpeech.SUCCESS) {
                                    int lang = textToSpeech.setLanguage(Locale.ENGLISH);

                                    textToSpeech.setSpeechRate(1.0f);
                                    int speech = textToSpeech.speak("Date Not Matched", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }
                        });
                    }
                    else
                    {
                        String date_3 = "";
                        int j = 0;
                        for (j = 0; j < 4; j++) {
                            date_3 += datee.charAt(j);
                        }
                        while (datee.charAt(j) != ' ' && j < datee.length()) {
                            j++;
                        }

                        while (j < datee.length()) {
                            date_3 += datee.charAt(j);
                            j++;
                        }

                        int l = date_3.length();



                        for (l = date_3.length() - 1; l >= 0; l--) {
                            if (date_3.charAt(l) != ' ') {
                                break;
                            }
                        }
                        l++;

                        String date_final = "";

                        for (int k = 0; k < l; k++) {
                            date_final += date_3.charAt(k);
                        }


                        number.setText(num.toString());


                        date_edit.setText(date_final.toString());


                        Number_Clicked();
                    }
                }


            }

            else
            {
                Toast.makeText(getApplicationContext(),"Say Number Then Date",Toast.LENGTH_LONG).show();
                textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        if (i == TextToSpeech.SUCCESS) {
                            int lang = textToSpeech.setLanguage(Locale.ENGLISH);

                            textToSpeech.setSpeechRate(1.0f);
                            int speech = textToSpeech.speak("Syntax : Number Followed By date ", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                });


            }


}
}
}