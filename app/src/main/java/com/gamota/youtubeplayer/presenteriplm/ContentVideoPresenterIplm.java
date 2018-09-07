package com.gamota.youtubeplayer.presenteriplm;

import com.apkfuns.logutils.LogUtils;
import com.gamota.youtubeplayer.model.comment.Item;
import com.gamota.youtubeplayer.model.video.Video;
import com.gamota.youtubeplayer.network.APIRequests;
import com.gamota.youtubeplayer.presenter.ContentVideoPresenter;
import com.gamota.youtubeplayer.view.ContentVideoView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ContentVideoPresenterIplm implements ContentVideoPresenter {
    private ContentVideoView contentVideoView;
    private CompositeDisposable compositeDisposable;

    public ContentVideoPresenterIplm(ContentVideoView contentVideoView, CompositeDisposable compositeDisposable) {
        this.contentVideoView = contentVideoView;
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public void getListComment(String videoId, String apiKey, String pageToken) {
        Disposable getListCommentWithPageTokenRequest = APIRequests.getListComment(videoId, apiKey, pageToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    LogUtils.d("getListComment success = " + jsonElement);
                    Gson gson = new Gson();
                    ArrayList<Item> items = gson.fromJson(jsonElement.getAsJsonObject().get("items"), new TypeToken<ArrayList<Item>>(){}.getType());
                    String nextPageToken = gson.fromJson(jsonElement.getAsJsonObject().get("nextPageToken"), new TypeToken<String>(){}.getType());
                    contentVideoView.getListCommentSuccess(items, nextPageToken);
                }, throwable -> {
                    LogUtils.e("getListComment error = " + throwable);
                    throwable.printStackTrace();
                    contentVideoView.getListCommentError();
                });
        compositeDisposable.add(getListCommentWithPageTokenRequest);
    }

    @Override
    public void getVideo(String videoId, String apiKey) {
        Disposable getVideoRequest = APIRequests.getVideo(videoId, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    LogUtils.d("getVideo success = " + jsonElement);
                    Gson gson = new Gson();
                    Video video = gson.fromJson(jsonElement.getAsJsonObject().get("items").getAsJsonArray().get(0), new TypeToken<Video>(){}.getType());
                    contentVideoView.getVideoSuccess(video);
                }, throwable -> {
                    LogUtils.e("getVideo error = " + throwable);
                    throwable.printStackTrace();
                    contentVideoView.getVideoError();
                });
        compositeDisposable.add(getVideoRequest);
    }
}
