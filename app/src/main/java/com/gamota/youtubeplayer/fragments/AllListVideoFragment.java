package com.gamota.youtubeplayer.fragments;

import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.gamota.youtubeplayer.R;
import com.gamota.youtubeplayer.adapter.VideoAdapter;
import com.gamota.youtubeplayer.base.BaseFragment;
import com.gamota.youtubeplayer.model.listvideomodel.Item;
import com.gamota.youtubeplayer.presenter.ListVideoPresenter;
import com.gamota.youtubeplayer.presenteriplm.ListVideoPresenterIplm;
import com.gamota.youtubeplayer.view.ListVideoView;

import java.util.ArrayList;

import butterknife.BindView;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static com.gamota.youtubeplayer.utils.Utils.API_KEY;
import static com.gamota.youtubeplayer.utils.Utils.CHANNEL_ID;

public class AllListVideoFragment extends BaseFragment implements ListVideoView,OnLoadMoreListener, OnRefreshListener, AppBarLayout.OnOffsetChangedListener {
    private boolean refreshing = false;
    private boolean loading = false;
    private String nextPageToken = "";
    private VideoAdapter videoAdapter;
    private ListVideoPresenter listVideoPresenter;
    private ArrayList<Item> items = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;

    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    @BindView(R.id.swipe_target)
    RecyclerView rvListVideo;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.llError)
    LinearLayoutCompat llError;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    public int initLayout() {
        return R.layout.fragment_all_video;
    }

    @Override
    public void getExtraData() {

    }

    @Override
    public void createPresenter() {
        listVideoPresenter = new ListVideoPresenterIplm(this, compositeDisposable);
    }

    @Override
    public void createAdapter() {
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == ORIENTATION_PORTRAIT) {
            linearLayoutManager = new LinearLayoutManager(this.getContext());
            rvListVideo.setLayoutManager(linearLayoutManager);
        } else if (orientation == ORIENTATION_LANDSCAPE){
            gridLayoutManager = new GridLayoutManager(this.getContext(), 2);
            rvListVideo.setLayoutManager(gridLayoutManager);
        }
        videoAdapter = new VideoAdapter(items, this.getContext(), this);
        rvListVideo.setAdapter(videoAdapter);
        setOnScrollListener();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvListVideo.smoothScrollToPosition(0);
            }
        });
    }

    @Override
    public void loadData() {
        listVideoPresenter.getListVideo(CHANNEL_ID, API_KEY, nextPageToken);
    }

    @Override
    public void getListVideoSuccess(ArrayList<Item> items, String nextPageToken, String totalResults) {
        if (!compositeDisposable.isDisposed()) {
            progressBar.setVisibility(View.GONE);
            swipeToLoadLayout.setVisibility(View.VISIBLE);
            llError.setVisibility(View.GONE);
            if (loading)
                loading = false;
            if (refreshing){
                this.items.clear();
                videoAdapter.notifyDataSetChanged();
                refreshing = false;
            }
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getId().getKind().compareTo("youtube#video") != 0) {
                    items.remove(i);
                    i--;
                }
            }
            this.nextPageToken = nextPageToken;
            this.items.addAll(items);
            videoAdapter.notifyDataSetChanged();
            if (nextPageToken == null)
                Toast.makeText(this.getContext(),"Loaded all videos",Toast.LENGTH_LONG ).show();
        }
    }

    @Override
    public void getListVideoError() {
        if (!compositeDisposable.isDisposed()){
            if (refreshing){
                Toast.makeText(this.getContext(),"Connection failed! Cannot refresh video!",Toast.LENGTH_LONG ).show();
            } else if (loading){
                Toast.makeText(this.getContext(),"Connection failed! Cannot load video!",Toast.LENGTH_LONG ).show();
            } else if (nextPageToken == "") {
                progressBar.setVisibility(View.GONE);
                llError.setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);
            }
        }
    }

    private void setOnScrollListener(){
        rvListVideo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = 0;
                if (getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
                    firstVisibleItem  = linearLayoutManager.findFirstVisibleItemPosition();
                } else if (getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE){
                    firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();
                }
                if (dy > 0 || firstVisibleItem == 0){
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });
    }

    @Override
    public void onLoadMore() {
        swipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeToLoadLayout.setLoadingMore(false);
                loading = true;
                if (nextPageToken != null) {
                    listVideoPresenter.getListVideo(CHANNEL_ID, API_KEY, nextPageToken);
                } else {
                    Toast.makeText(getContext(),"Loaded all videos",Toast.LENGTH_LONG ).show();
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
                listVideoPresenter.getListVideo(CHANNEL_ID, API_KEY,"");
            }
        }, 1500);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (!swipeToLoadLayout.isRefreshing())
            swipeToLoadLayout.setRefreshEnabled(verticalOffset==0);
    }
}
