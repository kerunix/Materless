package matterless.fr.wcs.matterless;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MessageSettingActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_setting);
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
