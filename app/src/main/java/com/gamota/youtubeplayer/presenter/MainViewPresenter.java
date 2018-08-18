package com.gamota.youtubeplayer.presenter;

public interface MainViewPresenter {
    void getListVideo(String channelId, String apiKey, String pageToken);
    void getChannelInfo(String channelId, String apiKey);
}
