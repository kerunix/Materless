package matterless.fr.wcs.matterless;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MessageSettingActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_setting);

        database = FirebaseDatabase.getInstance();
        mRef = database.getReference();

        buttonTimePicker = (Button) findViewById(R.id.buttonTimePicker);
        buttonSelectDay = (Button) findViewById(R.id.buttonSelectDay);
        buttonCreateEvent =(Button) findViewById(R.id.buttonCreateEvent);

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
                            Toast.makeText(MessageSettingActivity.this, "activé", Toast.LENGTH_SHORT).show();
                        }

                        else if (!isChecked) {
                            finalDays.remove(mDayList[which]);
                            Toast.makeText(MessageSettingActivity.this, "enlevé", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


                dayDialog.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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
                        timeHour = selectedHour;
                        timeMinute =selectedMinute;
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        buttonCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Message message = new Message(mMessageName.getText().toString(), finalDays, timeMinute, timeHour, mMessageContent.getText().toString());
                mRef.push().setValue(message);
            }
        });
    }
}
