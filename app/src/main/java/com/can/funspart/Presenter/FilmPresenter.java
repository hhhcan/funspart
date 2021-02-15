package com.can.funspart.Presenter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.can.funspart.base.BasePresenter;
import com.can.funspart.bean.film.FilmsResponse;
import com.can.funspart.viewinterface.film.IGetFilmLiveView;
import com.can.funspart.viewinterface.film.IGetFilmRankingView;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/9/21 0021.
 */
public class FilmPresenter extends BasePresenter {

    public static final String FILM_URL = "http://front-gateway.mtime.com/ticket/schedule/movie/coming_list.api?locationId=290";

    public FilmPresenter(Context context) {
        super(context);
    }

    /**
     * 获取正在热映
     */
    public void getFilmLive(IGetFilmLiveView iGetFilmLiveView) {

        funspartApi.getFilmMediaItems(FILM_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(filmsResponse -> {
                    disPlayFilmLiveList(iGetFilmLiveView, filmsResponse, mContext);
                }, this::loadError);

    }


    private void disPlayFilmLiveList(IGetFilmLiveView iGetFilmLiveView, FilmsResponse filmsResponse, Context context) {
        //Toast.makeText(context,filmLive.toString(),Toast.LENGTH_SHORT).show();
        if (filmsResponse == null) {
            iGetFilmLiveView.getDataFail();
        } else {
            iGetFilmLiveView.getFilmLiveSuccess(filmsResponse);
            Log.e("test", filmsResponse.toString());
        }
    }

    /**
     * 获取影视排行榜
     *
     * @param start
     * @param count
     */

    public void getFilmRanking(IGetFilmRankingView iGetFilmRankingView, int start, int count, boolean isLoadMore) {
        funspartApi.getFilmRanking(FILM_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(filmsResponse -> {
                    disPlayFilmRankingList(iGetFilmRankingView, filmsResponse, isLoadMore);
                }, this::loadError);

    }

    private void disPlayFilmRankingList(IGetFilmRankingView iGetFilmRankingView, FilmsResponse filmsResponse, boolean isLoadMore) {
        //Toast.makeText(context,root.toString(),Toast.LENGTH_SHORT).show();
        Log.e("test", filmsResponse.toString());
        iGetFilmRankingView.getFilmRankingSuccess(filmsResponse, isLoadMore);
    }

    private void loadError(Throwable throwable) {
        throwable.printStackTrace();
        Toast.makeText(mContext, "网络不见了", Toast.LENGTH_SHORT).show();
    }

}
