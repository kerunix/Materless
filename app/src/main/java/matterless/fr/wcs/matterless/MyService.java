package matterless.fr.wcs.matterless;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MyService extends Service {

    public static final String INTENT_START_BOT = "INTENT_START_BOT";
    public static final String INTENT_STOP_BOT = "INTENT_STOP_BOT";
    public final String FILE_NAME = "FILE_NAME";
    public static final String MESSAGE_NAME = "message_name";
    public static final String MESSAGE_CONTENT = "message_content";
    public static final String CHANNEL_ID = "channel_id";
    public static final String TAG = "MyService";




    private FileInputStream mfileInputStream;
    private UserCredentials muserCredentials;
    private DatabaseReference databaseReference;


    private AlarmManager alarmManager;


    public MyService() {
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        UserCredentials.fromFile(this, FILE_NAME);

        databaseReference = FirebaseDatabase.getInstance().getReference("Messages/" + muserCredentials.getUserID());

        if (intent != null){
            if(intent.getAction().equals(INTENT_START_BOT)){
                Log.e(TAG,"bot Started");
                databaseReference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        cancelAlarm(dataSnapshot);
                        sendAlarm(dataSnapshot);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        cancelAlarm(dataSnapshot);
                        sendAlarm(dataSnapshot);
                        Log.e(TAG, "data changed");

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        cancelAlarm(dataSnapshot);

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            else if(intent.getAction().equals(intent.ACTION_BOOT_COMPLETED)){
                Log.e(TAG,"bot Started");
                databaseReference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        cancelAlarm(dataSnapshot);
                        sendAlarm(dataSnapshot);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        cancelAlarm(dataSnapshot);
                        sendAlarm(dataSnapshot);

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        cancelAlarm(dataSnapshot);

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
            else if(intent.getAction().equals(INTENT_STOP_BOT)){
                Log.e(TAG,"bot Stopped");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        cancelAll(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendAlarm(DataSnapshot dataSnapshot){
        Message message = dataSnapshot.getValue(Message.class);

        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < message.getmDays().size(); i++) {

            if (message.getmDays().get(i).isEnabled()) {

                calendar.set(Calendar.DAY_OF_WEEK, i + 1);
                calendar.set(Calendar.HOUR_OF_DAY, message.getmTimeHour());
                calendar.set(Calendar.MINUTE, message.getmTimeMinute());
                long timeToALaram = calendar.getTimeInMillis();

                if(calendar.getTimeInMillis() < System.currentTimeMillis() - alarmManager.INTERVAL_HALF_HOUR /2){
                    timeToALaram += (alarmManager.INTERVAL_DAY * 7);
                }

                Intent intentToAlarm_Receiver = new Intent(MyService.this, Alarm_Receiver.class);
                intentToAlarm_Receiver.putExtra(MESSAGE_NAME, message.getmName());
                intentToAlarm_Receiver.putExtra(MESSAGE_CONTENT, message.getmMessageContent());
                intentToAlarm_Receiver.putExtra(CHANNEL_ID, message.getmChannelId());

                PendingIntent myPendingIntent = PendingIntent.getBroadcast(MyService.this,
                        message.getmDays().get(i).getId(),
                        intentToAlarm_Receiver,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeToALaram, alarmManager.INTERVAL_DAY * 7, myPendingIntent);
                Log.d(TAG, "yattaaaaaaa");


            }
        }
    }

    private void cancelAlarm(DataSnapshot dataSnapshot){
        Message message = dataSnapshot.getValue(Message.class);

        //String key = dataSnapshot.getKey();
        for (int i = 0; i < message.getmDays().size(); i++) {
            Intent intentToAlarm_Receiver = new Intent(MyService.this, Alarm_Receiver.class);
            //intentToAlarm_Receiver.putExtra(MESSAGE_KEY, key);
            PendingIntent myPendingIntent = PendingIntent.getBroadcast(MyService.this,
                    message.getmDays().get(i).getId(),
                    intentToAlarm_Receiver,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(myPendingIntent);


        }
    }

    private void cancelAll(DataSnapshot dataSnapshot){
        for (DataSnapshot child: dataSnapshot.getChildren()){
            cancelAlarm(child);
        }
    }
}
