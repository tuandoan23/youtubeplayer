package com.gamota.youtubeplayer.network;

import com.gamota.youtubeplayer.base.BaseActivity;
import com.google.gson.JsonElement;

import java.util.HashMap;

import io.reactivex.Observable;

public class APIRequests {
//    public static Observable<JsonElement> getListVideo(String channelId, String apiKey){
//        HashMap<String, Object> queryMap = new HashMap<>();
//        queryMap.put("channelId", channelId);
//        queryMap.put("key", apiKey);
//        return BaseAPIRequest.getClient().getListVideo(queryMap);
//    }

    public static Observable<JsonElement> getChannelInfo(String channelId, String apiKey){
        HashMap<String, Object> queryMap = new HashMap<>();
        queryMap.put("id", channelId);
        queryMap.put("key", apiKey);
        return BaseAPIRequest.getClient().getChannelInfo(queryMap);
    }

    public static Observable<JsonElement> getVideo(String videoId, String apiKey){
        HashMap<String, Object> queryMap = new HashMap<>();
        queryMap.put("id", videoId);
        queryMap.put("key", apiKey);
        return BaseAPIRequest.getClient().getVideo(queryMap);
    }

    public static Observable<JsonElement> getListVideo(String channelId, String apiKey, String pageToken){
        HashMap<String, Object> queryMap = new HashMap<>();
        queryMap.put("channelId", channelId);
        queryMap.put("key", apiKey);
        if (pageToken != "") {
            queryMap.put("pageToken", pageToken);
        }
        return BaseAPIRequest.getClient().getListVideo(queryMap);
    }
}
