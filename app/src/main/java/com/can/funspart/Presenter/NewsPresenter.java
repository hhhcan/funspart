package com.can.funspart.Presenter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.can.funspart.api.ApiRetrofit;
import com.can.funspart.base.BasePresenter;
import com.can.funspart.bean.news.NewsSummary;
import com.can.funspart.viewinterface.news.IGetNewsByTypeView;

import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class NewsPresenter extends BasePresenter {
    public NewsPresenter(Context context) {
        super(context);
    }

    /**
     * @param
     */
    public void getNewsListByType(IGetNewsByTypeView iGetNewsByTypeView, String type, String id, int startPage, boolean isLoadMore){
//        String type ="headline";
//        String id ="T1348647909107";
//        int startPage= 0;
        funspartApi.getNewsList(ApiRetrofit.getCacheControl(),type, id, startPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newsRoot -> {
                    disPlaySearchedNews(iGetNewsByTypeView,newsRoot,isLoadMore);
                },this::loadError);


    }



    private void disPlaySearchedNews(IGetNewsByTypeView iGetNewsByTypeView, Map<String, List<NewsSummary>> newsRoot, boolean isLoadMore) {
        iGetNewsByTypeView.getNewsByTypeSuccess(newsRoot,isLoadMore);
        Log.e("test", newsRoot.toString());
    }

    private void loadError( Throwable throwable) {
        throwable.printStackTrace();
        Toast.makeText(mContext, "网络不见了", Toast.LENGTH_SHORT).show();
    }
}
