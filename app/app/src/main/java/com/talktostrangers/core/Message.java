package com.talktostrangers.core;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by simon on 12.04.14.
 */
public class Message implements Parcelable {
    public long timestamp;
    public String text;
    public String authorID;
    public String authorName;

    public Message() {}

    private Message(Parcel in) {
        timestamp = in.readLong();
        text = in.readString();
        authorID = in.readString();
        authorName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int flag) {
        out.writeLong(timestamp);
        out.writeString(text);
        out.writeString(authorID);
        out.writeString(authorName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };


}
