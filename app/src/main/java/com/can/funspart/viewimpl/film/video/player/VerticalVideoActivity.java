package com.can.funspart.viewimpl.film.video.player;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.bumptech.glide.Glide;
import com.can.funspart.MyApp;
import com.can.funspart.Presenter.FilmPresenter;
import com.can.funspart.R;
import com.can.funspart.adapter.VerticalVideoAdapter;
import com.can.funspart.base.BaseActivity;
import com.can.funspart.bean.film.BaseVideoData;
import com.can.funspart.bean.film.VideoListData;
import com.can.funspart.bean.film.event.RefreshEvent;
import com.can.funspart.okhttp.http.OkHttpClientManager;
import com.can.funspart.utils.GlideUtils;
import com.can.funspart.utils.ToastUtil;
import com.can.funspart.utils.Utils;
import com.can.funspart.utils.WeakDataHolder;
import com.can.funspart.view.CircleImageView;
import com.can.funspart.view.TextImageView;
import com.can.funspart.view.VerticalViewPager;
import com.can.funspart.widget.VerticalVideoController;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.PlayerConfig;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

/**
 * author：Wcan
 * Ijk 竖屏播放
 */
public class VerticalVideoActivity extends BaseActivity {

    @BindView(R.id.verticalviewpager)
    VerticalViewPager mVerticalViewpager;

    private List<BaseVideoData> mList = new ArrayList<>();

    private int mCurrentItem;

    private IjkVideoView mIjkVideoView;
    private VerticalVideoController mVideoController;
    private VerticalVideoAdapter mVideoAdapter;
    private List<View> mViews = new ArrayList<>();

    private TextView mTvVideoTitle;

    private CircleImageView mIvUserAvatar;
    private TextView mTvUsername;
    private TextImageView mTvLikeCount;
    private TextImageView mTvPlayCount;

    ImageView mCover;

    private int mPlayingPosition;
    private int position;

    private int maxListCount = 20;

    @OnClick(R.id.iv_back)
    void back() {
        onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatus(); //透明状态栏
        setContentView(R.layout.activity_vertical_video);
        ButterKnife.bind(this);
        initView();
    }

    protected void initView() {
//        mList = getIntent().getParcelableArrayListExtra("videoUrlList");
        mList = (List<BaseVideoData>) WeakDataHolder.getInstance().getData("videoUrlList");
        position = getIntent().getIntExtra("position", -1);
        mCurrentItem = position;

        mIjkVideoView = new IjkVideoView(this);
        PlayerConfig config = new PlayerConfig.Builder().setLooping().build();
        mIjkVideoView.setPlayerConfig(config);
        mVideoController = new VerticalVideoController(this);
        mIjkVideoView.setVideoController(mVideoController);


        getImageData();

    }

    private void startPlay() {
        View view = mViews.get(mCurrentItem);
        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.container);
        mCover = (ImageView) view.findViewById(R.id.cover_img);

        mVideoController.setSelect(false);

        if (mCover != null && mCover.getDrawable() != null) {
            mVideoController.getThumb().setImageDrawable(mCover.getDrawable());
        }

        ViewGroup parent = (ViewGroup) mIjkVideoView.getParent();

        if (parent != null) {
            parent.removeAllViews();
        }

