package com.can.funspart.viewimpl.film;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.can.funspart.Presenter.FilmPresenter;
import com.can.funspart.R;
import com.can.funspart.adapter.FilmRankingAdapter;
import com.can.funspart.base.BaseFragment;
import com.can.funspart.bean.film.FilmsResponse;
import com.can.funspart.bean.film.Moviecomings;
import com.can.funspart.bean.film.FilmMediaItem;
import com.can.funspart.utils.ThemeUtils;
import com.can.funspart.viewinterface.film.IGetFilmRankingView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by wcan on 19/9/22.
 */
public class FilmRankingFragment extends BaseFragment implements IGetFilmRankingView,SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.id_swiperefreshlayout)
    SwipeRefreshLayout idSwiperefreshlayout;

    private FilmPresenter filmPresenter;
    private FilmRankingAdapter adapter;

    private LinearLayoutManager mLayoutManager;


    private int lastVisibleItem;
    private int pageCount;
    private final int PAGE_SIZE=10;
    private List<FilmMediaItem> mMediaItems;
    private Unbinder mUnbinder;

    public static FilmRankingFragment newInstance() {

        Bundle args = new Bundle();
        FilmRankingFragment fragment = new FilmRankingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fim_live, container, false);
        mUnbinder =ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        filmPresenter =new FilmPresenter(getActivity());

        filmPresenter.getFilmRanking(this,pageCount*PAGE_SIZE,PAGE_SIZE,false);
        mLayoutManager = new LinearLayoutManager(getContext());

        recyclerview.setLayoutManager(mLayoutManager);
        scrollRecycleView();
        idSwiperefreshlayout.setColorSchemeColors(ThemeUtils.getThemeColor());
        idSwiperefreshlayout.setOnRefreshListener(this);
    }

    @Override
    public void onDestroyView() {
        if (this.mUnbinder != null) {
            this.mUnbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public void getFilmRankingSuccess(FilmsResponse filmsResponse, boolean isLoadMore) {
        if(filmsResponse.getData().getMoviecomings()!=null){
            List<FilmMediaItem> mediaItems = new ArrayList<>();
            for(Moviecomings moviecomings: filmsResponse.getData().getMoviecomings()){
                if(moviecomings.getVideoId()>0){
                    FilmMediaItem filmMediaItem =new FilmMediaItem();
                    filmMediaItem.setMovieId(moviecomings.getVideoId());
                    filmMediaItem.setMovieName(moviecomings.getTitle());
                    filmMediaItem.setRating(moviecomings.getWantedCount());
                    filmMediaItem.setType(new ArrayList<String>(Arrays.asList(moviecomings.getType())));
                    filmMediaItem.setCoverImg(moviecomings.getImgUrl());
                    mediaItems.add(filmMediaItem);
                }
            }
            if(isLoadMore){
                mMediaItems.addAll(mediaItems);
                adapter.notifyDataSetChanged();
            }else {
                mMediaItems = mediaItems;
                adapter = new FilmRankingAdapter(getActivity(), mMediaItems);
                recyclerview.setAdapter(adapter);
            }
            adapter.notifyDataSetChanged();
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
                        if(adapter!=null) {
                            adapter.updateLoadStatus(adapter.LOAD_NONE);
                        }
                        return;

                    }
                    if (lastVisibleItem + 1 == mLayoutManager.getItemCount()) {
                        if(adapter!=null) {
                            adapter.updateLoadStatus(adapter.LOAD_PULL_TO);
                            // isLoadMore = true;
                            adapter.updateLoadStatus(adapter.LOAD_MORE);
                        }
                        //new Handler().postDelayed(() -> getBeforeNews(time), 1000);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                pageCount++;
                                filmPresenter.getFilmRanking(FilmRankingFragment.this,pageCount*PAGE_SIZE,PAGE_SIZE,true);
                            }
                        },1000) ;
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
        filmPresenter.getFilmRanking(this,pageCount*PAGE_SIZE,PAGE_SIZE,false);
        idSwiperefreshlayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (idSwiperefreshlayout != null) {
                    idSwiperefreshlayout.setRefreshing(false);
                }
            }
        },2000);

    }
}
