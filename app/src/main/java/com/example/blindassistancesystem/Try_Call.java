package com.example.blindassistancesystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Try_Call extends AppCompatActivity {

    Button button;

    String fruits[]={"Apple","Banana","Apricot","Orange","Water Melon","Apple","Banana","Apricot","Orange","Water Melon"};

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try_call);

        ActivityCompat.requestPermissions(Try_Call.this, new String[]{Manifest.permission.CALL_PHONE},100);

        button=(Button) findViewById(R.id.call_number);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone_number="01748703341";
                Intent phone_intent = new Intent(Intent.ACTION_CALL);

                // Set data of Intent through Uri by parsing phone number
                phone_intent.setData(Uri.parse("tel:" + phone_number));

                // start Intent 01848-391188
                startActivity(phone_intent);
            }
        });





    }
}