package matterless.fr.wcs.matterless;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by keru on 28/03/17.
 */

public class Message {

    private String mName;
    private ArrayList<String> mDays;
    private int mTimeMinute;
    private int mTimeHour;
    private String mMessageContent;

    public Message (String name, ArrayList<String> days, int timeMinute, int timeHour, String messageContent) {

        mName = name;
        mDays = days;
        mTimeMinute = timeMinute;
        mTimeHour = timeHour;
        mMessageContent = messageContent;
    }

    public List<String> getDays(){

        return mDays;
    }

    public int getmTimeMinute() {

        return mTimeMinute;
    }

    public int getTimeHour(){

        return mTimeHour;
    }

    public String getMessageContent(){

        return mMessageContent;
    }

    public String getName() {

        return mName;
    }
}
