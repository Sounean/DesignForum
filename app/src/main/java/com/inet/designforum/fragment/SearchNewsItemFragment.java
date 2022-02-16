package com.inet.designforum.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chienli.design_forum_all_lib.bean.News;
import com.chienli.design_forum_all_lib.bean.NewsGroup;
import com.inet.designforum.R;
import com.inet.designforum.adapter.SearchNewsItemRecyclerAdapter;
import com.inet.designforum.fragment.base.BaseFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

public class SearchNewsItemFragment extends BaseFragment implements OnRefreshLoadMoreListener {
    // 取出 getArgments的Bundle的NewsType
    public static final String NEWS_TYPE_KEY = "news type";
    private static final String NEWS_GROUP_KEY = "news group";

    // 该Fragment中的新闻类型
    private NewsGroup.NewsType newsType;

    /**
     * 禁止直接实例化此Fragment，使用getInstance方法
     */
    public SearchNewsItemFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 默认在空指针的时候给一个"艺术比赛"的类型
        newsType = (NewsGroup.NewsType) (getArguments() != null ?
                getArguments().getSerializable(NEWS_TYPE_KEY) :
                NewsGroup.NewsType.COMPETITION);

        List<NewsGroup> groups = (List<NewsGroup>) getArguments().getSerializable(NEWS_GROUP_KEY);
        for (NewsGroup group : groups){
            if (newsType == group.getType()){
                news = group.getNews();
            }
        }
        View view = inflater.inflate(R.layout.fragment_search_news_item, container, false);
        init(view);
        return view;
    }

    // Fragment 中只有一个Recycler和RefreshLayout
    private RecyclerView mRecycler;
    private SmartRefreshLayout mRefresh;

    @Override
    public void findViews(View view) {
        mRecycler = view.findViewById(R.id.rcl_search_news_item);
        mRefresh = view.findViewById(R.id.ref_search_news_item);
    }

    // 联网获取的数据
    private List<News> news = new ArrayList<>();

    @Override
    public void getData(View mView) {

    }

    @Override
    public void initViews(View mView) {
        mRecycler.setAdapter(new SearchNewsItemRecyclerAdapter(news));
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecycler.setLayoutManager(manager);

        // 设置滑动监听啦，是刷新和加载的集合(没有什么卵用，和单独实现是一样的)
        mRefresh.setOnRefreshLoadMoreListener(this);
    }

    public static SearchNewsItemFragment getInstance(NewsGroup.NewsType type, List<NewsGroup> groups) {
        SearchNewsItemFragment fragment = new SearchNewsItemFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NEWS_GROUP_KEY, new ArrayList<>(groups));
        bundle.putSerializable(NEWS_TYPE_KEY, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 加载更多应该是在 news 这个集合后添加数据然后通知适配器刷新
     * 或者传给适配器？？还是直接传递比较好，这样的话，适配器中的集合最好是并发安全的
     *
     * @param refreshLayout 适配器所绑定的RefreshLayout
     */
    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishLoadMore(2000);
    }

    /**
     * 刷新应该是在 获取完整数据后将自己的数据直接
     *
     * @param refreshLayout 适配器所绑定的RefreshLayout
     */
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishRefresh(2000, true);
    }
}
