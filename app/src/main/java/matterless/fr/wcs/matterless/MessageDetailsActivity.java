package matterless.fr.wcs.matterless;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.id.message;

public class MessageDetailsActivity extends AppCompatActivity implements View.OnClickListener{

    private Intent intent;

    private TextView textViewMessageDetailsTitle;
    private TextView textViewMessageDetailsHour;
    private TextView textViewMessageDetailsContent;
    private TextView textViewMessageDetailsDays;

    private int position;

    private Button buttonMessageDetailsEdit;
    private Button buttonMessageDetailsDelete;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);

        buttonMessageDetailsEdit =(Button) findViewById(R.id.buttonMessageDetailsEdit);
            buttonMessageDetailsEdit.setOnClickListener(this);
        buttonMessageDetailsDelete =(Button) findViewById(R.id.buttonMessageDetailsDelete);
            buttonMessageDetailsDelete.setOnClickListener(this);

        textViewMessageDetailsContent = (TextView) findViewById(R.id.textViewMessageDetailsContent);
        textViewMessageDetailsHour = (TextView) findViewById(R.id.textViewMessageDetailsHour);
        textViewMessageDetailsTitle = (TextView) findViewById(R.id.textViewMessageDetailsTitle);
        textViewMessageDetailsDays = (TextView) findViewById(R.id.textViewMessageDetailsDays);
        intent = getIntent();

        Message message = intent.getParcelableExtra("message");
        position = intent.getIntExtra("position", 0);

        textViewMessageDetailsTitle.setText(message.getmName());
        textViewMessageDetailsDays.setText(Arrays.toString(message.getmDays().toArray()));
        textViewMessageDetailsHour.setText(message.getmTimeHour() + ":" + message.getmTimeMinute());
        textViewMessageDetailsContent.setText(message.getmMessageContent());


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonMessageDetailsEdit:

                Intent intent = new Intent(MessageDetailsActivity.this, MessageSettingActivity.class);
                intent.putExtra("message", message);
                intent.putExtra("position", position);
                startActivity(intent);
                finish();
                break;

            case R.id.buttonMessageDetailsDelete:

                AlertDialog.Builder adb = new AlertDialog.Builder(MessageDetailsActivity.this);
                adb.setTitle("Supprimer?");
                adb.setMessage("Es-tu sûr(e) de vouloir supprimer ce message ?");
                adb.setNegativeButton("Annuler", null);
                adb.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent delete = new Intent(MessageDetailsActivity.this, MessageListActivity.class);
                        delete.putExtra("deletePosition", position);
                        startActivity(delete);
                        finish();

                    }
                });
                adb.show();
                break;
        }
    }
}
