package com.gamota.youtubeplayer.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import com.gamota.youtubeplayer.R;
import com.gamota.youtubeplayer.adapter.CommentAdapter;
import com.gamota.youtubeplayer.base.BaseActivity;
import com.gamota.youtubeplayer.database.DBHelper;
import com.gamota.youtubeplayer.model.comment.Item;
import com.gamota.youtubeplayer.model.video.Video;
import com.gamota.youtubeplayer.presenter.ContentVideoPresenter;
import com.gamota.youtubeplayer.presenteriplm.ContentVideoPresenterIplm;
import com.gamota.youtubeplayer.view.ContentVideoView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkOnClickListener;
import com.luseen.autolinklibrary.AutoLinkTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static com.gamota.youtubeplayer.utils.Utils.API_KEY;
import static com.gamota.youtubeplayer.utils.Utils.RFC3339ToDateString;

public class ContentVideoActivity extends BaseActivity implements ContentVideoView{
    private ContentVideoPresenter contentVideoPresenter;
    private String videoId;
    private String videoTitle;
    private String nextPageTokenComments="";
    private CommentAdapter commentAdapter;
    private ArrayList<Item> comments = new ArrayList<>();
    private ArrayList<com.gamota.youtubeplayer.model.listvideomodel.Item> favouriteVideos = new ArrayList<>();
    private LinearLayoutManager commentLayoutManager;
    private com.gamota.youtubeplayer.model.listvideomodel.Item video;
    private int commentCount;
    public DBHelper db;
    private boolean isFavourite = false;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvPublished)
    AppCompatTextView tvPublished;

    @BindView(R.id.tvDescription)
    AutoLinkTextView tvDescription;

    @BindView(R.id.rvListComment)
    RecyclerView rvListCommnet;

    @BindView(R.id.btnLoadMore)
    AppCompatButton btnLoadMore;

    @BindView(R.id.btnFavourite)
    AppCompatImageButton btnFavourite;

    @OnClick(R.id.btnFavourite)
    void favourite(){
        if (isFavourite){
            isFavourite = false;
            btnFavourite.setImageResource(R.drawable.ic_favorite_border);
            db.deleteFavourite(video);
        } else {
            isFavourite = true;
            btnFavourite.setImageResource(R.drawable.ic_favorite);
            db.insertFavourite(video);
        }

        if (getIntent().getBooleanExtra("favourite", false)){

        }
    }

    @Override
    public int initLayout() {
        return R.layout.activity_video;
    }

    @Override
    public void getExtraData() {
        video = (com.gamota.youtubeplayer.model.listvideomodel.Item) getIntent().getExtras().getParcelable("video");
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

        //Comment
        commentLayoutManager = new LinearLayoutManager(this);
        commentAdapter = new CommentAdapter(comments, this);
        rvListCommnet.setLayoutManager(commentLayoutManager);
        rvListCommnet.setAdapter(commentAdapter);


        db =  new DBHelper(this);
        favouriteVideos = db.getAllFavourite();
        for (int i = 0; i < favouriteVideos.size(); i++){
            if (favouriteVideos.get(i).getId().getVideoId().equals(videoId)){
                isFavourite = true;
                btnFavourite.setImageResource(R.drawable.ic_favorite);
            }
        }
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
        if (!compositeDisposable.isDisposed()) {
            if (getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
                tvPublished.setText(RFC3339ToDateString((video.getSnippet().getPublishedAt())));
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
                commentCount = Integer.parseInt(video.getStatistics().getCommentCount());
                if (commentCount > 0) {
                    contentVideoPresenter.getListComment(videoId, API_KEY, nextPageTokenComments);
                    if (commentCount > 4){
                        btnLoadMore.setVisibility(View.VISIBLE);
                    } else {
                        btnLoadMore.setVisibility(View.GONE);
                    }
                } else {
                    btnLoadMore.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void getVideoError() {
        if (!compositeDisposable.isDisposed()) {

        }
    }

    @Override
    public void getListCommentSuccess(ArrayList<Item> items, String nextPageToken) {
        if (!compositeDisposable.isDisposed()) {
            comments.addAll(items);
            nextPageTokenComments = nextPageToken;
            commentAdapter.notifyDataSetChanged();
            if (nextPageToken == null) {
//                Toast.makeText(getApplicationContext(), "Loaded all comments", Toast.LENGTH_LONG).show();
                btnLoadMore.setVisibility(View.GONE);
            }
            btnLoadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contentVideoPresenter.getListComment(videoId,API_KEY, nextPageToken);
                }
            });
        }
    }

    @Override
    public void getListCommentError() {
        if (!compositeDisposable.isDisposed()) {

        }
    }
}
