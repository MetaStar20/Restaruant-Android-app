package com.asura.library.posters;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class RemoteVideo extends VideoPoster implements Parcelable {
    private Uri uri;
    private int duration;

    public RemoteVideo(Uri uri,int duration) {
        this.uri = uri;
        this.duration = duration;
    }

    public Uri getUri() {
        return uri;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public RemoteVideo(Parcel in){
        uri = in.readParcelable(Uri.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(uri,flags);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RemoteVideo> CREATOR = new Creator<RemoteVideo>() {
        @Override
        public RemoteVideo createFromParcel(Parcel in) {
            return new RemoteVideo(in);
        }

        @Override
        public RemoteVideo[] newArray(int size) {
            return new RemoteVideo[size];
        }
    };
}
