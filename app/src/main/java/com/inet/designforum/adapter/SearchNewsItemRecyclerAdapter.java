package com.inet.designforum.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chienli.design_forum_all_lib.bean.Content;
import com.chienli.design_forum_all_lib.bean.News;
import com.inet.designforum.R;
import com.inet.designforum.activity.NewsActivity;
import com.inet.designforum.view.callback.DFRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchNewsItemRecyclerAdapter
        extends DFRecyclerViewAdapter<SearchNewsItemRecyclerAdapter.Holder>
        implements DFRecyclerViewAdapter.OnItemClickListener {

    private List<News> news;

    public SearchNewsItemRecyclerAdapter(List<News> news) {
        // 使用线程安全的List
        this.news = Collections.synchronizedList(new ArrayList<>(news));
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Holder holder = new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_search_news_item_recycler_item, viewGroup, false));
        // 点击事件就在这里实现, 依托于Holder实现
        holder.setOnItemClickListener(this);
        return holder;
    }

    /**
     * 点击事件
     *
     * @param view     itemView的子布局
     * @param position 所在的位置
     */
    @Override
    public void onItemClick(View view, int position) {
        // 点击就开启!
        NewsActivity.startNewsActivity(view.getContext(), news.get(position));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        holder.title.setText(news.get(i).getNewsTitle());

        List<Content> contents = new ArrayList<>();
        contents.add(new Content(news.get(i).getNewsText(), Content.ContentType.TEXT));
//        if (news.get(i).getNewsImageList() != null) {
//            for (String img : news.get(i).getNewsImageList()) {
//                contents.add(new Content(img, Content.ContentType.IMG));
//            }
//        }
        StringBuilder sb = new StringBuilder();
        for (Content c : contents) {
            if (c.getType() == Content.ContentType.TEXT) {
                sb.append(c.getContent());
            } else {
                sb.append("[图片]");
            }
        }
        holder.content.setText(sb.toString());

        if (news.get(i).getCover() != null) {
            holder.img.setVisibility(View.VISIBLE);
            Glide.with(holder.img).load(news.get(i).getCover()).into(holder.img);
        } else {
            holder.img.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    static class Holder extends DFRecyclerViewAdapter.Holder {
        TextView title;
        TextView userInfo;
        TextView likeAndConnection;
        TextView content;
        ImageView img;

        Holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_search_news_item_rcl_item_title);
            userInfo = itemView.findViewById(R.id.tv_search_news_item_rcl_item_user_info);
            likeAndConnection = itemView.findViewById(R.id.tv_search_news_item_rcl_item_like_and_collection);
            content = itemView.findViewById(R.id.tv_search_news_item_rcl_item_content);
            img = itemView.findViewById(R.id.imageView);
        }
    }
}
