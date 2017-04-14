package matterless.fr.wcs.matterless;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.POST;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public final String FILE_NAME = "FILE_NAME";
    public static final String TAG = "MainActivity";
    public static final String MESSAGE_NUMBER = "message_number";

    private Button buttonConfigureEvents;
    private Button buttonMyProfile;
    private ImageView imageViewBigButtonMainActivity;
    private FileInputStream mfileInputStream;
    private UserCredentials muserCredentials;

    private ArrayList<Message> arrayListMessage;

    private AlarmManager alarmManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonConfigureEvents = (Button) findViewById(R.id.buttonConfigureEvents);
        buttonConfigureEvents.setOnClickListener(this);
        buttonMyProfile = (Button)findViewById(R.id.buttonMyProfile);
        buttonMyProfile.setOnClickListener(this);
        imageViewBigButtonMainActivity = (ImageView) findViewById(R.id.imageViewBigButtonMainActivity);
        imageViewBigButtonMainActivity.setOnClickListener(this);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        PulsatorLayout pulsator = (PulsatorLayout) findViewById(R.id.pulsator);
        pulsator.start();

    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            mfileInputStream = openFileInput(FILE_NAME);
            int c;
            String temp="";
            while( (c = mfileInputStream.read()) != -1){
                temp = temp + Character.toString((char)c);
            }
            String[] arr = temp.split("\\|");
            muserCredentials = new UserCredentials(arr);
            mfileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(muserCredentials == null){
            Intent intentToSettingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intentToSettingsActivity);
        }

        else {

        }



        /*//alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        final Calendar calendar = Calendar.getInstance();
        //calendar.set(2017, 4, 5, timeHour, timeMinute);


        //Button buttonStart = (Button) findViewById(R.id.buttonCreateEvent);

        final Intent intent = new Intent(this, Alarm_Receiver.class);

        buttonCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DAY_OF_WEEK, 1);
                calendar.set(Calendar.HOUR_OF_DAY, timeHour);
                calendar.set(Calendar.MINUTE, timeMinute);

                String hourString = String.valueOf(timeHour);
                String minuteString = String.valueOf(timeMinute);
                Toast.makeText(MessageSettingActivity.this, hourString+"/"+minuteString, Toast.LENGTH_SHORT).show();
                myPendingIntent = PendingIntent.getBroadcast(MessageSettingActivity.this, 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), myPendingIntent);
                Log.d("yaaataaaaa", "yattaaaaaaa");
            }
        });

        /*buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_alarm_text("Alarme off");
                alarmManager.cancel(myPendingIntent);
            }
        });*/
    }

    @Override
    protected void onStop() {
        super.onStop();

        muserCredentials = null;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.buttonConfigureEvents:
                if(muserCredentials != null){
                    Intent intentToSettings = new Intent(MainActivity.this, MessageListActivity.class);
                    startActivity(intentToSettings);
                }
                else {
                    Toast.makeText(this, R.string.authentication_needed, Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.buttonMyProfile:
                Intent intentToSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intentToSettings);
                break;


            case R.id.imageViewBigButtonMainActivity:

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages/" + muserCredentials.getUserID());
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        arrayListMessage = new ArrayList<>();

                        for (DataSnapshot child : dataSnapshot.getChildren()) {

                            arrayListMessage.add(child.getValue(Message.class));
                        }



                        for (int i = 0; i < arrayListMessage.size(); i++) {
                            Calendar calendar = Calendar.getInstance();
                            for (int j = 0; j < arrayListMessage.get(i).getmDays().size(); j++) {

                                switch (arrayListMessage.get(i).getmDays().get(j)) {

                                    case "Lundi":
                                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                                        break;

                                    case "Mardi":
                                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                                        break;

                                    case "Mercredi":
                                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                                        break;

                                    case "Jeudi":
                                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                                        break;

                                    case "Vendredi":
                                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                                        break;

                                    case "Samedi":
                                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                                        break;

                                    case "Dimanche":
                                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                                        break;


                                }

                                calendar.set(Calendar.HOUR_OF_DAY, arrayListMessage.get(i).getmTimeHour());
                                calendar.set(Calendar.MINUTE, arrayListMessage.get(i).getmTimeMinute());

                                Intent intentToAlarm_Receiver = new Intent(MainActivity.this, Alarm_Receiver.class);
                                intentToAlarm_Receiver.putExtra(MESSAGE_NUMBER, 666);
                                String requestCode = String.valueOf(i) + String.valueOf(j);
                                PendingIntent myPendingIntent = PendingIntent.getBroadcast(MainActivity.this,
                                        Integer.parseInt(requestCode),
                                        intentToAlarm_Receiver, PendingIntent.FLAG_UPDATE_CURRENT);
                                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmManager.INTERVAL_DAY * 7, myPendingIntent);
                                Log.d(TAG, "yattaaaaaaa");

                            }
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
        }

    }
}
