package com.gamota.youtubeplayer.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gamota.youtubeplayer.R;
import com.gamota.youtubeplayer.adapter.VideoAdapter;
import com.gamota.youtubeplayer.adapter.ViewPagerAdapter;
import com.gamota.youtubeplayer.base.BaseActivity;
import com.gamota.youtubeplayer.fragments.AllListVideoFragment;
import com.gamota.youtubeplayer.fragments.FavouriteFragment;
import com.gamota.youtubeplayer.fragments.HistoryFragment;
import com.gamota.youtubeplayer.model.channel.ChannelInfo;
import com.gamota.youtubeplayer.model.listvideomodel.Item;
import com.gamota.youtubeplayer.presenter.MainViewPresenter;
import com.gamota.youtubeplayer.presenteriplm.MainViewPresenterIplm;
import com.gamota.youtubeplayer.view.MainView;

import java.util.ArrayList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.OnClick;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static com.gamota.youtubeplayer.utils.Utils.API_KEY;
import static com.gamota.youtubeplayer.utils.Utils.CHANNEL_ID;

public class MainActivity extends BaseActivity implements MainView{
    private MainViewPresenter mainViewPresenter;
    private AllListVideoFragment allVideoFragment;
    private HistoryFragment historyFragment;
    private FavouriteFragment favouriteFragment;
    private ViewPagerAdapter viewPagerAdapter;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.appBar)
    AppBarLayout appBar;

    @BindView(R.id.tvChannelTitle)
    AppCompatTextView tvChannelTitle;

    @BindView(R.id.imgChannelIcon)
    SimpleDraweeView imgChannelIcon;

    @BindView(R.id.tabs)
    TabLayout tabs;

    @BindView(R.id.llError)
    LinearLayoutCompat llError;

    @OnClick(R.id.imgSearch)
    void search(){
        Intent searchIntent = new Intent(this, SearchActivity.class);
        startActivity(searchIntent);
    }

    @Override
    public void getChannelInfoSuccess(ChannelInfo channelInfo) {
        if (!compositeDisposable.isDisposed()) {
            tvChannelTitle.setText(channelInfo.getSnippet().getTitle().toString());
            Uri uri = Uri.parse(channelInfo.getSnippet().getThumbnails().getDefault().getUrl().toString());
            imgChannelIcon.setImageURI(uri);
        }
    }

    @Override
    public void getChannelInfoError() {
        if (!compositeDisposable.isDisposed()){
            llError.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
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
        setUpViewPager(viewPager);
        tabs.setupWithViewPager(viewPager);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment = fm.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + tab.getPosition());
                if (fragment instanceof FavouriteFragment){
                    ((FavouriteFragment) fragment).refreshData();
                } else if (fragment instanceof HistoryFragment){
                    ((HistoryFragment) fragment).refreshData();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void loadData() {
        mainViewPresenter.getChannelInfo(CHANNEL_ID, API_KEY);
    }

    private void setUpViewPager(ViewPager viewPager){
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        allVideoFragment = new AllListVideoFragment();
        favouriteFragment = new FavouriteFragment();
        historyFragment = new HistoryFragment();

        viewPagerAdapter.addFragment(allVideoFragment, "All videos");
        viewPagerAdapter.addFragment(favouriteFragment, "Favourites");
        viewPagerAdapter.addFragment(historyFragment, "History");

        viewPager.setOffscreenPageLimit(10);
        viewPager.setAdapter(viewPagerAdapter);
    }
}
