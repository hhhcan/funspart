package com.can.funspart.Presenter;

import android.content.Context;
import android.widget.Toast;

import com.can.funspart.base.BasePresenter;
import com.can.funspart.bean.book.CategoryListDetail;
import com.can.funspart.viewinterface.film.IGetCategoryListView;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/9/21 0021.
 */
public class WanAndroidPresenter extends BasePresenter {

    public String GET_CATEGORY_LIST_URL; //拼接获取Android栏目类别内容。

    public WanAndroidPresenter(Context context) {
        super(context);
    }

    /**
     * 获取某一个分类下项目列表数据，分页展示
     *
     * @param cid         ：当前类别id
     * @param currentPage 当前页
     */

    public void getAndroidCategoryList(IGetCategoryListView igetCategoryListView, int cid, int currentPage, boolean isLoadMore) {
        GET_CATEGORY_LIST_URL = "https://www.wanandroid.com/project/list/" + currentPage + "/json?cid=" + cid;
        funspartApi.getCategoryList(GET_CATEGORY_LIST_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categoryListDetail -> {
                    disPlayAndroidCategoryList(igetCategoryListView, categoryListDetail, isLoadMore);
                }, this::loadError);

    }

    private void disPlayAndroidCategoryList(IGetCategoryListView igetCategoryListView, CategoryListDetail categoryListDetail, boolean isLoadMore) {
        igetCategoryListView.getCategoryListSuccess(categoryListDetail, isLoadMore);
    }

    private void loadError(Throwable throwable) {
        throwable.printStackTrace();
        Toast.makeText(mContext, "网络不见了", Toast.LENGTH_SHORT).show();
    }
}
