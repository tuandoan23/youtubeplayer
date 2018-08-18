package com.gamota.youtubeplayer.view;

import com.gamota.youtubeplayer.model.Video.Video;

public interface VideoPlayerView {
    void getVideoSuccess(Video video);
    void getVideoError();
}
