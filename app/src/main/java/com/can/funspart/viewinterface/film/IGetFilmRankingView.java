package com.can.funspart.viewinterface.film;

import com.can.funspart.base.IBaseView;
import com.can.funspart.bean.film.FilmsResponse;


public interface IGetFilmRankingView extends IBaseView{

    void getFilmRankingSuccess(FilmsResponse filmsResponse, boolean isLoadMore);

    /**
     * 网络请求失败
     */
    void getDataFail();

}
