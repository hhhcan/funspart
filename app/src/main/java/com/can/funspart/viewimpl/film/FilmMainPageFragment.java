package com.can.funspart.viewimpl.film;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.can.funspart.MyApp;
import com.can.funspart.Presenter.FilmPresenter;
import com.can.funspart.R;
import com.can.funspart.adapter.MainAdapter;
import com.can.funspart.bean.film.BaseVideoData;
import com.can.funspart.bean.film.VideoListData;
import com.can.funspart.bean.film.event.RefreshEvent;
import com.can.funspart.okhttp.http.OkHttpClientManager;
import com.can.funspart.utils.NoDoubleClickUtils;
import com.can.funspart.utils.ScreenUtils;
import com.can.funspart.utils.ThemeUtils;
import com.can.funspart.utils.ToastUtil;
import com.can.funspart.utils.WeakDataHolder;
import com.can.funspart.view.LoadFrameLayout;
import com.can.funspart.viewimpl.film.video.player.VerticalVideoActivity;
import com.can.funspart.widget.MyCustomHeader;
import com.can.mc.cyg.cygptr.PtrFrameLayout;
import com.can.mc.cyg.cygptr.recyclerview.RecyclerAdapterWithHF;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.share.can.cygwidget.loadmore.OnScrollToBottomLoadMoreListener;
import cn.share.can.cygwidget.recyclerview.PtrRecyclerViewUIComponent;
import cn.share.can.cygwidget.recyclerview.adapter.CygBaseRecyclerAdapter;
import cn.share.can.cygwidget.refersh.OnPullToRefreshListener;
import okhttp3.Request;


public class FilmMainPageFragment extends Fragment implements CygBaseRecyclerAdapter.OnItemClickListener<MainAdapter.MainViewHolder> {
    @BindView(R.id.am_ptr_framelayout)
    PtrRecyclerViewUIComponent ptrRecyclerViewUIComponent;
    @BindView(R.id.ar_empty_view)
    View emptyView;
    @BindView(R.id.load_frameLayout)
    LoadFrameLayout loadFrameLayout;

    TextView mRetry;

    private MainAdapter adapter;

    private RecyclerAdapterWithHF mAdapter;

    private List<BaseVideoData> mList = new ArrayList<>();

    boolean isLoadMore = false;

    MyCustomHeader header;

    public static FilmMainPageFragment newInstance() {

        Bundle args = new Bundle();
        FilmMainPageFragment fragment = new FilmMainPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    protected Unbinder mUnbinder;

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);
        onViewReallyCreated(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    protected void onViewReallyCreated(View view) {
        mUnbinder = ButterKnife.bind(this, view);
        mRetry = (TextView) loadFrameLayout.findViewById(R.id.tv_retry);

        mRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    getShortVideoData();
                }
            }
        });

        adapter = new MainAdapter(getActivity(), this);
        mAdapter = new RecyclerAdapterWithHF(adapter);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (mAdapter.isHeader(position) || mAdapter.isFooter(position)) ? gridLayoutManager.getSpanCount() : 1;
            }
        });

        ptrRecyclerViewUIComponent.setLayoutManager(gridLayoutManager);
        ptrRecyclerViewUIComponent.setAdapter(mAdapter);
//        ptrRecyclerViewUIComponent.setEmptyView(emptyView);

        initHeader();

        ptrRecyclerViewUIComponent.delayRefresh(100);
        ptrRecyclerViewUIComponent.setLoadMoreEnable(true);


        ptrRecyclerViewUIComponent.setOnPullToRefreshListener(new OnPullToRefreshListener() {
            @Override
            public void onPullToRefresh() {
                isLoadMore = false;
                if (mList != null && mList.size() > 0) {
                    mList.clear();
                }

                getShortVideoData();
            }
        });

        ptrRecyclerViewUIComponent.setOnScrollToBottomLoadMoreListener(new OnScrollToBottomLoadMoreListener() {
            @Override
            public void onScrollToBottomLoadMore() {
                isLoadMore = true;
                getShortVideoData();
            }
        });


    }

    private void initHeader() {
        header = new MyCustomHeader(getActivity());
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(PtrFrameLayout.LayoutParams.MATCH_PARENT, PtrFrameLayout.LayoutParams.WRAP_CONTENT));
        header.setPadding(0, ScreenUtils.dipToPx(MyApp.getInstance(), 15), 0, ScreenUtils.dipToPx(MyApp.getInstance(), 10));

        header.setUp(ptrRecyclerViewUIComponent);
        header.getTvtitle().setTextColor(ThemeUtils.getThemeColor());

        ptrRecyclerViewUIComponent.setHeaderView(header);

        ptrRecyclerViewUIComponent.setDurationToCloseHeader(600);
        ptrRecyclerViewUIComponent.setLoadingMinTime(1200);

        ptrRecyclerViewUIComponent.addPtrUIHandler(header);

    }

    public void getShortVideoData() {
        String url = FilmPresenter.FILM_URL;
        OkHttpClientManager.getAsyn(url, new OkHttpClientManager.StringCallback() {
            @Override
            public void onResponse(String response) {
                LogUtils.json(response);
                loadFrameLayout.showContentView();
                try {
                    VideoListData listData = VideoListData.fromJSONData(response);
                    LogUtils.e(listData.getVideoDataList().size());
                    header.getTvtitle().setText("发现" + listData.getVideoDataList().size() + "条精彩视频");
                    ptrRecyclerViewUIComponent.removeView(header);
                    ptrRecyclerViewUIComponent.setHeaderView(header);

                    if (isLoadMore) {
                        mList.addAll(listData.getVideoDataList());
                        adapter.setDataList(mList, false);
                        mAdapter.notifyDataSetChanged();
                        ptrRecyclerViewUIComponent.loadMoreComplete(true);

                    } else {
                        mList = listData.getVideoDataList();
                        if (mList.size() == 0) {
                            emptyView.setVisibility(View.VISIBLE);
                            ptrRecyclerViewUIComponent.setLoadMoreEnable(false);
                        } else {
                            emptyView.setVisibility(View.GONE);
                            ptrRecyclerViewUIComponent.setLoadMoreEnable(true);
                        }

                        adapter.setDataList(mList);
                        mAdapter.notifyDataSetChanged();
                        ptrRecyclerViewUIComponent.refreshComplete();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Request request, IOException e) {
                ptrRecyclerViewUIComponent.loadMoreComplete(false);
                ptrRecyclerViewUIComponent.refreshComplete();
                ToastUtil.showToast("网络连接失败");
                header.getTvtitle().setText("网络连接失败，请重试");
                ptrRecyclerViewUIComponent.removeView(header);
                ptrRecyclerViewUIComponent.setHeaderView(header);

                loadFrameLayout.showErrorView();

            }
        });
    }

    /**
     * 加载过程中不让点击
     */
    @Override
    public void onItemClick(int position) {
        if (ptrRecyclerViewUIComponent.isLoadingMore() || ptrRecyclerViewUIComponent.isRefreshing()) {
            return;
        }

        Intent intent = new Intent(getActivity(), VerticalVideoActivity.class);
        WeakDataHolder.getInstance().saveData("videoUrlList", mList);
        intent.putExtra("position", position);
        getActivity().startActivity(intent);

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onDestroyView() {
        if (this.mUnbinder != null) {
            this.mUnbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getImageData(RefreshEvent event) {
        adapter.setDataList(event.getList());
        mAdapter.notifyDataSetChanged();

        ptrRecyclerViewUIComponent.getRecyclerView().scrollToPosition(event.getPosition());
    }

}
