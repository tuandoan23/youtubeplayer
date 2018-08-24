package com.gamota.youtubeplayer.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

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
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkOnClickListener;
import com.luseen.autolinklibrary.AutoLinkTextView;

import butterknife.BindView;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static com.gamota.youtubeplayer.utils.Utils.API_KEY;
import static com.gamota.youtubeplayer.utils.Utils.RFC3339ToDate;
import static com.gamota.youtubeplayer.utils.Utils.dateToString;

public class ContentVideoActivity extends BaseActivity implements ContentVideoView{
    private ContentVideoPresenter contentVideoPresenter;
    private String videoId;
    private String videoTitle;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvPublished)
    AppCompatTextView tvPublished;

    @BindView(R.id.tvDescription)
    AutoLinkTextView tvDescription;

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
        tvDescription.addAutoLinkMode(AutoLinkMode.MODE_URL);
        tvDescription.setUrlModeColor(Color.rgb(0, 0, 255));
        tvDescription.setSelectedStateColor(Color.GRAY);
        playerFragment.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                if (!b){
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    if (getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
                        youTubePlayer.setFullscreen(true);
                    }
                    youTubePlayer.loadVideo(videoId);
                }
            }
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                ContentVideoActivity.this.overridePendingTransition(0,R.anim.comming_in);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void loadData() {
        contentVideoPresenter.getVideo(videoId, API_KEY);
    }

    @Override
    public void getVideoSuccess(Video video) {
        if (getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
            tvPublished.setText(dateToString(RFC3339ToDate(video.getSnippet().getPublishedAt())));
            tvDescription.setText(video.getSnippet().getDescription());
            tvDescription.setAutoLinkText(video.getSnippet().getDescription());
            tvDescription.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
                @Override
                public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                    if (autoLinkMode == AutoLinkMode.MODE_URL) {
                        try {
                            Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(matchedText.trim()));
                            startActivity(viewIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void getVideoError() {

    }
}
