package com.gamota.youtubeplayer.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;

import com.apkfuns.logutils.LogUtils;
import com.gamota.youtubeplayer.R;
import com.gamota.youtubeplayer.base.BaseActivity;
import com.gamota.youtubeplayer.model.Video.Video;
import com.gamota.youtubeplayer.presenter.VideoPlayerPresenter;
import com.gamota.youtubeplayer.presenteriplm.VideoPlayerPresenterIplm;
import com.gamota.youtubeplayer.utils.Utils;
import com.gamota.youtubeplayer.view.VideoPlayerView;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class VideoPlayerActivity extends YouTubeBaseActivity implements VideoPlayerView{
    private final String API_KEY = "AIzaSyBsrViFNAWWmWs0tVJ5z221PfWlsNZa8OQ";
//    private String videoId;
    private VideoPlayerPresenter videoPlayerPresenter;
    private YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_video);
        String videoId = getIntent().getStringExtra("videoId");
        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.player);
        youTubePlayerView.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(videoId);
                youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
        String videoTitle = getIntent().getStringExtra("videoTitle");
        String published = getIntent().getStringExtra("published");
        String description = getIntent().getStringExtra("description");
        AppCompatTextView tvVideoTitle = (AppCompatTextView) findViewById(R.id.tvVideoTitle);
        tvVideoTitle.setText(videoTitle);
        AppCompatTextView tvPublished = (AppCompatTextView) findViewById(R.id.tvPublished);
        tvPublished.setText(Utils.dateToString(Utils.RFC3339ToDate(published)));
        AppCompatTextView tvDescription = (AppCompatTextView) findViewById(R.id.tvDescription);
        tvDescription.setText(description);

    }

    @Override
    public void getVideoSuccess(Video video) {
    }

    @Override
    public void getVideoError() {

    }
}
