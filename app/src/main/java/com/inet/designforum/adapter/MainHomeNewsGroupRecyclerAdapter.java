package com.inet.designforum.adapter;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.chienli.design_forum_all_lib.bean.News;
import com.chienli.design_forum_all_lib.bean.NewsGroup;
import com.inet.designforum.R;
import com.inet.designforum.activity.NewsActivity;
import com.inet.designforum.activity.SearchNewsActivity;

import java.util.ArrayList;
import java.util.List;

public class MainHomeNewsGroupRecyclerAdapter extends RecyclerView.Adapter<MainHomeNewsGroupRecyclerAdapter.ViewHolder> {

    private List<NewsGroup> group;
    private List<MainHomeNewsAdapter> childAdapters = new ArrayList<>();

    public MainHomeNewsGroupRecyclerAdapter(List<NewsGroup> group) {
        this.group = new ArrayList<>(group);
    }

    @NonNull
    @Override
    public MainHomeNewsGroupRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(View.inflate(viewGroup.getContext(), R.layout.layout_main_home_recycler_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MainHomeNewsGroupRecyclerAdapter.ViewHolder viewHolder, int i) {
        viewHolder.title.setText(group.get(i).getType().getName());
        if (viewHolder.rcl.getAdapter() == null) {
            LinearLayoutManager manager = new LinearLayoutManager(viewHolder.itemView.getContext());
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            MainHomeNewsAdapter adapter = new MainHomeNewsAdapter(group.get(i).getNews(), group.get(i).getType());
            viewHolder.rcl.setAdapter(adapter);
            childAdapters.add(adapter);
            viewHolder.rcl.setLayoutManager(manager);
        } else {
            MainHomeNewsAdapter adapter = (MainHomeNewsAdapter) viewHolder.rcl.getAdapter();
            adapter.update(group.get(i).getNews());
        }
    }

    public void update(List<NewsGroup> group) {
        this.group = group;
        for (MainHomeNewsAdapter adapter : childAdapters) {
            for (NewsGroup g : group) {
                if (g.getType() == adapter.getType()) {
                    adapter.childUpdate(g.getNews());
                    new Handler(Looper.getMainLooper()).post(adapter::notifyDataSetChanged);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return group.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView rcl;
        TextView title;
        TextView more;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            rcl = itemView.findViewById(R.id.rcl_main_home_recycler_item);
            title = itemView.findViewById(R.id.tv_main_home_recycler_item_title);
            more = itemView.findViewById(R.id.tv_main_home_recycler_item_more);

            title.setOnClickListener(titleOnClickListener);
            more.setOnClickListener(moreOnClickListener);

        }
    }

    private View.OnClickListener titleOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO : 在这里实现大分类的点击事件
            String title = ((TextView) v).getText().toString();
            Toast.makeText(v.getContext(), title, Toast.LENGTH_SHORT).show();
            SearchNewsActivity.startActivity(v.getContext(), title);
        }
    };
    private View.OnClickListener moreOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO : 在这里实现 "more" 的点击事件
            String title = ((TextView) ((View) v.getParent()).findViewById(R.id.tv_main_home_recycler_item_title)).getText().toString();
            Toast.makeText(v.getContext(), title, Toast.LENGTH_SHORT).show();
            SearchNewsActivity.startActivity(v.getContext(), title);
        }
    };

    class MainHomeNewsAdapter extends RecyclerView.Adapter<MainHomeNewsAdapter.ViewHolder> implements View.OnClickListener {

        private List<News> news;
        private NewsGroup.NewsType type;

        MainHomeNewsAdapter(List<News> news, NewsGroup.NewsType type) {
            this.news = new ArrayList<>(news);
            this.type = type;
        }

        public NewsGroup.NewsType getType() {
            return type;
        }

        public void childUpdate(List<News> news) {
            this.news.clear();
            this.news.addAll(news);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(this::notifyDataSetChanged);

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(View.inflate(viewGroup.getContext(), R.layout.layout_main_home_recycler_item_recycler_item, null));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            // 放数据啦, 给点击事件用
            viewHolder.itemView.setTag(R.id.layout_main_home_recycler_item_recycler_item_key, news.get(i));
            viewHolder.title.setText(news.get(i).getNewsTitle());
        }

        @Override
        public int getItemCount() {
            return news.size();
        }

        @Override
        public void onClick(View v) {
            // TODO : 在这里实现各个item的点击事件
            News n = (News) v.getTag(R.id.layout_main_home_recycler_item_recycler_item_key);
            NewsActivity.startNewsActivity(v.getContext(), n);
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView title;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.title = itemView.findViewById(R.id.tv_main_home_recycler_item_recycler_title);
                itemView.setOnClickListener(MainHomeNewsAdapter.this);
            }
        }

        void update(List<News> news) {
            this.news = new ArrayList<>(news);
        }
    }
}
