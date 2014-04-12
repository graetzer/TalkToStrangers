package com.talktostrangers.core;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by simon on 12.04.14.
 */
public class Profile implements Parcelable {
    public String identifier;

    public String name;
    public String profileImageURL;

    public Profile(){}

    private Profile(Parcel in) {
        identifier = in.readString();
        name = in.readString();
        profileImageURL = in.readString();
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(identifier);
        out.writeString(name);
        out.writeString(profileImageURL);
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Profile> CREATOR
            = new Parcelable.Creator<Profile>() {
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

}
