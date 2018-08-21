package com.gamota.youtubeplayer.activity;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.apkfuns.logutils.LogUtils;
import com.gamota.youtubeplayer.R;
import com.gamota.youtubeplayer.base.BaseActivity;
import com.gamota.youtubeplayer.model.Video.Video;
import com.gamota.youtubeplayer.presenter.ContentVideoPresenter;
import com.gamota.youtubeplayer.presenteriplm.ContentVideoPresenterIplm;
import com.gamota.youtubeplayer.view.ContentVideoView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import butterknife.BindView;

import static com.gamota.youtubeplayer.utils.Utils.API_KEY;
import static com.gamota.youtubeplayer.utils.Utils.RFC3339ToDate;
import static com.gamota.youtubeplayer.utils.Utils.dateToString;

public class ContentVideoActivity extends BaseActivity implements ContentVideoView{
    private ContentVideoPresenter contentVideoPresenter;
    private String videoId;
    private String videoTitle;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.channelTitle)
    AppCompatTextView channelTitle;

    @BindView(R.id.tvPublished)
    AppCompatTextView tvPublished;

    @BindView(R.id.tvDescription)
    AppCompatTextView tvDescription;

    @Override
    public int initLayout() {
        return R.layout.activity_video;
    }

    @Override
    public void getExtraData() {
        videoId = getIntent().getStringExtra("videoId");
        videoTitle = getIntent().getStringExtra("videoTitle");
    }

    @Override
    public void createPresenter() {
        contentVideoPresenter = new ContentVideoPresenterIplm(this, compositeDisposable);
    }

    @Override
    public void createAdapter() {
        YouTubePlayerSupportFragment playerFragment =
                (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.playerFragment);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(videoTitle);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        playerFragment.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                if (!b){
                    youTubePlayer.loadVideo(videoId);
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                }
            }
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }

    @Override
    public void loadData() {
        contentVideoPresenter.getVideo(videoId, API_KEY);
    }

    @Override
    public void getVideoSuccess(Video video) {
        channelTitle.setText(video.getSnippet().getChannelTitle());
        tvPublished.setText(dateToString(RFC3339ToDate(video.getSnippet().getPublishedAt())));
        tvDescription.setText(video.getSnippet().getDescription());
        LogUtils.d(video.getSnippet().getDescription());
    }

    @Override
    public void getVideoError() {

    }
}
