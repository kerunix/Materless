package matterless.fr.wcs.matterless;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by keru on 18/04/17.
 */

public class MessageLocation implements Parcelable {

    private String mName;
    private String mChannelId;
    private String mChannelName;
    private String mMessageContent;
    private Location mLocation;

    private MessageLocation(){}


    public MessageLocation(String name, String channelId, String channelName, String messageContent, Location location) {

        mName = name;
        mChannelId = channelId;
        mChannelName = channelName;
        mMessageContent = messageContent;
        mLocation = location;

    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmChannelId() {
        return mChannelId;
    }

    public void setmChannelId(String mChannelId) {
        this.mChannelId = mChannelId;
    }

    public String getmChannelName() {
        return mChannelName;
    }

    public void setmChannelName(String mChannelName) {
        this.mChannelName = mChannelName;
    }

    public String getmMessageContent() {
        return mMessageContent;
    }

    public void setmMessageContent(String mMessageContent) {
        this.mMessageContent = mMessageContent;
    }

    public Location getmLocation() {
        return mLocation;
    }

    public void setmLocation(Location mLocation) {
        this.mLocation = mLocation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MessageLocation> CREATOR = new Creator<MessageLocation>() {
        @Override
        public MessageLocation createFromParcel(Parcel in) {
            return new MessageLocation(in);
        }

        @Override
        public MessageLocation[] newArray(int size) {
            return new MessageLocation[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mChannelId);
        dest.writeString(mChannelName);
        dest.writeString(mMessageContent);
        mLocation.writeToParcel(dest, flags);
    }

    protected MessageLocation(Parcel in) {
        mName = in.readString();
        mChannelId = in.readString();
        mChannelName = in.readString();
        mMessageContent = in.readString();
        Location.CREATOR.createFromParcel(in);
    }
}
