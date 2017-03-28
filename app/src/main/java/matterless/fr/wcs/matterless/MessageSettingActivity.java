package matterless.fr.wcs.matterless;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.view.View;
import android.widget.TimePicker;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MessageSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private String[] mDayList;
    private Button buttonTimePicker;
    private Button buttonSelectDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_setting);

        buttonTimePicker = (Button) findViewById(R.id.buttonTimePicker);
        buttonSelectDay = (Button) findViewById(R.id.buttonSelectDay);

        mDayList = getResources().getStringArray(R.array.daysOfWeekArray);
        buttonSelectDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ArrayList<String> selectedDays = new ArrayList<>();
                final AlertDialog.Builder dayDialog = new AlertDialog.Builder(MessageSettingActivity.this);
                final String[] days = mDayList;
                final boolean[] _selections = {true,true,true,true,true,true,true};
                dayDialog.setTitle("Choisis tes jours");
                dayDialog.setMultiChoiceItems(days, _selections, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        if (isChecked) {
                            selectedDays.add(days[which]);
                        }
                    }
                });

                AlertDialog dialog = dayDialog.create();
                dialog.show();
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
                        buttonTimePicker.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.buttonDeleteEvent:
                Intent intentDeleteMessage = new Intent(MessageSettingActivity.this, MessageListActivity.class);
                startActivity(intentDeleteMessage);
                break;
        }
    }
}
