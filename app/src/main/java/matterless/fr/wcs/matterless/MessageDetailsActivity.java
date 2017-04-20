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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.id.message;

public class MessageDetailsActivity extends AppCompatActivity {

    public final String FILE_NAME = "FILE_NAME";

    private Intent intent;

    private TextView textViewMessageDetailsTitle;
    private TextView textViewMessageDetailsHour;
    private TextView textViewMessageDetailsContent;
    private TextView textViewMessageDetailsDays;
    private TextView textViewChannel;

    private String ref;

    private Button buttonMessageDetailsEdit;
    private Button buttonMessageDetailsDelete;

    private FileInputStream mfileInputStream;
    private UserCredentials muserCredentials;


    /* Activity assez simple, on récupère le Message en intent et on affiche ses attributs dans
    * les views associées.
    *
    * Le bouton editMessage permet d'accéder à la message settings activity en passant en intent
    * le message affiché pour en modifier les caractéristiques.
    *
    * Le bouton deleteMessage affiche un AlertDialog pour que l'user confirme la suppression.
    * En cas de validation, on supprime le message de firebase et de la listView.
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);

        buttonMessageDetailsEdit =(Button) findViewById(R.id.buttonMessageDetailsEdit);
        buttonMessageDetailsDelete =(Button) findViewById(R.id.buttonMessageDetailsDelete);

        textViewMessageDetailsContent = (TextView) findViewById(R.id.textViewMessageDetailsContent);
        textViewMessageDetailsHour = (TextView) findViewById(R.id.textViewMessageDetailsHour);
        textViewMessageDetailsTitle = (TextView) findViewById(R.id.textViewMessageDetailsTitle);
        textViewMessageDetailsDays = (TextView) findViewById(R.id.textViewMessageDetailsDays);
        textViewChannel = (TextView) findViewById(R.id.textViewChannel);

        intent = getIntent();

        final Message message = intent.getParcelableExtra("message");
        ref = intent.getStringExtra("ref");

        textViewMessageDetailsTitle.setText(message.getmName());
        textViewMessageDetailsDays.setText(message.getDaysEnabled());
        textViewMessageDetailsHour.setText(message.getmTimeHour() + ":" + message.getmTimeMinute());
        textViewMessageDetailsContent.setText(message.getmMessageContent());
        textViewChannel.setText(message.getmChannelName());

        buttonMessageDetailsEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageDetailsActivity.this, MessageSettingActivity.class);
                intent.putExtra("message", message);
                intent.putExtra("ref", ref);
                startActivity(intent);
                finish();
            }
        });

        buttonMessageDetailsDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(MessageDetailsActivity.this);
                adb.setTitle(R.string.adbDeleteTitle);
                adb.setMessage(R.string.adbDeleteMessage);
                adb.setNegativeButton(R.string.adbDeleteBegButton, null);
                adb.setPositiveButton(R.string.adbDeletePosButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference mRef = database.getReference("Messages/" + muserCredentials.getUserID());
                        mRef.child(ref).removeValue();
                        finish();

                    }
                });
                adb.show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        muserCredentials = new UserCredentials();
        muserCredentials = UserCredentials.fromFile(MessageDetailsActivity.this, FILE_NAME);
    }

    @Override
    protected void onStop() {
        super.onStop();

        muserCredentials = null;
    }
}
