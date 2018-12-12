package com.gamota.youtubeplayer.model.listvideomodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.apkfuns.logutils.LogUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageDefault implements Parcelable{

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("width")
    @Expose
    private Integer width;
    @SerializedName("height")
    @Expose
    private Integer height;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeValue(this.width);
        dest.writeValue(this.height);
    }

    public ImageDefault() {
    }

    protected ImageDefault(Parcel in) {
        this.url = in.readString();
        this.width = (Integer) in.readValue(Integer.class.getClassLoader());
        this.height = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<ImageDefault> CREATOR = new Creator<ImageDefault>() {
        @Override
        public ImageDefault createFromParcel(Parcel source) {
            return new ImageDefault(source);
        }

        @Override
        public ImageDefault[] newArray(int size) {
            return new ImageDefault[size];
        }
    };
}