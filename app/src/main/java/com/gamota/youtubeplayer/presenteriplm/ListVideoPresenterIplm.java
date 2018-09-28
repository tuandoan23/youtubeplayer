package com.gamota.youtubeplayer.presenteriplm;

import com.apkfuns.logutils.LogUtils;
import com.gamota.youtubeplayer.model.listvideomodel.Item;
import com.gamota.youtubeplayer.network.APIRequests;
import com.gamota.youtubeplayer.presenter.ListVideoPresenter;
import com.gamota.youtubeplayer.view.ListVideoView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ListVideoPresenterIplm implements ListVideoPresenter {
    private ListVideoView listVideoView;
    private CompositeDisposable compositeDisposable;

    public ListVideoPresenterIplm(ListVideoView listVideoView, CompositeDisposable compositeDisposable) {
        this.listVideoView = listVideoView;
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public void getListVideo(String channelId, String apiKey, String pageToken) {
        Disposable getListVideoWithPageTokenRequest = APIRequests.getListVideo(channelId, apiKey, pageToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    LogUtils.d("getListVideo success = " + jsonElement);
                    Gson gson = new Gson();
                    ArrayList<Item> items = gson.fromJson(jsonElement.getAsJsonObject().get("items"), new TypeToken<ArrayList<Item>>(){}.getType());
                    String nextPageToken = gson.fromJson(jsonElement.getAsJsonObject().get("nextPageToken"), new TypeToken<String>(){}.getType());
                    String totalResults = gson.fromJson(jsonElement.getAsJsonObject().get("pageInfo").getAsJsonObject().get("totalResults"), new TypeToken<String>(){}.getType());
                    listVideoView.getListVideoSuccess(items, nextPageToken, totalResults);
                }, throwable -> {
                    LogUtils.e("getListVideo error = " + throwable);
                    throwable.printStackTrace();
                    listVideoView.getListVideoError();
                });
        compositeDisposable.add(getListVideoWithPageTokenRequest);
    }
}
