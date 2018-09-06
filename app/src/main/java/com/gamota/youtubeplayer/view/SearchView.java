package com.gamota.youtubeplayer.view;

import com.gamota.youtubeplayer.model.listvideomodel.Item;

import java.util.ArrayList;

public interface SearchView {
    void searchVideoSuccess(ArrayList<Item> items, String nextPageToken);
    void searchVideoError();
}
