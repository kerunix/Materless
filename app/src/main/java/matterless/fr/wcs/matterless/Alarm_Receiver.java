package matterless.fr.wcs.matterless;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Alarm_Receiver extends BroadcastReceiver {


    public static final String TAG = "Alarm_Receiver";
    public final String FILE_NAME = "FILE_NAME";

    private FileInputStream mfileInputStream;
    private UserCredentials muserCredentials;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(final Context context, Intent intent) {

        if(intent.getAction() == null) {

            String messageContent = intent.getStringExtra(MyService.MESSAGE_CONTENT);
            String messageName = intent.getStringExtra(MyService.MESSAGE_NAME);
            String channelId = intent.getStringExtra(MyService.CHANNEL_ID);

            Log.e("Ca marche !", "BORDEL !!!");

            muserCredentials = new UserCredentials();
            muserCredentials = UserCredentials.fromFile(context, FILE_NAME);


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

        else if (intent.getAction().equals(intent.ACTION_BOOT_COMPLETED)){
            Intent toService = new Intent(context, MyService.class);
            toService.setAction(MyService.INTENT_START_BOT);
            context.startService(toService);
        }



    }

}
