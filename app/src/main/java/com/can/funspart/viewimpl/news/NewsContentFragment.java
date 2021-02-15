package com.can.funspart.viewimpl.news;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.can.funspart.Presenter.NewsPresenter;
import com.can.funspart.R;
import com.can.funspart.adapter.NewsAdapter;
import com.can.funspart.base.BaseFragment;
import com.can.funspart.bean.news.NewsChannelTable;
import com.can.funspart.bean.news.NewsSummary;
import com.can.funspart.utils.ThemeUtils;
import com.can.funspart.viewinterface.news.IGetNewsByTypeView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsContentFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, IGetNewsByTypeView {


    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.id_swiperefreshlayout)
    SwipeRefreshLayout idSwiperefreshlayout;
    private int position;
    private NewsAdapter adapter;
    private int lastVisibleItem;
    private LinearLayoutManager mLayoutManager;
    private NewsPresenter newsPresenter;
    private List<NewsSummary> newsSummaryList;
    private NewsChannelTable currentNewsChannel;
    public static NewsContentFragment newInstance(int position, String title) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("position", position);
        NewsContentFragment fragment = new NewsContentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_content, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private int pageCount = 0;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            position = args.getInt("position");
            currentNewsChannel = NewsFragment.newsChannelTables.get(position);
        }
        newsSummaryList = new ArrayList<>();
        scrollRecycleView();
        idSwiperefreshlayout.setColorSchemeColors(ThemeUtils.getThemeColor());
        idSwiperefreshlayout.setOnRefreshListener(this);
        newsPresenter = new NewsPresenter(getActivity());
        newsPresenter.getNewsListByType(this, currentNewsChannel.getNewsChannelType(), currentNewsChannel.getNewsChannelId(), pageCount, false);
        adapter = new NewsAdapter(getActivity());
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setAdapter(adapter);
    }

    /**
     * recyclerView Scroll listener , maybe in here is wrong ?
     */
    public void scrollRecycleView() {
        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                    if (mLayoutManager.getItemCount() == 1) {
                        if (adapter != null) {
                            adapter.updateLoadStatus(adapter.LOAD_NONE);
                        }
                        return;

                    }
                    if (lastVisibleItem + 1 == mLayoutManager.getItemCount()) {
                        if (adapter != null) {
                            adapter.updateLoadStatus(adapter.LOAD_PULL_TO);
                            // isLoadMore = true;
                            adapter.updateLoadStatus(adapter.LOAD_MORE);
                        }
                        //new Handler().postDelayed(() -> getBeforeNews(time), 1000);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                pageCount++;
                                newsPresenter.getNewsListByType(NewsContentFragment.this, currentNewsChannel.getNewsChannelType(), currentNewsChannel.getNewsChannelId(), pageCount, true);
                            }
                        }, 1000);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    @Override
    public void getNewsByTypeSuccess(Map<String, List<NewsSummary>> newsRoot, boolean isLoadMore) {
        if (isLoadMore) {
            newsSummaryList.addAll(newsRoot.get(currentNewsChannel.getNewsChannelId()));
        } else {
            newsSummaryList.clear();
            newsSummaryList.addAll(newsRoot.get(currentNewsChannel.getNewsChannelId()));
        }
        adapter.setList(newsSummaryList);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onRefresh() {
        newsPresenter.getNewsListByType(NewsContentFragment.this, currentNewsChannel.getNewsChannelType(), currentNewsChannel.getNewsChannelId(), pageCount, false);
        idSwiperefreshlayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (idSwiperefreshlayout != null) {
                    idSwiperefreshlayout.setRefreshing(false);
                }
            }
        }, 1500);
    }
}
