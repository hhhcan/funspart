package com.can.funspart.viewimpl.film.filmdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.can.funspart.R;
import com.can.funspart.base.BaseActivity;
import com.can.funspart.bean.film.FilmsPlayResponse;
import com.can.funspart.bean.film.FilmMediaItem;
import com.can.funspart.okhttp.http.OkHttpClientManager;
import com.can.funspart.utils.DisplayImgUtis;
import com.can.funspart.utils.GsonUtil;
import com.can.funspart.utils.ThemeUtils;
import com.can.funspart.viewimpl.film.video.player.SystemVideoActivity;

import java.io.IOException;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

public class FilmDetailActivity extends BaseActivity {

    public static String EXTRA_ID = "film_item";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_film)
    ImageView ivFilm;
    @BindView(R.id.tv_rating)
    TextView tvRating;
    @BindView(R.id.tv_rating_num)
    TextView tvRatingNum;
    @BindView(R.id.tv_film_type)
    TextView tvFilmType;
    @BindView(R.id.tv_film_country)
    TextView tvFilmCountry;
    @BindView(R.id.tv_film_name)
    TextView tvFilmName;
    @BindView(R.id.tv_description)
    TextView tvDescription;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.tv_more_info)
    TextView tvMoreInfo;

    private FilmMediaItem currentMediaItem;
    private Context context;
    private String alt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_film_detail);
        ButterKnife.bind(this);
        context = this;
        applyKitKatTranslucency();
        initView();
        initData();
    }

    private void initView() {

        toolbar.setBackgroundColor(ThemeUtils.getThemeColor());
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backThActivity();
            }
        });


    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            currentMediaItem = (FilmMediaItem) intent.getSerializableExtra(EXTRA_ID);
        }
        if (currentMediaItem != null) {
            if (currentMediaItem.getCoverImg() != null) {
                DisplayImgUtis.getInstance().display(context, currentMediaItem.getCoverImg(), ivFilm);
            }
            if (!TextUtils.isEmpty(currentMediaItem.getVideoTitle())) {
                toolbar.setTitle(currentMediaItem.getVideoTitle());
            }
            tvRating.setText("评分:" + new Random().nextInt(10) +1);
            tvRatingNum.setText(currentMediaItem.getRating()+"人评分");
            tvFilmCountry.setText("中国大陆");

            if (currentMediaItem.getType() != null && currentMediaItem.getType().size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String s : currentMediaItem.getType()) {
                    stringBuilder.append(s + "/");
                }
                tvFilmType.setText(stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1));
            }
            if (currentMediaItem.getMovieName() != null) {
                tvDescription.setText(currentMediaItem.getMovieName()+"\n"+ currentMediaItem.getMovieName()+"\n"+ currentMediaItem.getMovieName());
            }

            tvFilmName.setText(currentMediaItem.getVideoTitle() + " [原名]");

        }
    }

    @OnClick({R.id.iv_film, R.id.tv_more_info})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_film:
            case R.id.tv_more_info:
                //根据传递的uri--跳转到播放
                //视频内容信息
                String url = "http://front-gateway.mtime.com/video/play_url?video_id=" + currentMediaItem.getMovieId() + "&source=1&scheme=http";
                OkHttpClientManager.getAsyn(url, new OkHttpClientManager.StringCallback() {

                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        FilmsPlayResponse playResponse = (FilmsPlayResponse) GsonUtil.JSONToObject(response, FilmsPlayResponse.class);
                        if(playResponse!=null&& playResponse.getData()!=null){
                            Intent  intent = new Intent(FilmDetailActivity.this, SystemVideoActivity.class);
                            intent.putExtra("playUrl",playResponse.getData().get(1).url);
                            intent.putExtra("movie_name", currentMediaItem.getMovieName());
                            startThActivityByIntent(intent);
                        }
                    }
                });
                break;
        }
    }
}
