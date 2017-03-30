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

    private Message(){};

    public Message (String name, ArrayList<String> days, int timeMinute, int timeHour, String messageContent) {

        mName = name;
        mDays = days;
        mTimeMinute = timeMinute;
        mTimeHour = timeHour;
        mMessageContent = messageContent;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public ArrayList<String> getmDays() {
        return mDays;
    }

    public void setmDays(ArrayList<String> mDays) {
        this.mDays = mDays;
    }

    public int getmTimeMinute() {
        return mTimeMinute;
    }

    public void setmTimeMinute(int mTimeMinute) {
        this.mTimeMinute = mTimeMinute;
    }

    public int getmTimeHour() {
        return mTimeHour;
    }

    public void setmTimeHour(int mTimeHour) {
        this.mTimeHour = mTimeHour;
    }

    public String getmMessageContent() {
        return mMessageContent;
    }

    public void setmMessageContent(String mMessageContent) {
        this.mMessageContent = mMessageContent;
    }
}
