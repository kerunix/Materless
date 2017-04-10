package matterless.fr.wcs.matterless;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Message implements Parcelable {

    private String mName;
    private String mMessageContent;
    private ArrayList<String> mDays;
    private int mTimeMinute;
    private int mTimeHour;

    private  Message(){}

    public Message (String name, ArrayList<String> days, int timeMinute, int timeHour, String messageContent) {

        mName = name;
        mDays = days;
        mTimeMinute = timeMinute;
        mTimeHour = timeHour;
        mMessageContent = messageContent;
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(mName);     //name
        List<String> arr = mDays.subList(0, mDays.size());
        dest.writeStringList(arr);   //days
        dest.writeInt(mTimeMinute); //minute
        dest.writeInt(mTimeHour); //hour
        dest.writeString(mMessageContent);    //content
    }

    private Message(Parcel in){

        mName = in.readString();
        ArrayList<String> arr = new ArrayList<String>();
        in.readStringList(arr);
        mDays = arr;
        mTimeMinute = in.readInt();
        mTimeHour = in.readInt();
        mMessageContent = in.readString();
    }
}
