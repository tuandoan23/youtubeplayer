package com.gamota.youtubeplayer.model.listvideomodel;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Thumbnails implements Parcelable{

    @SerializedName("default")
    @Expose
    private ImageDefault _default;
    @SerializedName("medium")
    @Expose
    private ImageMedium medium;
    @SerializedName("high")
    @Expose
    private ImageHigh high;

    public ImageDefault getDefault() {
        return _default;
    }

    public void setDefault(ImageDefault _default) {
        this._default = _default;
    }

    public ImageMedium getMedium() {
        return medium;
    }

    public void setMedium(ImageMedium medium) {
        this.medium = medium;
    }

    public ImageHigh getHigh() {
        return high;
    }

    public void setHigh(ImageHigh high) {
        this.high = high;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this._default, flags);
        dest.writeParcelable(this.medium, flags);
        dest.writeParcelable(this.high, flags);
    }

    public Thumbnails() {
    }

    protected Thumbnails(Parcel in) {
        this._default = in.readParcelable(ImageDefault.class.getClassLoader());
        this.medium = in.readParcelable(ImageMedium.class.getClassLoader());
        this.high = in.readParcelable(ImageHigh.class.getClassLoader());
    }

    public static final Creator<Thumbnails> CREATOR = new Creator<Thumbnails>() {
        @Override
        public Thumbnails createFromParcel(Parcel source) {
            return new Thumbnails(source);
        }

        @Override
        public Thumbnails[] newArray(int size) {
            return new Thumbnails[size];
        }
    };
}
