package com.gamota.youtubeplayer.presenteriplm;

import com.apkfuns.logutils.LogUtils;
import com.gamota.youtubeplayer.model.listvideomodel.Item;
import com.gamota.youtubeplayer.network.APIRequests;
import com.gamota.youtubeplayer.presenter.SearchViewPresenter;
import com.gamota.youtubeplayer.view.SearchView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SearchViewPresenterIplm implements SearchViewPresenter {
    private SearchView searchView;
    private CompositeDisposable compositeDisposable;

    public SearchViewPresenterIplm(SearchView searchView, CompositeDisposable compositeDisposable) {
        this.searchView = searchView;
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public void searchVideo(String channelId, String apiKey, String pageToken, String q) {
        Disposable searchVideoWithPageTokenRequest = APIRequests.searchVideo(channelId, apiKey, pageToken, q)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    LogUtils.d("searchVideo success = " + jsonElement);
                    Gson gson = new Gson();
                    ArrayList<Item> items = gson.fromJson(jsonElement.getAsJsonObject().get("items"), new TypeToken<ArrayList<Item>>(){}.getType());
                    String nextPageToken = gson.fromJson(jsonElement.getAsJsonObject().get("nextPageToken"), new TypeToken<String>(){}.getType());
                    searchView.searchVideoSuccess(items, nextPageToken);
                }, throwable -> {
                    LogUtils.e("getListVideo error = " + throwable);
                    throwable.printStackTrace();
                    searchView.searchVideoError();
                });
        compositeDisposable.add(searchVideoWithPageTokenRequest);
    }
}