        frameLayout.addView(mIjkVideoView);
        mIjkVideoView.setUrl(mList.get(mCurrentItem).getVideoPlayUrl());
        mIjkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_DEFAULT);
        mIjkVideoView.start();

        mPlayingPosition = mCurrentItem;
    }


    @Override
    public void onPause() {
        super.onPause();
        mIjkVideoView.pause();
        if (mVideoController != null) {
            mVideoController.getIvPlay().setVisibility(View.GONE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mIjkVideoView.resume();

        if (mVideoController != null) {
            mVideoController.setSelect(false);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIjkVideoView.release();
    }


    public void getImageData() {

        for (BaseVideoData item : mList) {
            View view = LayoutInflater.from(this).inflate(R.layout.view_video_item, null);
            mCover = (ImageView) view.findViewById(R.id.cover_img);

            mIvUserAvatar = (CircleImageView) view.findViewById(R.id.iv_user_avatar);
            mTvUsername = (TextView) view.findViewById(R.id.tv_username);
            mTvLikeCount = (TextImageView) view.findViewById(R.id.tv_like_count);
            mTvPlayCount = (TextImageView) view.findViewById(R.id.tv_play_count);
            mTvVideoTitle = (TextView) view.findViewById(R.id.tv_video_title);

            Glide.with(MyApp.getInstance()).load(item.getCoverImgUrl()).dontAnimate().into(mCover);

            GlideUtils.loadImage(MyApp.getInstance(), item.getAuthorImgUrl(), mIvUserAvatar, null);

            mTvVideoTitle.setText(item.getTitle());

            mTvUsername.setText(item.getAuthorName());

            mTvPlayCount.setText(Utils.formatNumber(item.getPlayCount()) + "播放");

            mTvLikeCount.setText(Utils.formatNumber(item.getLikeCount()) + "赞");

            mViews.add(view);
        }

        mVideoAdapter = new VerticalVideoAdapter(mViews);
        mVerticalViewpager.setAdapter(mVideoAdapter);


        if (position != -1) {
            mVerticalViewpager.setCurrentItem(position);
        }


        mVerticalViewpager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {


            @Override
            public void onPageSelected(int position) {
                mCurrentItem = position;
                mIjkVideoView.pause();


                if (mCurrentItem == mList.size() - 1) {
                    ToastUtil.showToast("加载中，请稍后");
                    getShortVideoData();
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

                if (mPlayingPosition == mCurrentItem) return;
                if (state == VerticalViewPager.SCROLL_STATE_IDLE) {
                    mIjkVideoView.release();
                    ViewParent parent = mIjkVideoView.getParent();
                    if (parent != null && parent instanceof FrameLayout) {
                        ((FrameLayout) parent).removeView(mIjkVideoView);
                    }
                    startPlay();
                }

            }

        });


        mVerticalViewpager.post(new Runnable() {
            @Override
            public void run() {
                startPlay();
            }
        });


    }


    /**
     * 下拉数据规律：min_cursor=max_cursor=0
     * 上拉数据规律：
     * 第二次请求取第一次请求返回的json数据中的min_cursor字段，max_cursor不需要携带。
     * 第三次以及后面所有的请求都只带max_cursor字段，值为第一次请求返回的json数据中的max_cursor字段
     */
    public void getShortVideoData() {
        String url = FilmPresenter.FILM_URL;
        OkHttpClientManager.getAsyn(url, new OkHttpClientManager.StringCallback() {
            @Override
            public void onResponse(String response) {
                LogUtils.json(response);
                try {
                    VideoListData listData = VideoListData.fromJSONData(response);
                    if (listData.getVideoDataList() == null || listData.getVideoDataList().size() == 0) {
                        return;
                    }
                    List<BaseVideoData> list = listData.getVideoDataList();
                    mList.addAll(list);
                    mViews.clear();//加载更多需要先清空原来的view

                    for (BaseVideoData item : mList) {
                        View view = LayoutInflater.from(VerticalVideoActivity.this).inflate(R.layout.view_video_item, null);
                        mCover = (ImageView) view.findViewById(R.id.cover_img);

                        mIvUserAvatar = (CircleImageView) view.findViewById(R.id.iv_user_avatar);
                        mTvUsername = (TextView) view.findViewById(R.id.tv_username);
                        mTvLikeCount = (TextImageView) view.findViewById(R.id.tv_like_count);
                        mTvPlayCount = (TextImageView) view.findViewById(R.id.tv_play_count);
                        mTvVideoTitle = (TextView) view.findViewById(R.id.tv_video_title);

                        Glide.with(MyApp.getInstance()).load(item.getCoverImgUrl()).dontAnimate().into(mCover);

                        GlideUtils.loadImage(MyApp.getInstance(), item.getAuthorImgUrl(), mIvUserAvatar, null);

                        mTvVideoTitle.setText(item.getTitle());

                        mTvUsername.setText(item.getAuthorName());

                        mTvPlayCount.setText(Utils.formatNumber(item.getPlayCount()) + "播放");

                        mTvLikeCount.setText(Utils.formatNumber(item.getLikeCount()) + "赞");

                        mViews.add(view);
                    }

                    mVideoAdapter.setmViews(mViews);
                    mVideoAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Request request, IOException e) {

                ToastUtil.showToast("网络连接失败");

            }
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EventBus.getDefault().post(new RefreshEvent(mList, mCurrentItem, 0));
    }
}