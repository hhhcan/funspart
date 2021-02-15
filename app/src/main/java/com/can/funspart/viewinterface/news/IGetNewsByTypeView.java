package com.can.funspart.viewinterface.news;

import com.can.funspart.base.IBaseView;
import com.can.funspart.bean.news.NewsSummary;

import java.util.List;
import java.util.Map;

/**
 * Created by forezp on 16/9/30.
 */
public interface IGetNewsByTypeView extends IBaseView{

    void getNewsByTypeSuccess(Map<String, List<NewsSummary>> newsRoot, boolean isLoadMore);
}
