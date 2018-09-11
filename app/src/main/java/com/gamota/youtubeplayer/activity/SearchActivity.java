package com.gamota.youtubeplayer.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.gamota.youtubeplayer.R;
import com.gamota.youtubeplayer.adapter.VideoAdapter;
import com.gamota.youtubeplayer.base.BaseActivity;
import com.gamota.youtubeplayer.model.listvideomodel.Item;
import com.gamota.youtubeplayer.presenter.SearchViewPresenter;
import com.gamota.youtubeplayer.presenteriplm.SearchViewPresenterIplm;
import com.gamota.youtubeplayer.view.SearchView;

import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static com.gamota.youtubeplayer.utils.Utils.API_KEY;
import static com.gamota.youtubeplayer.utils.Utils.CHANNEL_ID;

public class SearchActivity extends BaseActivity implements SearchView, OnLoadMoreListener, OnRefreshListener {
    private SearchViewPresenter searchViewPresenter;
    private String nextPageToken = "";
    private VideoAdapter videoAdapter;
    private ArrayList<Item> items = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private boolean refreshing = false;
    private boolean loading = false;
    private boolean isNewSearch = false;
    private String q ="";

    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.swipe_target)
    RecyclerView rvListVideo;

    @BindView(R.id.inputText)
    TextInputEditText inputText;

    @BindView(R.id.llError)
    LinearLayoutCompat llError;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.llNoVideo)
    LinearLayoutCompat llNoVideo;

    @Override
    public int initLayout() {
        return R.layout.activity_search;
    }

    @Override
    public void getExtraData() {

    }

    @Override
    public void createPresenter() {
        searchViewPresenter = new SearchViewPresenterIplm(this, compositeDisposable);
    }

    @Override
    public void createAdapter() {
        swipeToLoadLayout.setVisibility(View.GONE);
        llError.setVisibility(View.GONE);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == ORIENTATION_PORTRAIT) {
            linearLayoutManager = new LinearLayoutManager(this);
            rvListVideo.setLayoutManager(linearLayoutManager);
        } else if (orientation == ORIENTATION_LANDSCAPE){
            gridLayoutManager = new GridLayoutManager(this, 2);
            rvListVideo.setLayoutManager(gridLayoutManager);
        }
        videoAdapter = new VideoAdapter(items, this, null);
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
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nextPageToken = "";
                isNewSearch = true;
                (new android.os.Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String input = charSequence.toString().trim();
                        q = replaceWithPattern(input, "+");
                    }
                }, 2000);
                LogUtils.d(q);
                if (!q.equals("")) {
                    searchViewPresenter.searchVideo(CHANNEL_ID, API_KEY, nextPageToken, q);
                } else {
                    items.clear();
                    videoAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public String replaceWithPattern(String str,String replace){
        Pattern ptn = Pattern.compile("\\s+");
        Matcher mtch = ptn.matcher(str);
        return mtch.replaceAll(replace);
    }

    @Override
    public void searchVideoSuccess(ArrayList<Item> items, String nextPageToken) {
        if (!compositeDisposable.isDisposed()) {
            progressBar.setVisibility(View.GONE);
            if (items.size() == 0){
                llNoVideo.setVisibility(View.VISIBLE);
                swipeToLoadLayout.setVisibility(View.GONE);
            } else {
                llNoVideo.setVisibility(View.GONE);
                swipeToLoadLayout.setVisibility(View.VISIBLE);
            }
            if (isNewSearch){
                this.items.clear();
                videoAdapter.notifyDataSetChanged();
            }
            isNewSearch = false;
            llError.setVisibility(View.GONE);
            swipeToLoadLayout.setVisibility(View.VISIBLE);
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
//            if (nextPageToken == null)
//                Toast.makeText(getApplicationContext(),"Loaded all videos",Toast.LENGTH_LONG ).show();
        }
    }

    @Override
    public void searchVideoError() {
        if (!compositeDisposable.isDisposed()){
            if (refreshing){
                Toast.makeText(this,"Connection failed! Cannot refresh video!",Toast.LENGTH_LONG ).show();
            } else if (loading){
                Toast.makeText(this,"Connection failed! Cannot load video!",Toast.LENGTH_LONG ).show();
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
                    searchViewPresenter.searchVideo(CHANNEL_ID, API_KEY, nextPageToken, q);
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
                searchViewPresenter.searchVideo(CHANNEL_ID, API_KEY,"", q);
            }
        }, 1500);
    }
}
