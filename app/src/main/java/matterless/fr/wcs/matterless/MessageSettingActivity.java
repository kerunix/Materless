package matterless.fr.wcs.matterless;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MessageSettingActivity extends AppCompatActivity /*implements View.OnClickListener*/ {


    public final String FILE_NAME = "FILE_NAME";
    public static final String BOT_SIGNATURE = "[MaterlessBot] ";

    private Intent intent;
    private String ref;

    private Button buttonTimePicker;
    private Button buttonSelectDay;
    private Button buttonCreateEvent;
    private Button buttonChoseChannel;

    private CheckBox checkBoxSignature;

    private EditText mEditTextMessageName;
    private EditText mEditTextMessageContent;

    private Message mFutureMessage;
    private ArrayList<String> finalDays;

    private ChannelRequest mChannelRequest;
    private FirebaseDatabase database;
    private DatabaseReference mRef;

    private int hour;
    private int minute;

    private FileInputStream mfileInputStream;
    private UserCredentials muserCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_setting);

        database = FirebaseDatabase.getInstance();

        intent = getIntent();
        ref = intent.getStringExtra("ref");

        checkBoxSignature = (CheckBox) findViewById(R.id.checkBoxSignature);

        buttonTimePicker = (Button) findViewById(R.id.buttonTimePicker);
        buttonSelectDay = (Button) findViewById(R.id.buttonSelectDay);
        buttonCreateEvent = (Button) findViewById(R.id.buttonCreateEvent);
        buttonChoseChannel = (Button) findViewById(R.id.buttonChoseChannel);

        mEditTextMessageName = (EditText) findViewById(R.id.editTextMessageName);
        mEditTextMessageContent = (EditText) findViewById(R.id.editTextMessageContent);

        finalDays = new ArrayList<>();

        if (intent.hasExtra("message")) {


            mFutureMessage = intent.getParcelableExtra("message");

            buttonTimePicker.setText(mFutureMessage.getmTimeHour() + ":" + mFutureMessage.getmTimeMinute());
            buttonSelectDay.setText(mFutureMessage.getDaysEnabled());
            mEditTextMessageName.setText(mFutureMessage.getmName());
            mEditTextMessageContent.setText(mFutureMessage.getmMessageContent());
            buttonCreateEvent.setText(R.string.ValidateButton);

        }

        else {
            mFutureMessage = new Message(getResources().getStringArray(R.array.daysOfWeekArray));
        }


        buttonChoseChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder channelDialog = new AlertDialog.Builder(MessageSettingActivity.this);
                final String[] channelList = mChannelRequest.getChannelNames(mChannelRequest.getPublicChannel());
                int checkboxId = 1;
                if (intent.hasExtra("message")) {

                    for (int i = 0; i < channelList.length; i++) {

                        if (mFutureMessage.getmChannelName().equals(channelList[i])) {
                            checkboxId = i;
                        }
                    }
                }


                channelDialog.setTitle("Choisis ton canal de discussion");
                channelDialog.setSingleChoiceItems(channelList, checkboxId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mFutureMessage.setmChannelName(mChannelRequest.getPublicChannel().get(which).getDisplayName());
                        mFutureMessage.setmChannelId(mChannelRequest.getPublicChannel().get(which).getId());
                        buttonChoseChannel.setText(mFutureMessage.getmChannelName());
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        final boolean[] _selections = {false, false, false, false, false, false, false};

        if (intent.hasExtra("message")) {

            for (int i = 0; i < mFutureMessage.getmDays().size(); i++) {

                switch (mFutureMessage.getmDays().get(i).isEnabled() ? 1 : 0) {

                    case 1:
                        _selections[i] = true;
                        mFutureMessage.getmDays().get(i).setEnabled(true);
                        break;
                }
            }
        }

        buttonSelectDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder dayDialog = new AlertDialog.Builder(MessageSettingActivity.this);
                final String[] day = getResources().getStringArray(R.array.daysOfWeekArray);

                dayDialog.setTitle("Choisis tes jours");

                dayDialog.setMultiChoiceItems(day, _selections, new DialogInterface.OnMultiChoiceClickListener() {


                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        if (isChecked) {
                            mFutureMessage.getmDays().get(which).setEnabled(true);
                        } else if (!isChecked) {
                            mFutureMessage.getmDays().get(which).setEnabled(false);
                        }

                    }
                });


                dayDialog.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        buttonSelectDay.setText(mFutureMessage.getDaysEnabled());
                        dialog.dismiss();
                    }
                });

                dayDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                }).show();
            }
        });

        buttonTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();

                if (intent.hasExtra("message")) {
                    hour = mFutureMessage.getmTimeHour();
                    minute = mFutureMessage.getmTimeMinute();
                } else {
                    hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    minute = mcurrentTime.get(Calendar.MINUTE);
                }
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MessageSettingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mFutureMessage.setmTimeHour(selectedHour);
                        mFutureMessage.setmTimeMinute(selectedMinute);

                        if (mFutureMessage.getmTimeMinute() < 10) {
                            buttonTimePicker.setText(mFutureMessage.getmTimeHour() + ":0" + mFutureMessage.getmTimeMinute());
                        } else {
                            buttonTimePicker.setText(mFutureMessage.getmTimeHour() + ":" + mFutureMessage.getmTimeMinute());
                        }
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();
            }
        });

        buttonCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String finalContent = mEditTextMessageContent.getText().toString();
                if (checkBoxSignature.isChecked()) {


                    finalContent = BOT_SIGNATURE + finalContent;
                }

                if (!intent.hasExtra("message") && mFutureMessage.getDaysEnabled().length() == 0 || mFutureMessage.getmTimeHour() == 0 || mFutureMessage.getmTimeMinute() == 0 || mEditTextMessageName.getText().toString().length() == 0 || mEditTextMessageContent.getText().toString().length() == 0) {

                }


                if (finalDays.size() == 0 || mEditTextMessageName.getText() == null || mEditTextMessageContent.getText() == null) {
                    Toast.makeText(MessageSettingActivity.this, R.string.toastComplete, Toast.LENGTH_SHORT).show();
                }

                else if(!intent.hasExtra("message")){
                    mFutureMessage.setmName(mEditTextMessageName.getText().toString().trim());
                    mFutureMessage.setmMessageContent(mEditTextMessageContent.getText().toString().trim());

                    mRef = database.getReference("Messages/" + muserCredentials.getUserID());
                    mRef.push().setValue(mFutureMessage);
                    finish();
                }
                else {
                    mFutureMessage.setmName(mEditTextMessageName.getText().toString().trim());
                    mFutureMessage.setmMessageContent(mEditTextMessageContent.getText().toString().trim());

                    mRef = database.getReference("Messages/" + muserCredentials.getUserID());
                    mRef.child(ref).setValue(mFutureMessage);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

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

        mRef = database.getReference("Messages/" + muserCredentials.getUserID());

        MattermostService mattermostService =
                ServiceGenerator.RETROFIT.create(MattermostService.class);
        Call<ChannelRequest> call = mattermostService.getChannels("Bearer " + muserCredentials.getToken());
        call.enqueue(new Callback<ChannelRequest>() {
            @Override
            public void onResponse(Call<ChannelRequest> call, Response<ChannelRequest> response) {
                mChannelRequest = response.body();
            }

            @Override
            public void onFailure(Call<ChannelRequest> call, Throwable t) {
                Log.e(MainActivity.TAG, t.toString());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        muserCredentials = null;
    }
}