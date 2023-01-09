package com.example.blindassistancesystem;

import static android.content.Context.POWER_SERVICE;

import static com.example.blindassistancesystem.Dashboard.CALL_STATIC;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.widget.Toast;

import java.security.Provider;
import java.util.Locale;

public class CallReciever extends BroadcastReceiver {
    private TextToSpeech textToSpeech;
    static PhoneCall listener;
    public int voice=1;
    String nameNumber=" ";





    PhoneStateListener phoneStateListener=new PhoneStateListener();
    @Override
    public void onReceive(Context context, Intent intent) {

        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            String savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        } else {
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                state = TelephonyManager.CALL_STATE_IDLE;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                state = TelephonyManager.CALL_STATE_RINGING;
            }
            if (number != null && !number.isEmpty() && !number.equals("null") && state==TelephonyManager.CALL_STATE_RINGING) {
                phoneStateListener.onCallStateChanged(state,number);
                showToast(context, "NUMBER =>" + number);
                nameNumber=getContactName(context,number);
                if(!nameNumber.equals(null)){
                    textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int i) {
                            if(CALL_STATIC==1) {
                                if (i == TextToSpeech.SUCCESS) {
                                    int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                                    textToSpeech.setSpeechRate(0.75f);
                                    String text = nameNumber + " is calling. " + nameNumber + " is calling. " + nameNumber + " is calling. " + nameNumber + " is calling. " + nameNumber + " is calling. " +
                                            nameNumber + " is calling. " + nameNumber + " is calling. " + nameNumber + " is calling. " + nameNumber + " is calling. " + nameNumber + " is calling. ";
                                    int speech = textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);

                                }
                            }
                        }

                    });
                }
                showToast(context,"Name is: "+ nameNumber);
                return;
            }

        }
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    if(i==TextToSpeech.SUCCESS){
                        int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                        textToSpeech.setSpeechRate(0.75f);
                        String text=nameNumber + " is calling. " + nameNumber + " is calling. " +nameNumber + " is calling. " + nameNumber + " is calling. "+nameNumber + " is calling. "+
                                nameNumber + " is calling. " + nameNumber + " is calling. " +nameNumber + " is calling. " + nameNumber + " is calling. "+nameNumber + " is calling. ";
                        int speech = textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null);

                    }
                }

            });
        }




    }




    void showToast(Context context, String message){
        Toast toast=Toast.makeText(context,message,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    @SuppressLint("Range")
    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }





}
