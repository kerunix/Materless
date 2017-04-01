package matterless.fr.wcs.matterless;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.POST;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public final String FILE_NAME = "FILE_NAME";
    public static final String TAG = "MainActivity";

    private Button buttonConfigureEvents;
    private Button buttonMyProfile;
    private ImageView imageViewBigButtonMainActivity;
    private FileInputStream mfileInputStream;
    private UserCredentials muserCredentials;


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
                break;
        }

    }
}
