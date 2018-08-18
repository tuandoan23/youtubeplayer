package com.gamota.youtubeplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.apkfuns.logutils.LogUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gamota.youtubeplayer.OnLoadMoreListener;
import com.gamota.youtubeplayer.R;
import com.gamota.youtubeplayer.model.ListVideoModel.Item;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private ArrayList<Item> arrayListVideo;
    private Context context;

    public VideoAdapter(ArrayList<Item> items, Context context) {
        arrayListVideo = items;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvTitleVideo)
        AppCompatTextView tvTitleVideo;

        @BindView(R.id.tvDescription)
        AppCompatTextView tvDescription;

        @BindView(R.id.imgVideo)
        SimpleDraweeView imgVideo;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View listVideoView = inflater.inflate(R.layout.video, parent, false);
        return new ViewHolder(listVideoView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        Item video = arrayListVideo.get(position);
        Uri uri = Uri.parse(video.getSnippet().getThumbnails().getHigh().getUrl().toString());
        viewHolder.imgVideo.setImageURI(uri);
        viewHolder.tvTitleVideo.setText(video.getSnippet().getTitle());
        viewHolder.tvDescription.setText(video.getSnippet().getDescription().toString());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String videoId = video.getVideoId().getVideoId();
                String videoTitle = video.getSnippet().getTitle();
                String published = video.getSnippet().getPublishedAt();
                String description = video.getSnippet().getDescription();
                Intent intent = new Intent("custom-message");
                intent.putExtra("videoId", videoId);
                intent.putExtra("videoTitle", videoTitle);
                intent.putExtra("published", published);
                intent.putExtra("description", description);
                intent.putExtra("isClicked", true);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return arrayListVideo.size();
    }
}
