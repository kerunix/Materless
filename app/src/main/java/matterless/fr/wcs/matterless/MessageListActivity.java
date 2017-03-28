package matterless.fr.wcs.matterless;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MessageListActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.buttonAddEvent:
                Intent intentToMessageSetting = new Intent(MessageListActivity.this, MessageSettingActivity.class);
                startActivity(intentToMessageSetting);
                break;
        }
    }
}
