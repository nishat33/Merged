package com.example.blindassistancesystem;

//import static com.example.blindassistancesystem.Dashboard.ISDISABLE;

import static com.example.blindassistancesystem.Dashboard.CALL_STATIC;
import static com.example.blindassistancesystem.Dashboard.REQUEST_CODE;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class Settings extends AppCompatActivity {
    RelativeLayout emergency;
    //Switch aSwitch;
    RelativeLayout font;

    SwitchCompat switchCompat;
    SwitchCompat callSwitchCompat;
    ImageButton imageButton;


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchCompat=findViewById(R.id.SoundSwitch);
        callSwitchCompat=findViewById(R.id.Calling);
        SharedPreferences sharedPreferences2=getSharedPreferences("callsave",MODE_PRIVATE);
        callSwitchCompat.setChecked(sharedPreferences2.getBoolean("callvalue",true));

        SharedPreferences sharedPreferences=getSharedPreferences("save",MODE_PRIVATE);
        switchCompat.setChecked(sharedPreferences.getBoolean("value",true));
        emergency=(RelativeLayout)findViewById(R.id.EmergencyCont);
        emergency.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this,EmergencyContact.class);
                startActivity(intent);
            }
        });



        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchCompat.isChecked())
                {
                    // When switch checked
                    REQUEST_CODE = -1;
                    SharedPreferences.Editor editor=getSharedPreferences("save",MODE_PRIVATE).edit();
                    editor.putBoolean("value",true);
                    editor.apply();
                    switchCompat.setChecked(true);
                }
                else
                {
                    // When switch unchecked
                    REQUEST_CODE=1;
                    SharedPreferences.Editor editor=getSharedPreferences("save",MODE_PRIVATE).edit();
                    editor.putBoolean("value",false);
                    editor.apply();
                    switchCompat.setChecked(false);
                }
            }
        }
        );

        callSwitchCompat.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (callSwitchCompat.isChecked())
                {
                    // When switch checked
                    CALL_STATIC = -1;
                    SharedPreferences.Editor editor=getSharedPreferences("callsave",MODE_PRIVATE).edit();
                    editor.putBoolean("callvalue",true);
                    editor.apply();
                    callSwitchCompat.setChecked(true);
                }
                else
                {
                    // When switch uncheckeds
                    CALL_STATIC=1;
                    SharedPreferences.Editor editor=getSharedPreferences("callsave",MODE_PRIVATE).edit();
                    editor.putBoolean("callvalue",false);
                    editor.apply();
                    callSwitchCompat.setChecked(false);
                }
            }
        });




    }



}