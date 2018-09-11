package com.gamota.youtubeplayer.fragments;

import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.gamota.youtubeplayer.R;
import com.gamota.youtubeplayer.adapter.VideoAdapter;
import com.gamota.youtubeplayer.base.BaseFragment;
import com.gamota.youtubeplayer.database.DBHelper;
import com.gamota.youtubeplayer.model.listvideomodel.Item;

import java.util.ArrayList;

import butterknife.BindView;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class HistoryFragment extends BaseFragment implements OnLoadMoreListener, OnRefreshListener, AppBarLayout.OnOffsetChangedListener {
    private DBHelper db;
    private VideoAdapter videoAdapter;
    private ArrayList<Item> items = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private int size = 0;

    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    @BindView(R.id.swipe_target)
    RecyclerView rvListVideo;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.llError)
    LinearLayoutCompat llError;

    @Override
    public int initLayout() {
        return R.layout.fragment_history_video;
    }

    @Override
    public void getExtraData() {

    }

    @Override
    public void createPresenter() {

    }

    @Override
    public void createAdapter() {
        db = new DBHelper(getContext());
        setOnScrollListener();
        swipeToLoadLayout.setOnLoadMoreListener(this);
        swipeToLoadLayout.setOnRefreshListener(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvListVideo.smoothScrollToPosition(0);
            }
        });
    }

    public void refreshData(){
        items = db.getAllRecently();
        size = items.size();
        if (size == 0){
            llError.setVisibility(View.VISIBLE);
            swipeToLoadLayout.setVisibility(View.GONE);
        } else {
            llError.setVisibility(View.GONE);
            swipeToLoadLayout.setVisibility(View.VISIBLE);
        }
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == ORIENTATION_PORTRAIT) {
            linearLayoutManager = new LinearLayoutManager(this.getContext());
            rvListVideo.setLayoutManager(linearLayoutManager);
        } else if (orientation == ORIENTATION_LANDSCAPE){
            gridLayoutManager = new GridLayoutManager(this.getContext(), 2);
            rvListVideo.setLayoutManager(gridLayoutManager);
        }
        videoAdapter = new VideoAdapter(items, this.getContext(), (Fragment) this);
        rvListVideo.setAdapter(videoAdapter);
    }

    @Override
    public void loadData() {

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
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (!swipeToLoadLayout.isRefreshing())
            swipeToLoadLayout.setRefreshEnabled(verticalOffset==0);
    }

    @Override
    public void onLoadMore() {
        swipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeToLoadLayout.setLoadingMore(false);
                if (items.size() != size) {
                    refreshData();
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
                refreshData();
            }
        }, 1500);
    }
}
