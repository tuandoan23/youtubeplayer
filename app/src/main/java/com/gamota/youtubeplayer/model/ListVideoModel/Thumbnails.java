package com.gamota.youtubeplayer.model.ListVideoModel;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Thumbnails {

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

}
