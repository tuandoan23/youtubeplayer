package com.gamota.youtubeplayer.view;

import com.gamota.youtubeplayer.model.comment.Item;
import com.gamota.youtubeplayer.model.video.Video;

import java.util.ArrayList;

public interface ContentVideoView {
    void getVideoSuccess(Video video);
    void getVideoError();

    void getListCommentSuccess(ArrayList<Item> items, String nextPageToken);
    void getListCommentError();
}
