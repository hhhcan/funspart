package com.can.funspart.viewimpl.film;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.can.funspart.Presenter.FilmPresenter;
import com.can.funspart.R;
import com.can.funspart.adapter.FilmLiveAdapter;
import com.can.funspart.base.BaseFragment;
import com.can.funspart.base.EasyRecyclerViewAdapter;
import com.can.funspart.bean.film.FilmMediaItem;
import com.can.funspart.bean.film.FilmsResponse;
import com.can.funspart.bean.film.Moviecomings;
import com.can.funspart.utils.ScreenUtils;
import com.can.funspart.utils.ThemeUtils;
import com.can.funspart.viewimpl.film.video.player.SystemVideoActivity;
import com.can.funspart.viewinterface.film.IGetFilmLiveView;
import com.can.funspart.widget.SpacesItemDecoration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by wcan on 16/9/22.
 */
public class FilmLiveFragment extends BaseFragment implements IGetFilmLiveView, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.id_swiperefreshlayout)
    SwipeRefreshLayout idSwiperefreshlayout;
    private FilmPresenter filmPresenter;
    private FilmLiveAdapter filmLiveAdapter;
    protected Unbinder mUnbinder;
    public static FilmLiveFragment newInstance() {

        Bundle args = new Bundle();

        FilmLiveFragment fragment = new FilmLiveFragment();
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
    public void onDestroyView() {
        if (this.mUnbinder != null) {
            this.mUnbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        filmLiveAdapter = new FilmLiveAdapter(getActivity());
        filmPresenter = new FilmPresenter(getActivity());
        filmPresenter.getFilmLive(this);
        idSwiperefreshlayout.setColorSchemeColors(ThemeUtils.getThemeColor());
        idSwiperefreshlayout.setOnRefreshListener(this);
        recyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(ScreenUtils.dipToPx(getActivity(), 10), ScreenUtils.dipToPx(getActivity(), 10), ScreenUtils.dipToPx(getActivity(), 10), 0);
        recyclerview.addItemDecoration(spacesItemDecoration);
        recyclerview.setAdapter(filmLiveAdapter);
    }

    @Override
    public void getFilmLiveSuccess(FilmsResponse filmsResponse) {
        List<FilmMediaItem> mediaItems = new ArrayList<>();
        if(filmsResponse.getData().getMoviecomings()!=null){
            for(Moviecomings moviecomings: filmsResponse.getData().getMoviecomings()){
                if(moviecomings.getVideoId()>0){
                    FilmMediaItem filmMediaItem =new FilmMediaItem();
                    filmMediaItem.setMovieId(moviecomings.getVideoId());
                    filmMediaItem.setMovieName(moviecomings.getTitle());
                    filmMediaItem.setType(new ArrayList<String>(Arrays.asList(moviecomings.getType())));
                    filmMediaItem.setCoverImg(moviecomings.getImgUrl());
                    mediaItems.add(filmMediaItem);
                }
            }
        }
        filmLiveAdapter.setDatas(mediaItems);
        filmLiveAdapter.notifyDataSetChanged();
        filmLiveAdapter.setOnItemClickListener(new EasyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position, Object data) {
                //3.传递列表数据-对象-序列化
                Intent intent = new Intent(getActivity(), SystemVideoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("videolist", (Serializable) mediaItems);
                intent.putExtras(bundle);
                intent.putExtra("position", position);
                startThActivityByIntent(intent);
            }
        });
    }

    @Override
    public void getDataFail() {

    }

    @Override
    public void onRefresh() {
        filmPresenter.getFilmLive(this);
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
