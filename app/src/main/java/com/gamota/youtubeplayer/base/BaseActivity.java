package com.gamota.youtubeplayer.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.gamota.youtubeplayer.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseActivity extends AppCompatActivity {
    private Unbinder unbinder;
    protected CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.default_color));
        }
        setContentView(initLayout());
        unbinder = ButterKnife.bind(this);
        compositeDisposable = new CompositeDisposable();
        getExtraData();
        createPresenter();
        createAdapter();
        loadData();
    }

    public abstract int initLayout();

    public abstract void getExtraData();

    public abstract void createPresenter();

    public abstract void createAdapter();

    public abstract void loadData();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        compositeDisposable.dispose();
    }
}

