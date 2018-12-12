package com.gamota.youtubeplayer.loadlayout;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.aspsine.swipetoloadlayout.SwipeLoadMoreTrigger;
import com.aspsine.swipetoloadlayout.SwipeTrigger;

public class LoadMoreFooterView extends AppCompatTextView implements SwipeTrigger, SwipeLoadMoreTrigger {
    public LoadMoreFooterView(Context context) {
        super(context);
    }

    public LoadMoreFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onLoadMore() {
        setText("Loading more...");
    }

    @Override
    public void onPrepare() {
        setText("");
    }

    @Override
    public void onMove(int yScrolled, boolean isComplete, boolean automatic) {
        if (!isComplete) {
            if (yScrolled <= -getHeight()) {
                setText("Release to load more");
            } else {
                setText("Swipe to load more");
            }
        } else {
            setText("Load more returning...");
        }
    }

    @Override
    public void onRelease() {
        setText("Loading more...");
    }

    @Override
    public void onComplete() {
        setText("Complete");
    }

    @Override
    public void onReset() {
        setText("");
    }
}
