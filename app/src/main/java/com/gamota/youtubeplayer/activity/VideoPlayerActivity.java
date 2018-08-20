package com.gamota.youtubeplayer.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.gamota.youtubeplayer.utils.Utils.API_KEY;

public class VideoPlayerActivity extends YouTubeBaseActivity implements VideoPlayerView{
    private Unbinder unbinder;

    @BindView(R.id.tvVideoTitle)
    AppCompatTextView tvVideoTitle;

    @BindView(R.id.tvPublished)
    AppCompatTextView tvPublished;

//    @BindView(R.id.tvDescription)
//    AppCompatTextView tvDescription;

    @BindView(R.id.player)
    YouTubePlayerView youTubePlayerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_video);
        unbinder = ButterKnife.bind(this);
        String videoId = getIntent().getStringExtra("videoId");
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
        tvVideoTitle.setText(videoTitle);
        tvPublished.setText(Utils.dateToString(Utils.RFC3339ToDate(published)));
//        tvDescription.setText(description);

    }

    @Override
    public void getVideoSuccess(Video video) {
    }

    @Override
    public void getVideoError() {

    }
}
