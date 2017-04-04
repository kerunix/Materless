package matterless.fr.wcs.matterless;

import android.app.Activity;
import android.text.Layout;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.Query;

/**
 * Created by keru on 30/03/17.
 */

public class CustomListAdapter extends FirebaseListAdapter<Message> {

    public CustomListAdapter (Query ref, Activity activity, int layout) {
        super (ref, Message.class, layout, activity );
    }
    @Override
    protected void populateView(View v, Message model) {

        TextView textViewListItemName = (TextView) v.findViewById(R.id.textViewMessageName);

        textViewListItemName.setText(model.getmName());
        String toto = model.getmName();
        String jena = "";
    }
}
