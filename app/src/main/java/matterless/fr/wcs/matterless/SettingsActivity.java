package matterless.fr.wcs.matterless;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.*;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewUserName;
    private UsersLogin usersLogin;
    private UserProfile userProfile;
    private UserCredentials muserCredentials;
    private TextView textViewEmail;
    private TextView textViewPassword;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonlog;
    private String authToken;
    private FileOutputStream mfileOutputStream;
    private FileInputStream mfileInputStream;
    public final String API_BASE_URL = "https://chat.wildcodeschool.fr/api/v3/";
    public final String FILE_NAME = "FILE_NAME";
    private ImageView imageViewProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        imageViewProfilePicture = (ImageView) findViewById(R.id.imageViewProfilePicture);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        textViewPassword = (TextView) findViewById(R.id.textViewPassword);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonlog = (Button) findViewById(R.id.buttonLog);
        buttonlog.setOnClickListener(this);
        textViewUserName = (TextView) findViewById(R.id.textViewUserName);


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

        if(muserCredentials != null){

            textViewEmail.setVisibility(View.GONE);
            textViewPassword.setVisibility(View.GONE);
            editTextEmail.setVisibility(View.GONE);
            editTextPassword.setVisibility(View.GONE);
            textViewUserName.setVisibility(View.VISIBLE);
            imageViewProfilePicture.setVisibility(View.VISIBLE);
            buttonlog.setText(R.string.buttonLogOutText);

            textViewUserName.setText(muserCredentials.getUserName());

            MattermostService getUserImageUrl = ServiceGenerator.RETROFIT.create(MattermostService.class);
            Call<ResponseBody> callImageUrl = getUserImageUrl.getProfilePicture( "Bearer " + muserCredentials.getToken(), muserCredentials.getImageUrl());
            callImageUrl.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseImageUrl) {
                    if (responseImageUrl.isSuccessful()) {

                        textViewUserName.setText(muserCredentials.getUserName());

                        boolean writtenToDisk = writeResponseBodyToDisk(responseImageUrl.body());
                        if(writtenToDisk) {
                            File imageFile = new File(String.valueOf(getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/profilePicture.png")));
                            Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                            imageViewProfilePicture.setImageBitmap(myBitmap);
                            Toast.makeText(SettingsActivity.this, "image enregistré avec un le token du intenral storage", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(SettingsActivity.this, "ta ton image", Toast.LENGTH_SHORT).show();



                    } else {
                        SettingsActivity.this.deleteFile(FILE_NAME);
                        usersLogin = new UsersLogin(muserCredentials.getEmail(), muserCredentials.getPassword());
                        MattermostService mattermostService =
                                ServiceGenerator.RETROFIT.create(MattermostService.class);
                        Call<Void> callLogin = mattermostService.basicLogin(usersLogin);
                        callLogin.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, final Response<Void> response) {

                                if (response.isSuccessful()) {
                                    // usersLogin object available
                                    authToken = response.headers().get("Token");
                                    Toast.makeText(SettingsActivity.this, "ok!", Toast.LENGTH_SHORT).show();
                                    MattermostService getUserProfile =
                                            ServiceGenerator.RETROFIT.create(MattermostService.class);
                                    Call<UserProfile> callUserProfile = getUserProfile.getUser("Bearer " + authToken);
                                    callUserProfile.enqueue(new Callback<UserProfile>() {
                                        @Override
                                        public void onResponse(Call<UserProfile> call, Response<UserProfile> responseUser) {

                                            if (responseUser.isSuccessful()) {
                                                // usersLogin object available
                                                textViewEmail.setVisibility(View.GONE);
                                                textViewPassword.setVisibility(View.GONE);
                                                editTextEmail.setVisibility(View.GONE);
                                                editTextPassword.setVisibility(View.GONE);
                                                textViewUserName.setVisibility(View.VISIBLE);
                                                imageViewProfilePicture.setVisibility(View.VISIBLE);
                                                buttonlog.setText(R.string.buttonLogOutText);

                                                userProfile = responseUser.body();
                                                String imageUrl = API_BASE_URL + "users/" +userProfile.getId() + "/image?time=" + userProfile.getLastPictureUpdate();

                                                UserCredentials userCredentials = new UserCredentials(userProfile.getId(), userProfile.getEmail(),
                                                        editTextPassword.getText().toString(),
                                                        authToken,
                                                        imageUrl,
                                                        userProfile.getUsername());

                                                try {
                                                    mfileOutputStream = openFileOutput(FILE_NAME, SettingsActivity.this.MODE_PRIVATE);
                                                } catch (FileNotFoundException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    mfileOutputStream.write(userCredentials.oneString().getBytes());
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    mfileOutputStream.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }


                                                MattermostService getUserImageUrl = ServiceGenerator.RETROFIT.create(MattermostService.class);
                                                Call<ResponseBody> callImageUrl = getUserImageUrl.getProfilePicture( "Bearer " + authToken, imageUrl);
                                                callImageUrl.enqueue(new Callback<ResponseBody>() {
                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseImageUrl) {
                                                        if (responseImageUrl.isSuccessful()) {

                                                            textViewUserName.setText(userProfile.getUsername());

                                                            boolean writtenToDisk = writeResponseBodyToDisk(responseImageUrl.body());
                                                            if(writtenToDisk) {
                                                                File imageFile = new File(String.valueOf(getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/profilePicture.png")));
                                                                Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                                                                imageViewProfilePicture.setImageBitmap(myBitmap);
                                                                Toast.makeText(SettingsActivity.this, "image enregistré", Toast.LENGTH_SHORT).show();
                                                            }
                                                            Toast.makeText(SettingsActivity.this, "ta ton image", Toast.LENGTH_SHORT).show();



                                                        } else {
                                                            Toast.makeText(SettingsActivity.this, "Foireux", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                                                    }
                                                });

                                                Toast.makeText(SettingsActivity.this, "successfull", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // error response, no access to resource?
                                                Toast.makeText(SettingsActivity.this, "error", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<UserProfile> call, Throwable t) {
                                            Toast.makeText(SettingsActivity.this, "failure", Toast.LENGTH_SHORT).show();

                                        }
                                    });




                                } else {
                                    // error response, no access to resource?
                                    Toast.makeText(SettingsActivity.this, "pk c'est là", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                // something went completely south (like no internet connection)
                                Toast.makeText(SettingsActivity.this, "doesn't work", Toast.LENGTH_SHORT).show();
                                Log.d("Error", t.getMessage());
                            }
                        });

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonLog:
                if(editTextEmail != null && editTextPassword != null && muserCredentials == null){
                    usersLogin = new UsersLogin(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                    MattermostService mattermostService =
                            ServiceGenerator.RETROFIT.create(MattermostService.class);
                    Call<Void> call = mattermostService.basicLogin(usersLogin);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, final Response<Void> response) {

                            if (response.isSuccessful()) {
                                // usersLogin object available
                                authToken = response.headers().get("Token");
                                Toast.makeText(SettingsActivity.this, "ok!", Toast.LENGTH_SHORT).show();
                                MattermostService getUserProfile =
                                        ServiceGenerator.RETROFIT.create(MattermostService.class);
                                Call<UserProfile> callUserProfile = getUserProfile.getUser("Bearer " + authToken);
                                callUserProfile.enqueue(new Callback<UserProfile>() {
                                    @Override
                                    public void onResponse(Call<UserProfile> call, Response<UserProfile> responseUser) {

                                        if (responseUser.isSuccessful()) {
                                            // usersLogin object available
                                            textViewEmail.setVisibility(View.GONE);
                                            textViewPassword.setVisibility(View.GONE);
                                            editTextEmail.setVisibility(View.GONE);
                                            editTextPassword.setVisibility(View.GONE);
                                            textViewUserName.setVisibility(View.VISIBLE);
                                            imageViewProfilePicture.setVisibility(View.VISIBLE);
                                            buttonlog.setText(R.string.buttonLogOutText);

                                            userProfile = responseUser.body();
                                            String imageUrl = API_BASE_URL + "users/" +userProfile.getId() + "/image?time=" + userProfile.getLastPictureUpdate();

                                             muserCredentials = new UserCredentials(userProfile.getId(), userProfile.getEmail(),
                                                    editTextPassword.getText().toString(),
                                                    authToken,
                                                    imageUrl,
                                                    userProfile.getUsername());

                                            try {
                                                mfileOutputStream = openFileOutput(FILE_NAME, SettingsActivity.this.MODE_PRIVATE);
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                mfileOutputStream.write(muserCredentials.oneString().getBytes());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                mfileOutputStream.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            Intent toService = new Intent(SettingsActivity.this, MyService.class);
                                            toService.setAction(MyService.INTENT_START_BOT);
                                            startService(toService);


                                            MattermostService getUserImageUrl = ServiceGenerator.RETROFIT.create(MattermostService.class);
                                            Call<ResponseBody> callImageUrl = getUserImageUrl.getProfilePicture( "Bearer " + authToken, imageUrl);
                                            callImageUrl.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseImageUrl) {
                                                    if (responseImageUrl.isSuccessful()) {

                                                        textViewUserName.setText(userProfile.getUsername());

                                                        boolean writtenToDisk = writeResponseBodyToDisk(responseImageUrl.body());
                                                        if(writtenToDisk) {
                                                            File imageFile = new File(String.valueOf(getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/profilePicture.png")));
                                                            Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                                                            imageViewProfilePicture.setImageBitmap(myBitmap);
                                                            Toast.makeText(SettingsActivity.this, "image enregistré", Toast.LENGTH_SHORT).show();
                                                        }
                                                        Toast.makeText(SettingsActivity.this, "ta ton image", Toast.LENGTH_SHORT).show();



                                                    } else {
                                                        Toast.makeText(SettingsActivity.this, "Foireux", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                                }
                                            });

                                            Toast.makeText(SettingsActivity.this, "successfull", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // error response, no access to resource?
                                            Toast.makeText(SettingsActivity.this, "error", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<UserProfile> call, Throwable t) {
                                        Toast.makeText(SettingsActivity.this, "failure", Toast.LENGTH_SHORT).show();

                                    }
                                });




                            } else {
                                // error response, no access to resource?
                                Toast.makeText(SettingsActivity.this, "pk c'est là", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            // something went completely south (like no internet connection)
                            Toast.makeText(SettingsActivity.this, "doesn't work", Toast.LENGTH_SHORT).show();
                            Log.d("Error", t.getMessage());
                        }
                    });
                }

                else if (authToken != null || muserCredentials != null){

                    textViewEmail.setVisibility(View.VISIBLE);
                    textViewPassword.setVisibility(View.VISIBLE);
                    editTextEmail.setVisibility(View.VISIBLE);
                    editTextEmail.setText(null);
                    editTextPassword.setVisibility(View.VISIBLE);
                    editTextPassword.setText(null);
                    textViewUserName.setVisibility(View.INVISIBLE);
                    imageViewProfilePicture.setVisibility(View.INVISIBLE);
                    buttonlog.setText(R.string.buttonLogInText);
                    muserCredentials = null;
                    SettingsActivity.this.deleteFile(FILE_NAME);
                }
        }

    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(SettingsActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "profilePicture.png");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
