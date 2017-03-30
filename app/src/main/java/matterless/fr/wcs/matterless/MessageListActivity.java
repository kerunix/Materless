package matterless.fr.wcs.matterless;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Button;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

public class MessageListActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView mListViewMessage;

    private CustomListAdapter mAdapter;

    private FirebaseDatabase mDatabase;
    Query mRef;


    private Button buttonAddEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();

        mListViewMessage = (ListView) findViewById(R.id.listViewMessage);

        mAdapter = new CustomListAdapter(mRef, MessageListActivity.this, R.layout.message_list_item);


        buttonAddEvent = (Button) findViewById(R.id.buttonAddEvent);
        buttonAddEvent.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.buttonAddEvent:
                Intent intentToMessageSetting = new Intent(MessageListActivity.this, MessageSettingActivity.class);
                startActivity(intentToMessageSetting);
                finish();
                break;
        }
    }
}
