package com.gamota.youtubeplayer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gamota.youtubeplayer.OnLoadMoreListener;
import com.gamota.youtubeplayer.R;
import com.gamota.youtubeplayer.adapter.VideoAdapter;
import com.gamota.youtubeplayer.base.BaseActivity;
import com.gamota.youtubeplayer.model.Channel.ChannelInfo;
import com.gamota.youtubeplayer.model.ListVideoModel.Item;
import com.gamota.youtubeplayer.model.ListVideoModel.ListVideo;
import com.gamota.youtubeplayer.presenter.MainViewPresenter;
import com.gamota.youtubeplayer.presenteriplm.MainViewPresenterIplm;
import com.gamota.youtubeplayer.view.MainView;

import java.util.ArrayList;

import butterknife.BindView;

import static com.gamota.youtubeplayer.utils.Utils.API_KEY;
import static com.gamota.youtubeplayer.utils.Utils.CHANNEL_ID;

public class MainActivity extends BaseActivity implements MainView{
    private MainViewPresenter mainViewPresenter;
    private String nextPageToken = "";
    private VideoAdapter videoAdapter;
    private ArrayList<Item> items = new ArrayList<>();
    private int total;
    private LinearLayoutManager linearLayoutManager;
    private boolean loading = true;
    int visibleItemCount, totalItemCount, firstVisibleItem;
    private int previousTotal = 0;
    private int visibleThreshold = 1;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rvListVideo)
    RecyclerView rvListVideo;

    @BindView(R.id.tvTotal)
    AppCompatTextView tvTotal;

    @BindView(R.id.tvChannelTitle)
    AppCompatTextView tvChannelTitle;

    @BindView(R.id.imgChannelIcon)
    SimpleDraweeView imgChannelIcon;

    @Override
    public void getChannelInfoSuccess(ChannelInfo channelInfo) {
        if (!compositeDisposable.isDisposed()) {
            tvChannelTitle.setText(channelInfo.getSnippet().getTitle().toString());
            Uri uri = Uri.parse(channelInfo.getSnippet().getThumbnails().getDefault().getUrl().toString());
            imgChannelIcon.setImageURI(uri);
            mainViewPresenter.getListVideo(CHANNEL_ID, API_KEY, nextPageToken);
        }
    }

    @Override
    public void getChannelInfoError() {
        if (!compositeDisposable.isDisposed()){

        }
    }

    @Override
    public int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void getExtraData() {

    }

    @Override
    public void createPresenter() {
        mainViewPresenter = new MainViewPresenterIplm(this, compositeDisposable);
    }

    @Override
    public void createAdapter() {
        setSupportActionBar(toolbar);
        linearLayoutManager = new LinearLayoutManager(this);
        rvListVideo.setLayoutManager(linearLayoutManager);
        videoAdapter = new VideoAdapter(items, this);
        rvListVideo.setAdapter(videoAdapter);
    }

    @Override
    public void loadData() {
        mainViewPresenter.getChannelInfo(CHANNEL_ID, API_KEY);
        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String videoId = intent.getStringExtra("videoId");
                String videoTitle = intent.getStringExtra("videoTitle");
                String published = intent.getStringExtra("published");
                String description = intent.getStringExtra("description");
                boolean isClicked = intent.getBooleanExtra("isClicked",false);
                if (isClicked){
                    Intent newIntent = new Intent(getApplicationContext(), VideoPlayerActivity.class );
                    newIntent.putExtra("videoId", videoId);
                    newIntent.putExtra("videoTitle", videoTitle);
                    newIntent.putExtra("published", published);
                    newIntent.putExtra("description", description);
                    startActivity(newIntent);
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));
    }

    @Override
    public void getListVideoSuccess(ArrayList<Item> items, String nextPageToken, String totalResults) {
        if (!compositeDisposable.isDisposed()) {
            tvTotal.setText(totalResults);
            total = Integer.parseInt(totalResults);
            this.nextPageToken = nextPageToken;
            this.items.addAll(items);
            videoAdapter.notifyDataSetChanged();
            setOnScrollListener(nextPageToken);
        }
    }

    private void setOnScrollListener(String nextPageToken) {
        rvListVideo.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = rvListVideo.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    LogUtils.d("end");
                    mainViewPresenter.getListVideo(CHANNEL_ID, API_KEY, nextPageToken);
                    loading = true;
                }
            }
        });
    }

    @Override
    public void getListVideoError() {
        if (!compositeDisposable.isDisposed()){

        }
    }
}
