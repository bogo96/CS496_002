package com.example.user.cs496_002;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 2018-01-01.
 */

public class Origin implements Parcelable{
    int from;
    String content;
    String imgid;

    public Origin(int from,String content, String imgid){
        this.from = from;
        this.content =content;
        this.imgid = imgid;
    }

    protected Origin(Parcel in) {
        from = in.readInt();
        content = in.readString();
    }

    public static final Creator<Origin> CREATOR = new Creator<Origin>() {
        @Override
        public Origin createFromParcel(Parcel in) {
            return new Origin(in);
        }

        @Override
        public Origin[] newArray(int size) {
            return new Origin[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(content);
        parcel.writeInt(from);
    }
}