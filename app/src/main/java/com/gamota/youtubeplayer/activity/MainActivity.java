package com.gamota.youtubeplayer.activity;

import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gamota.youtubeplayer.R;
import com.gamota.youtubeplayer.adapter.VideoAdapter;
import com.gamota.youtubeplayer.base.BaseActivity;
import com.gamota.youtubeplayer.model.Channel.ChannelInfo;
import com.gamota.youtubeplayer.model.ListVideoModel.Item;
import com.gamota.youtubeplayer.presenter.MainViewPresenter;
import com.gamota.youtubeplayer.presenteriplm.MainViewPresenterIplm;
import com.gamota.youtubeplayer.view.MainView;

import java.util.ArrayList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import butterknife.BindView;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static com.gamota.youtubeplayer.utils.Utils.API_KEY;
import static com.gamota.youtubeplayer.utils.Utils.CHANNEL_ID;

public class MainActivity extends BaseActivity implements MainView, OnRefreshListener, OnLoadMoreListener,AppBarLayout.OnOffsetChangedListener {
    private MainViewPresenter mainViewPresenter;
    private String nextPageToken = "";
    private VideoAdapter videoAdapter;
    private ArrayList<Item> items = new ArrayList<>();
    private long total = 0;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private boolean refreshing = false;
    private boolean loading = false;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    @BindView(R.id.appBar)
    AppBarLayout appBar;

    @BindView(R.id.swipe_target)
    RecyclerView rvListVideo;

    @BindView(R.id.statistic)
    LinearLayoutCompat statistic;

    @BindView(R.id.tvTotal)
    AppCompatTextView tvTotal;

    @BindView(R.id.tvSub)
    AppCompatTextView tvSub;

    @BindView(R.id.tvView)
    AppCompatTextView tvView;

    @BindView(R.id.tvChannelTitle)
    AppCompatTextView tvChannelTitle;

    @BindView(R.id.imgChannelIcon)
    SimpleDraweeView imgChannelIcon;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    public void getChannelInfoSuccess(ChannelInfo channelInfo) {
        if (!compositeDisposable.isDisposed()) {
            tvChannelTitle.setText(channelInfo.getSnippet().getTitle().toString());
            Uri uri = Uri.parse(channelInfo.getSnippet().getThumbnails().getDefault().getUrl().toString());
            imgChannelIcon.setImageURI(uri);
            total = Long.parseLong(channelInfo.getStatistics().getVideoCount());
            tvTotal.setText(format(total));
            long sub = Long.parseLong(channelInfo.getStatistics().getSubscriberCount());
            tvSub.setText(format(sub));
            long view = Long.parseLong(channelInfo.getStatistics().getViewCount());
            tvView.setText(format(view));
            mainViewPresenter.getListVideo(CHANNEL_ID, API_KEY, nextPageToken);
        }
    }

    @Override
    public void getChannelInfoError() {
        if (!compositeDisposable.isDisposed()){
        }
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "K");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "B");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

//        long truncated = value / (divideBy / 10); //the number part of the output times 10
        int truncated = (int) (((double)value / (divideBy / 10)) + 0.5);
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
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
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        appBar.addOnOffsetChangedListener(this);
        int orientation = getResources().getConfiguration().orientation;
        if (getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
            linearLayoutManager = new LinearLayoutManager(this);
            rvListVideo.setLayoutManager(linearLayoutManager);
        } else if (orientation == ORIENTATION_LANDSCAPE){
            gridLayoutManager = new GridLayoutManager(this, 2);
            rvListVideo.setLayoutManager(gridLayoutManager);
        }
        videoAdapter = new VideoAdapter(items, this);
        rvListVideo.setAdapter(videoAdapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvListVideo.smoothScrollToPosition(0);
            }
        });
    }

    @Override
    public void loadData() {
        mainViewPresenter.getChannelInfo(CHANNEL_ID, API_KEY);
    }

    @Override
    public void getListVideoSuccess(ArrayList<Item> items, String nextPageToken, String totalResults) {
        if (!compositeDisposable.isDisposed()) {
            if (loading)
                loading = false;
            if (refreshing){
                this.items.clear();
                this.rvListVideo.removeAllViews();
                LogUtils.d("nextPageToken: " + nextPageToken);
                refreshing = false;
            }
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getVideoId().getKind().compareTo("youtube#video") != 0) {
                    items.remove(i);
                    i--;
                }
            }
            this.nextPageToken = nextPageToken;
            this.items.addAll(items);
            videoAdapter.notifyDataSetChanged();
            if (nextPageToken == null)
                Toast.makeText(getApplicationContext(),"Loaded all videos",Toast.LENGTH_LONG ).show();
        }
    }

    @Override
    public void getListVideoError() {
        if (!compositeDisposable.isDisposed()){
            if (refreshing){
                Toast.makeText(this,"Connection failed! Cannot refresh video!",Toast.LENGTH_LONG ).show();
            } else if (loading){
                Toast.makeText(this,"Connection failed! Cannot load video!",Toast.LENGTH_LONG ).show();
            }
        }
    }

    @Override
    public void onLoadMore() {
        swipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeToLoadLayout.setLoadingMore(false);
                loading = true;
                if (nextPageToken != null) {
                    mainViewPresenter.getListVideo(CHANNEL_ID, API_KEY, nextPageToken);
                } else {
                    Toast.makeText(getApplicationContext(),"Loaded all videos",Toast.LENGTH_LONG ).show();
                }
            }
        }, 1500);
    }

    @Override
    public void onRefresh() {
        swipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeToLoadLayout.setRefreshing(false);
                refreshing = true;
                mainViewPresenter.getListVideo(CHANNEL_ID, API_KEY,"");
            }
        }, 1500);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (!swipeToLoadLayout.isRefreshing())
            swipeToLoadLayout.setRefreshEnabled(verticalOffset==0);
    }
}
