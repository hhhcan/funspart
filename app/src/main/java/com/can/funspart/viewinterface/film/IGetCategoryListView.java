package com.can.funspart.viewinterface.film;

import com.can.funspart.base.IBaseView;
import com.can.funspart.bean.book.CategoryListDetail;

/**
 * Created by forezp on 16/9/21.
 */
public interface IGetCategoryListView extends IBaseView {

    void getCategoryListSuccess(CategoryListDetail categoryListDetail, boolean isLoadMore);

    /**
     * 网络请求失败
     */
    void getDataFail();

}
