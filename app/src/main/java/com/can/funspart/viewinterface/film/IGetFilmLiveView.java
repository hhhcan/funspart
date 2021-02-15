package com.can.funspart.viewinterface.film;

import com.can.funspart.base.IBaseView;
import com.can.funspart.bean.film.FilmsResponse;


public interface IGetFilmLiveView extends IBaseView {

    void getFilmLiveSuccess(FilmsResponse filmsResponse);

    /**
     * 网络请求失败
     */
    void getDataFail();
}
