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
    public static final String BOT_SIGNATURE = "[MaterlessBot]";

    private Intent intent;
    private String ref;

    private Button buttonTimePicker;
    private Button buttonSelectDay;
    private Button buttonCreateEvent;
    private Button buttonChoseChannel;

    private EditText mMessageName;
    private EditText mMessageContent;

    private ArrayList<String> finalDays;
    private String[] mDayList;
    private int timeMinute;
    private int timeHour;

    private ChannelRequest mChannelRequest;
    private String mChoosenChannelName;
    private String mChoosenChannelId;

    private int hour;
    private int minute;

    private FirebaseDatabase database;
    private DatabaseReference mRef;

    private FileInputStream mfileInputStream;
    private UserCredentials muserCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_setting);

        database = FirebaseDatabase.getInstance();

        intent = getIntent();
        final Message mMessage = intent.getParcelableExtra("message");
        ref = intent.getStringExtra("ref");


        buttonTimePicker = (Button) findViewById(R.id.buttonTimePicker);
        buttonSelectDay = (Button) findViewById(R.id.buttonSelectDay);
        buttonCreateEvent = (Button) findViewById(R.id.buttonCreateEvent);
        buttonChoseChannel = (Button) findViewById(R.id.buttonChoseChannel);

        mMessageName = (EditText) findViewById(R.id.editTextMessageName);
        mMessageContent = (EditText) findViewById(R.id.editTextMessageContent);

        mDayList = getResources().getStringArray(R.array.daysOfWeekArray);
        finalDays = new ArrayList<>();

        if (intent.hasExtra("message")) {

            buttonTimePicker.setText(mMessage.getmTimeHour() + ":" + mMessage.getmTimeMinute());
            buttonSelectDay.setText(arrayConverter(mMessage.getmDays()));
            buttonChoseChannel.setText(mMessage.getmChannelName());
            mMessageName.setText(mMessage.getmName());
            mMessageContent.setText(mMessage.getmMessageContent());
            buttonCreateEvent.setText("Valider");
            timeHour = mMessage.getmTimeHour();
            timeMinute = mMessage.getmTimeMinute();

        }


        buttonChoseChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder channelDialog = new AlertDialog.Builder(MessageSettingActivity.this);
                final String[] channelList = mChannelRequest.getChannelNames(mChannelRequest.removePrivateChan());
                int checkboxId = 1;
                if (intent.hasExtra("message")) {

                    for (int i = 0; i < channelList.length; i++) {

                        if (mMessage.getmChannelName().equals(channelList[i])) {
                            checkboxId = i;
                        }
                    }
                }


                channelDialog.setTitle("Choisis ton canal de discussion");
                channelDialog.setSingleChoiceItems(channelList, checkboxId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mChoosenChannelName = mChannelRequest.removePrivateChan().get(which).getDisplayName();
                        mChoosenChannelId = mChannelRequest.removePrivateChan().get(which).getId();

                        buttonChoseChannel.setText(mChoosenChannelName);
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        final boolean[] _selections = {false, false, false, false, false, false, false};

        if (intent.hasExtra("message")) {

            for (int i = 0; i < mMessage.getmDays().size(); i++) {

                switch (mMessage.getmDays().get(i)) {

                    case "Lundi":
                        _selections[0] = true;
                        finalDays.add(mDayList[0]);
                        break;

                    case "Mardi":
                        _selections[1] = true;
                        finalDays.add(mDayList[1]);
                        break;

                    case "Mercredi":
                        _selections[2] = true;
                        finalDays.add(mDayList[2]);
                        break;

                    case "Jeudi":
                        _selections[3] = true;
                        finalDays.add(mDayList[3]);
                        break;

                    case "Vendredi":
                        _selections[4] = true;
                        finalDays.add(mDayList[4]);
                        break;

                    case "Samedi":
                        _selections[5] = true;
                        finalDays.add(mDayList[5]);
                        break;

                    case "Dimanche":
                        _selections[6] = true;
                        finalDays.add(mDayList[6]);
                        break;
                }
            }
        }

        buttonSelectDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder dayDialog = new AlertDialog.Builder(MessageSettingActivity.this);
                final String[] day = mDayList;

                dayDialog.setTitle("Choisis tes jours");

                dayDialog.setMultiChoiceItems(day, _selections, new DialogInterface.OnMultiChoiceClickListener() {


                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        if (isChecked) {
                            finalDays.add(mDayList[which]);
                        } else if (!isChecked) {
                            finalDays.remove(mDayList[which]);
                        }

                    }
                });


                dayDialog.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        buttonSelectDay.setText(arrayConverter(finalDays));
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
                    hour = mMessage.getmTimeHour();
                    minute = mMessage.getmTimeMinute();
                } else {
                    hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    minute = mcurrentTime.get(Calendar.MINUTE);
                }
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MessageSettingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        timeHour = selectedHour;
                        timeMinute = selectedMinute;

                        if (timeMinute < 10) {
                            buttonTimePicker.setText(timeHour + ":0" + timeMinute);
                        } else {
                            buttonTimePicker.setText(timeHour + ":" + timeMinute);
                        }
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();
            }
        });

        buttonCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (finalDays.size() == 0 || timeHour == 0 || timeMinute == 0 || mMessageName.getText() == null || mMessageContent.getText() == null) {

                    Toast.makeText(MessageSettingActivity.this, R.string.toastComplete, Toast.LENGTH_SHORT).show();
                }

                else if(!intent.hasExtra("message")){
                    final Message editMessage = new Message(mMessageName.getText().toString(), finalDays, timeMinute, timeHour, mMessageContent.getText().toString(), mChoosenChannelId, mChoosenChannelName);
                    DatabaseReference mRef = database.getReference("Messages/" + muserCredentials.getUserID());
                    mRef.push().setValue(editMessage);
                    finish();
                }
                else {

                    final Message editMessage = new Message(mMessageName.getText().toString(), finalDays, timeMinute, timeHour, mMessageContent.getText().toString(), mChoosenChannelId, mChoosenChannelName);
                    DatabaseReference mRef = database.getReference("Messages/" + muserCredentials.getUserID());
                    mRef.child(ref).setValue(editMessage);
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

    public String arrayConverter(ArrayList<String> arrayList) {

        String daysDisplay = "";
        if(arrayList.size() > 1) {

            for (int i = 0; i < 2; i++) {

                if (i == arrayList.size() - 1) {

                    daysDisplay = daysDisplay + arrayList.get(i) + ".";
                } else {

                    daysDisplay = daysDisplay + arrayList.get(i) + ", ";
                }
            }
            daysDisplay = daysDisplay + "...";
        }
        else {
            daysDisplay = arrayList.get(0);
        }
        return daysDisplay;
    }

}