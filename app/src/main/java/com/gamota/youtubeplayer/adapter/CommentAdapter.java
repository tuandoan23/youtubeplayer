package com.gamota.youtubeplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.text.emoji.widget.EmojiAppCompatTextView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.apkfuns.logutils.LogUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gamota.youtubeplayer.R;
import com.gamota.youtubeplayer.model.comment.Item;
import com.gamota.youtubeplayer.utils.Utils;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<Item> arrayListComment;
    private Context context;

    public CommentAdapter(ArrayList<Item> arrayListComment, Context context) {
        this.arrayListComment = arrayListComment;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvUser)
        AppCompatTextView tvUser;

        @BindView(R.id.tvComment)
        EmojiAppCompatTextView tvComment;

        @BindView(R.id.imgUserIcon)
        SimpleDraweeView imgUserIcon;

        @BindView(R.id.tvPublishedComment)
        AppCompatTextView tvPublishedComment;

        public ViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View listCommentView = inflater.inflate(R.layout.comment, parent, false);
        return new ViewHolder(listCommentView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        Item comment = arrayListComment.get(position);
        Uri uri = Uri.parse(comment.getSnippet().getTopLevelComment().getSnippetComment().getAuthorProfileImageUrl());
        viewHolder.imgUserIcon.setImageURI(uri);
        viewHolder.tvUser.setText(comment.getSnippet().getTopLevelComment().getSnippetComment().getAuthorDisplayName());
        viewHolder.tvComment.setText(Html.fromHtml(comment.getSnippet().getTopLevelComment().getSnippetComment().getTextOriginal()));
        viewHolder.tvPublishedComment.setText(Utils.getTimeAgo(Utils.RFC3339ToDate(comment.getSnippet().getTopLevelComment().getSnippetComment().getPublishedAt())));
    }

    @Override
    public int getItemCount() {
        if (arrayListComment != null)
            return arrayListComment.size();
        return 0;
    }
}
