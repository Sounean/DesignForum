package com.inet.designforum.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.design_forum_all_lib.bean.Content;
import com.chienli.design_forum_all_lib.bean.News;
import com.inet.designforum.R;
import com.inet.designforum.util.DisplayUtil;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.Holder> {
    private List<Content> contents;
    private News news;

    public NewsRecyclerAdapter(List<Content> contents, News news) {
        this.contents = contents;
        this.news = news;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(View.inflate(viewGroup.getContext(), R.layout.layout_news_recycler_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        Content content = contents.get(i);
        if (content.getType() == Content.ContentType.TEXT) {
            holder.tv.setVisibility(View.VISIBLE);
            holder.img.setVisibility(View.GONE);
            holder.tv.setText(content.getContent());
        } else if (content.getType() == Content.ContentType.IMG) {
            holder.tv.setVisibility(View.GONE);
            holder.img.setVisibility(View.VISIBLE);
            holder.img.setMaxWidth(DisplayUtil.getDisplayWidth(holder.img.getContext()) - DisplayUtil.dp2px(holder.img.getContext(),24)); // 屏幕宽度减掉24dp
            Glide.with(holder.itemView.getContext()).load(DesignForumApplication.HOST + "images/news/" + news.getPath() + "/" + content.getContent()).into(holder.img);
        }
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        FrameLayout fl;
        TextView tv;
        ImageView img;

        Holder(@NonNull View itemView) {
            super(itemView);
            fl = itemView.findViewById(R.id.fl_news_recycler);
            tv = itemView.findViewById(R.id.tv_news_recycler);
            img = itemView.findViewById(R.id.img_news_recycler);
        }
    }
}
