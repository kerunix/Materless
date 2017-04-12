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
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wilder on 04/04/17.
 */

public class Alarm_Receiver extends BroadcastReceiver {

    public static final String TAG = "Alarm_Receiver";
    public final String FILE_NAME = "FILE_NAME";

    private FileInputStream mfileInputStream;
    private UserCredentials muserCredentials;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(final Context context, Intent intent) {

        String messageContent = intent.getStringExtra(MyService.MESSAGE_CONTENT);
        String messageName = intent.getStringExtra(MyService.MESSAGE_NAME);
        String channelId= intent.getStringExtra(MyService.CHANNEL_ID);

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

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification noti = new Notification.Builder(context)
                .setContentTitle(messageName)
                .setContentText(messageContent)
                .setSmallIcon(R.mipmap.icon)
                .build();

        notificationManager.notify(new Random().nextInt(), noti);

        Post post = new Post();
        post.setMessage(messageContent);
        post.setChannelId(channelId);
        String token = muserCredentials.getToken();
        MattermostService sendPost = ServiceGenerator.RETROFIT.create(MattermostService.class);
        Call<Post> callPost = sendPost.sendPost( ("Bearer " + muserCredentials.getToken()), post );
        callPost.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(response.isSuccessful()){
                    Log.e(TAG, "Post sended" + response.toString());

                }
                else{
                    Log.e(TAG, String.valueOf("response was not sucessfullll" +response.toString() + response.headers()));
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

            }
        });



    }

}
