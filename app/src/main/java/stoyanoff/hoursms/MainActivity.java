package stoyanoff.hoursms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {


    private TextView showPhoneText;
    private TextView showMessageText;
    private TextView statusText;
    private TextView specialText;
    private Button stopButton;
    private Button startButton;
    private AlarmManager alarmManager;
    private Intent smsAlertIntent;
    private Calendar calendar;
    private ComponentName receiver;
    private PackageManager packageManager;
    private String phoneNumber;
    private String message;
    public static final String PREFS_NAME = "ReceiverData";
    private boolean exceptionThrown;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private  Long specifiedStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        stopButton = (Button) findViewById(R.id.stopButton);
        startButton = (Button) findViewById(R.id.startButton);
        statusText = (TextView) findViewById(R.id.statusText);
        showPhoneText = (TextView) findViewById(R.id.showPhoneText);
        showMessageText = (TextView) findViewById(R.id.showMessageText);
        specialText = (TextView) findViewById(R.id.specialText);

          sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
//        phoneNumber = sharedPreferences.getString("phone", "");
//        message = sharedPreferences.getString("msg","");
//

        //TODO using contacts for numbers

        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);


        message = sharedPrefs.getString("message", "NULL");
        phoneNumber = sharedPrefs.getString("phone", "NULL");

        showPhoneText.setText(phoneNumber);
        showMessageText.setText(message);

        if(phoneNumber.contains("883465400")){
            specialText.setText(R.string.special_message);
        }
        checkActive();

        specifiedStartTime = sharedPrefs.getLong("timePrefA_Key",0L);

    }

    public void stopButtonClick(View view) {

        receiver = new ComponentName(this, TextEventReceiver.class);
        packageManager = this.getPackageManager();
        packageManager.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        Toast.makeText(this, R.string.sendingHalted,Toast.LENGTH_SHORT).show();

       setStatusInactive();
       checkActive();

    }

    public void startButtonClick(View view) {

     //TODO input data validation for the sms

        calendar = new GregorianCalendar();
        calendar.getTime();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if((calendar.get(Calendar.MINUTE)>0 ) ) {
            calendar.set(Calendar.HOUR_OF_DAY,hour+1);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND,0);
        }


        receiver = new ComponentName(this, TextEventReceiver.class);
        packageManager = this.getPackageManager();
        packageManager.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        smsAlertIntent = new Intent(this,TextEventReceiver.class);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
       // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),60*60*1000 ,
       //         PendingIntent.getBroadcast(this,1, smsAlertIntent, PendingIntent.FLAG_UPDATE_CURRENT));

         alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, specifiedStartTime,60*60*1000 ,
                PendingIntent.getBroadcast(this,1, smsAlertIntent, PendingIntent.FLAG_UPDATE_CURRENT));


        setStatusActive();
        checkActive();
    }

    private void checkActive(){

        exceptionThrown = sharedPreferences.getBoolean("exceptionThrown",false);

        if(exceptionThrown) {
            statusText.setText(R.string.inactiveStatus);
            statusText.setTextColor(Color.RED);
        }else{
            statusText.setTextColor(Color.GREEN);
            statusText.setText(R.string.activeStatus);
            Toast.makeText(this, R.string.serviceActive, Toast.LENGTH_SHORT).show();
        }

    }

    protected void setStatusInactive(){
        exceptionThrown = true;
        editor = sharedPreferences.edit();
        editor.putBoolean("exceptionThrown",exceptionThrown);
        editor.commit();

    }
    private void setStatusActive(){
        exceptionThrown = false;
        editor = sharedPreferences.edit();
        editor.putBoolean("exceptionThrown",exceptionThrown);
        editor.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra( SettingsActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName() );
            intent.putExtra( SettingsActivity.EXTRA_NO_HEADERS, true );
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
