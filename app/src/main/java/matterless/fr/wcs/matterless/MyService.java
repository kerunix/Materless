package matterless.fr.wcs.matterless;

import android.*;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

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
    private ArrayList<Message> mLocationMessage;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;


    public MyService() {

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mLocationMessage = new ArrayList<>();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        try {
            mfileInputStream = openFileInput(FILE_NAME);
            int c;
            String temp = "";
            while ((c = mfileInputStream.read()) != -1) {
                temp = temp + Character.toString((char) c);
            }
            String[] arr = temp.split("\\|");
            muserCredentials = new UserCredentials(arr);
            mfileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Messages/" + muserCredentials.getUserID());

        if (intent != null) {
            if (intent.getAction().equals(INTENT_START_BOT)) {
                mGoogleApiClient.connect();

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            if (child.getValue(Message.class).getmLatLng() != null) {
                                mLocationMessage.add(child.getValue(Message.class));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Log.e(TAG, "bot Started");
                databaseReference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.getValue(Message.class).getmLatLng() != null) {
                            //TODO
                        } else {
                            cancelAlarm(dataSnapshot);
                            sendAlarm(dataSnapshot);
                            Log.e(TAG, "data changed");

                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.getValue(Message.class).getmLatLng() != null) {
                            //TODO
                        } else {
                            cancelAlarm(dataSnapshot);
                            sendAlarm(dataSnapshot);
                            Log.e(TAG, "data changed");
                        }

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
            } else if (intent.getAction().equals(intent.ACTION_BOOT_COMPLETED)) {
                mGoogleApiClient.connect();
                Log.e(TAG, "bot Started");
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


            } else if (intent.getAction().equals(INTENT_STOP_BOT)) {
                mGoogleApiClient.disconnect();
                Log.e(TAG, "bot Stopped");
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

    private void sendAlarm(DataSnapshot dataSnapshot) {
        Message message = dataSnapshot.getValue(Message.class);

        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < message.getmDays().size(); i++) {

            if (message.getmDays().get(i).isEnabled()) {

                calendar.set(Calendar.DAY_OF_WEEK, i + 1);
                calendar.set(Calendar.HOUR_OF_DAY, message.getmTimeHour());
                calendar.set(Calendar.MINUTE, message.getmTimeMinute());
                long timeToALaram = calendar.getTimeInMillis();

                if (calendar.getTimeInMillis() < System.currentTimeMillis() - alarmManager.INTERVAL_HALF_HOUR / 2) {
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

    private void cancelAlarm(DataSnapshot dataSnapshot) {
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

    private void cancelAll(DataSnapshot dataSnapshot) {
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            cancelAlarm(child);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        createLocationRequest();
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000 * 20);
        mLocationRequest.setFastestInterval(1000 * 5);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.e("haaaa", "ici");

        for (int i = 0; i < mLocationMessage.size(); i++){
            if(mLocationMessage.get(i).getmLatLng().latitude == location.getLatitude()
                && mLocationMessage.get(i).getmLatLng().longitude == location.getLongitude()){

                Post post = new Post();
                post.setMessage(mLocationMessage.get(i).getmMessageContent());
                post.setChannelId(mLocationMessage.get(i).getmChannelId());

                MattermostService sendPost = ServiceGenerator.RETROFIT.create(MattermostService.class);
                Call<Post> callPost = sendPost.sendPost(("Bearer " + muserCredentials.getToken()), post);
                callPost.enqueue(new Callback<Post>() {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response) {
                        if (response.isSuccessful()) {
                            Log.e(TAG, "Post sended" + response.toString());

                        } else {
                            Log.e(TAG, String.valueOf("response was not sucessfullll" + response.toString() + response.headers()));
                        }
                    }

                    @Override
                    public void onFailure(Call<Post> call, Throwable t) {

                    }
                });
            }
        }
    }
}
