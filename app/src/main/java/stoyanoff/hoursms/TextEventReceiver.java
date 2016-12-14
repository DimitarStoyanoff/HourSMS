package stoyanoff.hoursms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Created by Stoyanoff on 23/6/2016.
 */
public class TextEventReceiver extends BroadcastReceiver {

    private String phoneNumber;
    private String message;
    private SharedPreferences sharedPreferences;
    private boolean exceptionThrown;
    private SharedPreferences.Editor editor;
    private Long specifiedStartTime;


    @Override
    public void onReceive(Context context, Intent intent) {

        smsSending(context,intent);

    }

    public void smsSending(Context context, Intent intent){


        sharedPreferences = context.getSharedPreferences(MainActivity.PREFS_NAME,
                Context.MODE_PRIVATE);

      //  phoneNumber = sharedPreferences.getString("phone", "");
      //  message = sharedPreferences.getString("msg","");


        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);


        message = sharedPrefs.getString("message", "NULL");

        phoneNumber = sharedPrefs.getString("phone", "NULL");

        specifiedStartTime = sharedPrefs.getLong("timePrefA_Key",0L);

        try{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber,null,message,null,null);

        }catch(IllegalArgumentException ex){
           Toast.makeText(context, "Invalid input",Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
            exceptionThrown = true;
            editor = sharedPreferences.edit();
            editor.putBoolean("exceptionThrown",exceptionThrown);
            editor.commit();
        }

    }

}
