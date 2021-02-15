package com.can.funspart.viewimpl.book;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.can.funspart.Presenter.WanAndroidPresenter;
import com.can.funspart.R;
import com.can.funspart.adapter.AndroidCategoryAdapter;
import com.can.funspart.base.BaseFragment;
import com.can.funspart.bean.book.AndroidCategory;
import com.can.funspart.bean.book.CategoryListDetail;
import com.can.funspart.utils.ThemeUtils;
import com.can.funspart.viewinterface.film.IGetCategoryListView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookContentFragment extends BaseFragment implements IGetCategoryListView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.id_swiperefreshlayout)
    SwipeRefreshLayout idSwiperefreshlayout;

    private WanAndroidPresenter wanAndroidPresenter;
    private AndroidCategoryAdapter adapter;

    private LinearLayoutManager mLayoutManager;


    private int lastVisibleItem;
    private int pageCount = 1;
    private CategoryListDetail mRoot;
    private int position;
    private AndroidCategory.DataBean currentCategory;
    private int lastRequestDataSize;
    private boolean hasPullLoading;

    public static BookContentFragment newInstance(int position, String title) {

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("position", position);
        BookContentFragment fragment = new BookContentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fim_live, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            position = args.getInt("position");
            currentCategory = BookFragment.androidCategory.getData().get(position);
        }
        wanAndroidPresenter = new WanAndroidPresenter(getActivity());
        wanAndroidPresenter.getAndroidCategoryList(this, currentCategory.getId(), pageCount, false);
        mLayoutManager = new LinearLayoutManager(getContext());

        recyclerview.setLayoutManager(mLayoutManager);
        scrollRecycleView();
        idSwiperefreshlayout.setColorSchemeColors(ThemeUtils.getThemeColor());
        idSwiperefreshlayout.setOnRefreshListener(this);
    }


    @Override
    public void getCategoryListSuccess(CategoryListDetail categorylist, boolean isLoadMore) {
        if (isLoadMore) {
            lastRequestDataSize = mRoot.getData().getDatas().size();
            mRoot.getData().getDatas().addAll(categorylist.getData().getDatas());
            adapter.notifyDataSetChanged();
        } else {
            mRoot = categorylist;
            adapter = new AndroidCategoryAdapter(getActivity(), mRoot);
            recyclerview.setAdapter(adapter);
            hasPullLoading = true;
        }
    }

    @Override
    public void getDataFail() {

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
                            adapter.updateLoadStatus(adapter.LOAD_MORE);
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                pageCount++;
                                wanAndroidPresenter.getAndroidCategoryList(BookContentFragment.this, currentCategory.getId(), pageCount, true);
                            }
                        }, 1000);
                        idSwiperefreshlayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (idSwiperefreshlayout != null) {
                                    idSwiperefreshlayout.setRefreshing(false);
                                    if (lastRequestDataSize == mRoot.getData().getDatas().size()) {
                                        adapter.updateLoadStatus(adapter.LOAD_NONE);
//                                        adapter.updateLoadStatus(adapter.LOAD_END);
                                        return;
                                    }
                                }
                            }
                        }, 2000);
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
    public void onRefresh() {
        if (hasPullLoading) {
            idSwiperefreshlayout.setRefreshing(false);
            return;
        }
        wanAndroidPresenter.getAndroidCategoryList(BookContentFragment.this, currentCategory.getId(), pageCount, false);
        idSwiperefreshlayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (idSwiperefreshlayout != null) {
                    idSwiperefreshlayout.setRefreshing(false);
                }
            }
        }, 2000);

    }
}
