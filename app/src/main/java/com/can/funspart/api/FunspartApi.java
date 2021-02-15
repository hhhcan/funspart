package com.can.funspart.api;

import com.can.funspart.bean.book.CategoryListDetail;
import com.can.funspart.bean.film.FilmsResponse;
import com.can.funspart.bean.news.NewsSummary;

import java.util.List;
import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Url;
import rx.Observable;

public interface FunspartApi {

    @GET()
    Observable<FilmsResponse> getFilmMediaItems(@Url String url);

    @GET()
    Observable<FilmsResponse> getFilmRanking(@Url String url);

    /**
     * 某一个分类下项目列表数据，分页展示
     * eg:
     * https://www.wanandroid.com/project/list/1/json?cid=294
     * <p>
     * 方法：GET
     * 参数：
     * cid 分类的id，上面项目分类接口
     * 页码：拼接在链接中，从1开始。
     */
    @GET()
    Observable<CategoryListDetail> getCategoryList(@Url String url);

    @GET("nc/article/{type}/{id}/{startPage}-20.html")
    Observable<Map<String, List<NewsSummary>>> getNewsList(
            @Header("Cache-Control") String cacheControl,
            @Path("type") String type, @Path("id") String id,
            @Path("startPage") int startPage);
}
