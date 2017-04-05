package matterless.fr.wcs.matterless;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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

    @Override
    public void onReceive(Context context, Intent intent) {


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

        Post post = new Post();
        post.setMessage("Ce message est envoyé depuis l'émulateur android. Vive le forum de mattermost !!");
        post.setChannelId("bfsnn43zfpne3m877s66a454ih");
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

}