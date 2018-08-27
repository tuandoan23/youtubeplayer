package com.gamota.youtubeplayer.view;

import com.gamota.youtubeplayer.model.channel.ChannelInfo;
import com.gamota.youtubeplayer.model.listvideomodel.Item;

import java.util.ArrayList;

public interface MainView {
    void getListVideoSuccess(ArrayList<Item> items, String nextPageToken, String totalResults);
    void getListVideoError();

    void getChannelInfoSuccess(ChannelInfo channelInfo);
    void getChannelInfoError();
}
