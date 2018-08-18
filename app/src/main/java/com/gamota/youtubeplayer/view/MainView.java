package com.gamota.youtubeplayer.view;

import com.gamota.youtubeplayer.model.Channel.ChannelInfo;
import com.gamota.youtubeplayer.model.ListVideoModel.Item;
import com.gamota.youtubeplayer.model.ListVideoModel.ListVideo;

import java.util.ArrayList;

public interface MainView {
    void getListVideoSuccess(ArrayList<Item> items, String nextPageToken, String totalResults);
    void getListVideoError();

    void getChannelInfoSuccess(ChannelInfo channelInfo);
    void getChannelInfoError();
}
