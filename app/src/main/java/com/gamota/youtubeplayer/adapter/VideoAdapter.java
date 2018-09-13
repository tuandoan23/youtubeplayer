package com.gamota.youtubeplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.apkfuns.logutils.LogUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gamota.youtubeplayer.R;
import com.gamota.youtubeplayer.activity.ContentVideoActivity;
import com.gamota.youtubeplayer.activity.MainActivity;
import com.gamota.youtubeplayer.database.DBHelper;
import com.gamota.youtubeplayer.event.MessageEvent;
import com.gamota.youtubeplayer.fragments.FavouriteFragment;
import com.gamota.youtubeplayer.fragments.HistoryFragment;
import com.gamota.youtubeplayer.model.listvideomodel.Item;
import com.gamota.youtubeplayer.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private ArrayList<Item> arrayListVideo;
    private Context context;
    private Fragment fragment;
    private DBHelper db;
    private boolean isYoutubeInstalled;

    public VideoAdapter(ArrayList<Item> items, Context context, Fragment fragment) {
        arrayListVideo = items;
        this.context = context;
        this.fragment = fragment;
        isYoutubeInstalled = Utils.isAppInstalled(Utils.packageName, fragment);
        db = new DBHelper(context);
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
                Item item = arrayListVideo.get(position);
                db.insertRecently(item);
                String videoId = video.getId().getVideoId();
                String videoTitle = video.getSnippet().getTitle();
                Intent newIntent = new Intent(context, ContentVideoActivity.class );
                newIntent.putExtra("videoId", videoId);
                newIntent.putExtra("videoTitle", videoTitle);
                newIntent.putExtra("video", item);
                if (fragment instanceof FavouriteFragment){
                    newIntent.putExtra("favourite", true);
                }
                if (isYoutubeInstalled) {
                    context.startActivity(newIntent);
                    EventBus.getDefault().post(new MessageEvent(true));
                } else {
                    installSuggestDialog();
                }
            }
        });
    }

    private void installSuggestDialog(){
        new MaterialDialog.Builder(fragment.getContext())
                .title(R.string.title_dialog)
                .content(R.string.content)
                .positiveText("Yes")
                .negativeText("No")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        openPlayStore();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    private void openPlayStore(){
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Utils.packageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + Utils.packageName)));
        }
    }

    @Override
    public int getItemCount() {
        return arrayListVideo.size();
    }
}
