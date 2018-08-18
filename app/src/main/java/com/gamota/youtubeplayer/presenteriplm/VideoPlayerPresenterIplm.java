package com.gamota.youtubeplayer.presenteriplm;

import com.apkfuns.logutils.LogUtils;
import com.gamota.youtubeplayer.model.Channel.ChannelInfo;
import com.gamota.youtubeplayer.model.Video.Video;
import com.gamota.youtubeplayer.network.APIRequests;
import com.gamota.youtubeplayer.presenter.VideoPlayerPresenter;
import com.gamota.youtubeplayer.view.VideoPlayerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class VideoPlayerPresenterIplm implements VideoPlayerPresenter {
    private VideoPlayerView videoPlayerView;
    private CompositeDisposable compositeDisposable;

    public VideoPlayerPresenterIplm(VideoPlayerView videoPlayerView, CompositeDisposable compositeDisposable) {
        this.videoPlayerView = videoPlayerView;
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public void getVideo(String videoId, String apiKey) {
        Disposable getVideoRequest = APIRequests.getVideo(videoId, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    LogUtils.d("getVideo success = " + jsonElement);
                    Gson gson = new Gson();
                    Video video = gson.fromJson(jsonElement.getAsJsonObject().get("items"), new TypeToken<Video>(){}.getType());
                    videoPlayerView.getVideoSuccess(video);
                }, throwable -> {
                    LogUtils.e("getVideo error = " + throwable);
                    throwable.printStackTrace();
                    videoPlayerView.getVideoError();
                });
        compositeDisposable.add(getVideoRequest);
    }
}
