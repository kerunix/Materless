package matterless.fr.wcs.matterless;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.Button;

import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MessageSettingActivity extends AppCompatActivity implements View.OnClickListener {

    public final String FILE_NAME = "FILE_NAME";

    private String[] mDayList;
    private Button buttonTimePicker;
    private Button buttonSelectDay;
    private Button buttonCreateEvent;
    private EditText mMessageName;
    private EditText mMessageContent;

    private ArrayList<String> finalDays = new ArrayList<>();
    private int timeMinute;
    private int timeHour;

    private FirebaseDatabase database;
    private DatabaseReference mRef;

    private FileInputStream mfileInputStream;
    private UserCredentials muserCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_setting);

        database = FirebaseDatabase.getInstance();

        buttonTimePicker = (Button) findViewById(R.id.buttonTimePicker);
        buttonSelectDay = (Button) findViewById(R.id.buttonSelectDay);
        buttonCreateEvent =(Button) findViewById(R.id.buttonCreateEvent);
          buttonCreateEvent.setOnClickListener(this);

        mMessageName = (EditText) findViewById(R.id.editTextMessageName);
        mMessageContent = (EditText) findViewById(R.id.editTextMessageContent);

        mDayList = getResources().getStringArray(R.array.daysOfWeekArray);
        buttonSelectDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder dayDialog = new AlertDialog.Builder(MessageSettingActivity.this);
                final String[] day = mDayList;
                final boolean[] _selections = {false, false, false, false, false, false, false};

                dayDialog.setTitle("Choisis tes jours");

                dayDialog.setMultiChoiceItems(day, _selections, new DialogInterface.OnMultiChoiceClickListener() {


                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        if (isChecked) {
                            finalDays.add(mDayList[which]);
                        }

                        else if (!isChecked) {
                            finalDays.remove(mDayList[which]);
                        }

                    }
                });


                dayDialog.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        buttonSelectDay.setText(finalDays.toString());
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
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MessageSettingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        buttonTimePicker.setText(timeHour + ":" + timeMinute);
                        timeHour = selectedHour;
                        timeMinute =selectedMinute;
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();
            }
        });
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

        mRef = database.getReference("Messages/"+muserCredentials.getUserID());
    }

    @Override
    protected void onStop() {
        super.onStop();

        muserCredentials = null;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.buttonCreateEvent:
                Message message = new Message(mMessageName.getText().toString(), finalDays, timeMinute, timeHour, mMessageContent.getText().toString());
                mRef.push().setValue(message);
                Intent intent = new Intent(MessageSettingActivity.this, MessageListActivity.class);
                startActivity(intent);
        }
    }
}
