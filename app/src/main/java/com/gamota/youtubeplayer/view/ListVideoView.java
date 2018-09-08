package com.gamota.youtubeplayer.view;

import com.gamota.youtubeplayer.model.listvideomodel.Item;

import java.util.ArrayList;

public interface ListVideoView {
    void getListVideoSuccess(ArrayList<Item> items, String nextPageToken, String totalResults);
    void getListVideoError();
}
