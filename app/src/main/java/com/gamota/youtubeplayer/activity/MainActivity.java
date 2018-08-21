package com.gamota.youtubeplayer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.apkfuns.logutils.LogUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gamota.youtubeplayer.R;
import com.gamota.youtubeplayer.adapter.VideoAdapter;
import com.gamota.youtubeplayer.base.BaseActivity;
import com.gamota.youtubeplayer.model.Channel.ChannelInfo;
import com.gamota.youtubeplayer.model.ListVideoModel.Item;
import com.gamota.youtubeplayer.presenter.MainViewPresenter;
import com.gamota.youtubeplayer.presenteriplm.MainViewPresenterIplm;
import com.gamota.youtubeplayer.view.MainView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import butterknife.BindView;

import static com.gamota.youtubeplayer.utils.Utils.API_KEY;
import static com.gamota.youtubeplayer.utils.Utils.CHANNEL_ID;

public class MainActivity extends BaseActivity implements MainView{
    private MainViewPresenter mainViewPresenter;
    private String nextPageToken = "";
    private VideoAdapter videoAdapter;
    private ArrayList<Item> items = new ArrayList<>();
    private long total = 0;
    private LinearLayoutManager linearLayoutManager;
    private boolean loading = true;
    int visibleItemCount, totalItemCount, firstVisibleItem;
    private int previousTotal = 0;
    private int visibleThreshold = 1;
    private DecimalFormat formatter = new DecimalFormat("#########");

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rvListVideo)
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

    public static int roundUp(int src){
        int len = String.valueOf(src).length()-1;
        if (len==0) len=1;
        int d = (int) Math.pow((double) 10, (double) len);
        return (src + (d-1))/d*d;
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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvListVideo.smoothScrollToPosition(0);
//                rvListVideo.scrollToPosition(0);
            }
        });
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
                boolean isClicked = intent.getBooleanExtra("isClicked",false);
                if (isClicked){
                    Intent newIntent = new Intent(getApplicationContext(), ContentVideoActivity.class );
                    newIntent.putExtra("videoId", videoId);
                    newIntent.putExtra("videoTitle", videoTitle);
                    newIntent.putExtra("published", published);
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
            ArrayList<Item> itemsVideo = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getVideoId().getKind().compareTo("youtube#video") != 0) {
//                    itemsVideo.add(items.get(i));
                    items.remove(i);
                    i--;
                }
            }
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
                if(dy > 0 || firstVisibleItem == 0){
                    fab.hide();
                } else{
                    fab.show();
                }
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
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
