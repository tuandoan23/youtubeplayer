package com.gamota.youtubeplayer.network;

import com.google.gson.JsonElement;

import java.util.Map;

import io.reactivex.Observable;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface APIEndpoints {

    @GET("/youtube/v3/search?part=snippet%2Cid&order=date&maxResults=6")
    Observable<JsonElement> getListVideo(@QueryMap Map<String, Object> queryMap);

    @GET("/youtube/v3/channels?part=snippet%2Cstatistics")
    Observable<JsonElement> getChannelInfo(@QueryMap Map<String, Object> queryMap);

    @GET("/youtube/v3/videos?part=snippet%2Cstatistics")
    Observable<JsonElement> getVideo(@QueryMap Map<String, Object> queryMap);

    @GET("/youtube/v3/commentThreads?part=snippet&maxResults=4&order=time")
    Observable<JsonElement> getListComment(@QueryMap Map<String, Object> queryMap);
}
