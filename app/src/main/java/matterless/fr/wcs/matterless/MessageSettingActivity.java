package matterless.fr.wcs.matterless;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MessageSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonDeleteEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_setting);

        buttonDeleteEvent = (Button) findViewById(R.id.buttonDeleteEvent);
        buttonDeleteEvent.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.buttonDeleteEvent:
                Intent intentDeleteMessage = new Intent(MessageSettingActivity.this, MessageListActivity.class);
                startActivity(intentDeleteMessage);
                finish();
                break;
        }
    }
}
