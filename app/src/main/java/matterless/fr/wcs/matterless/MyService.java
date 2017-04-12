package matterless.fr.wcs.matterless;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
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

public class MyService extends Service {

    public static final String INTENT_START_BOT = "INTENT_START_BOT";
    public static final String INTENT_STOP_BOT = "INTENT_STOP_BOT";
    public final String FILE_NAME = "FILE_NAME";
    public static final String MESSAGE_NUMBER = "message_number";
    public static final String TAG = "MyService";




    private FileInputStream mfileInputStream;
    private UserCredentials muserCredentials;

    private ArrayList<Message> arrayListMessage;

    private AlarmManager alarmManager;


    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

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


        if (intent != null){
            if(intent.getAction().equals(INTENT_START_BOT)){
                Log.e("lala","lala");

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages/" + muserCredentials.getUserID());
                databaseReference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Message message = dataSnapshot.getValue(Message.class);

                        Calendar calendar = Calendar.getInstance();

                        for (int i = 0; i < message.getmDays().size(); i++){

                            switch (message.getmDays().get(i)) {

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

                            calendar.set(Calendar.HOUR_OF_DAY, message.getmTimeHour());
                            calendar.set(Calendar.MINUTE, message.getmTimeMinute());

                            Intent intentToAlarm_Receiver = new Intent(MyService.this, Alarm_Receiver.class);
                            intentToAlarm_Receiver.putExtra(MESSAGE_NUMBER, dataSnapshot.getKey());
                            String requestCode = String.valueOf(i) + String.valueOf(j);
                            PendingIntent myPendingIntent = PendingIntent.getBroadcast(MyService.this,
                                    Integer.parseInt(requestCode),
                                    intentToAlarm_Receiver, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmManager.INTERVAL_DAY * 7, myPendingIntent);
                            Log.d(TAG, "yattaaaaaaa");

                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
            }
            else if(intent.getAction().equals(intent.ACTION_BOOT_COMPLETED)){

            }
            else if(intent.getAction().equals(INTENT_STOP_BOT)){

            }

        }

        return START_STICKY;
    }

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

            Intent intentToAlarm_Receiver = new Intent(MyService.this, Alarm_Receiver.class);
            intentToAlarm_Receiver.putExtra(MESSAGE_NUMBER, i);
            String requestCode = String.valueOf(i) + String.valueOf(j);
            PendingIntent myPendingIntent = PendingIntent.getBroadcast(MyService.this,
                    Integer.parseInt(requestCode),
                    intentToAlarm_Receiver, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmManager.INTERVAL_DAY * 7, myPendingIntent);
            Log.d(TAG, "yattaaaaaaa");

        }
    }
}
