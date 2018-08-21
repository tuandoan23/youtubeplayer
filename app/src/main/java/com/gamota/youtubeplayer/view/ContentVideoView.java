package com.gamota.youtubeplayer.view;

import com.gamota.youtubeplayer.model.Video.Video;

public interface ContentVideoView {
    void getVideoSuccess(Video video);
    void getVideoError();
}
