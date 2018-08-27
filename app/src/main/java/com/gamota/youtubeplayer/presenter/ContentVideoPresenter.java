package com.gamota.youtubeplayer.presenter;

public interface ContentVideoPresenter {
    void getVideo(String videoId, String apiKey);
    void getListComment(String videoId, String apiKey, String pageToken);
}
