package matterless.fr.wcs.matterless;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.util.Log;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Alarm_Receiver extends BroadcastReceiver {

    public static final String TAG = "Alarm_Receiver";
    public final String FILE_NAME = "FILE_NAME";
    public static final String MESSAGE_NUMBER = "message_number";
    private int messageNumber;

    private FileInputStream mfileInputStream;
    private UserCredentials muserCredentials;

    private ArrayList<Message> arrayListMessage;

    @Override
    public void onReceive(final Context context, Intent intent) {

        messageNumber = intent.getIntExtra(MESSAGE_NUMBER, 1000000);
        arrayListMessage = new ArrayList<>();


        Log.e("Ca marche !", "BORDEL !!!");

        try {
            mfileInputStream = context.openFileInput(FILE_NAME);
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


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages/" + muserCredentials.getUserID());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    arrayListMessage.add(child.getValue(Message.class));
                }

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification noti = new Notification.Builder(context)
                        .setContentTitle(arrayListMessage.get(messageNumber).getmName())
                        .setContentText("Youhou")
                        .setSmallIcon(R.mipmap.icon)
                        .build();

                notificationManager.notify((int) Math.random(), noti);

                Post post = new Post();
                post.setMessage(arrayListMessage.get(messageNumber).getmMessageContent());
                post.setChannelId(arrayListMessage.get(messageNumber).getmChannelId());
                String token = muserCredentials.getToken();
                MattermostService sendPost = ServiceGenerator.RETROFIT.create(MattermostService.class);
                Call<Post> callPost = sendPost.sendPost( ("Bearer " + muserCredentials.getToken()), post );
                callPost.enqueue(new Callback<Post>() {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response) {
                        if(response.isSuccessful()){
                            Log.d(TAG, "Post sended" + response.toString());

                        }
                        else{
                            Log.d(TAG, String.valueOf("response was not sucessfullll" +response.toString() + response.headers()));
                        }
                    }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {

                        }
                    });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
