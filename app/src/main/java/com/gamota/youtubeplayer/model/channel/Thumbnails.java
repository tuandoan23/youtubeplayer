package com.gamota.youtubeplayer.model.channel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Thumbnails {

    @SerializedName("default")
    @Expose
    private DefaultImage _default;
    @SerializedName("medium")
    @Expose
    private MediumImage medium;
    @SerializedName("high")
    @Expose
    private HighImage high;

    public DefaultImage getDefault() {
        return _default;
    }

    public void setDefault(DefaultImage _default) {
        this._default = _default;
    }

    public MediumImage getMedium() {
        return medium;
    }

    public void setMedium(MediumImage medium) {
        this.medium = medium;
    }

    public HighImage getHigh() {
        return high;
    }

    public void setHigh(HighImage high) {
        this.high = high;
    }

}