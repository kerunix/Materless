package matterless.fr.wcs.matterless;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.buttonConfigureEvents:
                Intent intentToMessageList = new Intent(MainActivity.this, MessageListActivity.class);
                startActivity(intentToMessageList);
                break;
            case R.id.buttonMyProfile:
                Intent intentToSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intentToSettings);
                break;
        }

    }
}
