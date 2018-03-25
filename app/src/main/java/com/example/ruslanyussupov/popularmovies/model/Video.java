package com.example.ruslanyussupov.popularmovies.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Video implements Parcelable {

    private String key;
    private String name;
    private String site;
    private int size;

    public Video(String key, String name, String site, int size) {
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
    }

    protected Video(Parcel in) {
        key = in.readString();
        name = in.readString();
        site = in.readString();
        size = in.readInt();
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    public String getPreviewImagePath() {
        return "https://img.youtube.com/vi/" + key + "/1.jpg";
    }

    public String getUrl() {
        return "https://www.youtube.com/watch?v=" + key;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(site);
        dest.writeInt(size);
    }
}
