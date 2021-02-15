package com.can.funspart.viewimpl.film.video.player;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.can.funspart.R;
import com.can.funspart.bean.film.FilmMediaItem;
import com.can.funspart.bean.film.FilmsPlayResponse;
import com.can.funspart.okhttp.http.OkHttpClientManager;
import com.can.funspart.utils.GsonUtil;
import com.can.funspart.utils.Utils;
import com.dueeeke.videoplayer.listener.VideoListener;
import com.dueeeke.videoplayer.player.IjkVideoView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Request;

import static com.dueeeke.videoplayer.player.IjkVideoView.SCREEN_SCALE_4_3;
import static com.dueeeke.videoplayer.player.IjkVideoView.SCREEN_SCALE_DEFAULT;

/**
 * author：Wcan
 * create on 2019/9/28 11:42
 * 作用：系统播放器
 */
public class SystemVideoActivity extends Activity implements View.OnClickListener {

    private boolean isUseSystem = true;
    /**
     * 视频进度的更新
     */
    private static final int PROGRESS = 1;
    /**
     * 隐藏控制面板
     */
    private static final int HIDE_MEDIA_CONTROLLER = 2;
    /**
     * 显示网络速度
     */
    private static final int SHOW_SPEED = 3;

    private Uri mUri;

    @BindView(R.id.ijk_video_view)
    IjkVideoView mIjkVideoView;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_system_time)
    TextView mTvSystemTime;
    @BindView(R.id.btn_voice)
    Button mBtnVoice;
    @BindView(R.id.seekbar_voice)
    SeekBar mSeekBarVoice;
    @BindView(R.id.btn_swich_player)
    Button mBtnSwitchPlayer;
    @BindView(R.id.media_controller)
    RelativeLayout mMediaController;
    @BindView(R.id.tv_current_time)
    TextView mTvCurrentTime;
    @BindView(R.id.seekbar_video)
    SeekBar mSeekBarVideo;
    @BindView(R.id.tv_duration)
    TextView mTvDuration;
    @BindView(R.id.btn_exit)
    Button mBtnExit;
    @BindView(R.id.btn_video_pre)
    Button mBtnVideoPre;
    @BindView(R.id.btn_video_start_pause)
    Button mBtnVideoStartOrPause;
    @BindView(R.id.btn_video_next)
    Button mBtnVideoNext;
    @BindView(R.id.btn_video_switch_screen)
    Button mBtnVideoSwitchScreen;
    @BindView(R.id.tv_buffer_net_speed)
    TextView mBufferNetSpeed;
    @BindView(R.id.ll_buffer)
    LinearLayout mLlBuffer;
    @BindView(R.id.loading_net_speed)
    TextView mLoadingNetSpeed;
    @BindView(R.id.ll_loading)
    LinearLayout ll_loading;

    private Utils utils;
    /**
     * 传入进来的视频列表
     */
    private ArrayList<FilmMediaItem> mMediaItems;
    /**
     * 要播放的列表中的具体位置
     */
    private int mPosition;

    /**
     * 1.定义手势识别器
     */
    private GestureDetector gestureDetector;

    /**
     * 是否显示控制面板
     */
    private boolean mIsShowMediaController = false;
    /**
     * 是否全屏
     */
    private boolean mIsFullScreen = false;

    /**
     * 屏幕的宽
     */
    private int screenWidth = 0;

    /**
     * 屏幕的高
     */
    private int screenHeight = 0;

    /**
     * 调用声音
     */
    private AudioManager am;

    /**
     * 当前的音量
     */
    private int currentVoice;

    /**
     * 0~15
     * 最大音量
     */
    private int maxVoice;
    /**
     * 是否是静音
     */
    private boolean mIsMute = false;
    /**
     * 是否是网络uri
     */
    private boolean mIsNetUri;
    /**
     * 上一次的播放进度
     */
    private int mLastPlayPosition;
    /**
     * 接收外来的播放url
     */
    private String mPlayUrl;
    /**
     * 接收外来的媒资标题
     */
    private String mMovieName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initViews();
        setListener();
        getData(getIntent());
        setData();
    }

    private void initData() {
        utils = new Utils();

        //2.实例化手势识别器，并且重写双击，点击，长按
        gestureDetector = new GestureDetector(this, new MySimpleOnGestureListener());
        //得到屏幕的宽和高
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        //得到音量
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

    }

    private void initViews() {
        setContentView(R.layout.activity_system_video);
        ButterKnife.bind(this);

        mBtnVoice.setOnClickListener(this);
        mBtnSwitchPlayer.setOnClickListener(this);
        mBtnExit.setOnClickListener(this);
        mBtnVideoPre.setOnClickListener(this);
        mBtnVideoStartOrPause.setOnClickListener(this);
        mBtnVideoNext.setOnClickListener(this);
        mBtnVideoSwitchScreen.setOnClickListener(this);

        //最大音量和SeekBar关联
        mSeekBarVoice.setMax(maxVoice);
        //设置当前进度-当前音量
        mSeekBarVoice.setProgress(currentVoice);

        //开始更新网络速度
        handler.sendEmptyMessage(SHOW_SPEED);
    }

    private void getData(Intent intent) {
        //得到播放地址
        mUri = intent.getData();//文件夹，图片浏览器，QQ空间
        mMediaItems = (ArrayList<FilmMediaItem>) getIntent().getSerializableExtra("videolist");
        mPosition = intent.getIntExtra("position", 0);

        mPlayUrl = intent.getStringExtra("playUrl");
        mMovieName = intent.getStringExtra("movie_name");
    }

    private void setData() {
        if (mMediaItems != null && mMediaItems.size() > 0) {
            FilmMediaItem mediaItem = mMediaItems.get(mPosition);
            if (mediaItem.getMovieId() == 0) {
                Toast.makeText(SystemVideoActivity.this, "数据传递异常", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            String url = "http://front-gateway.mtime.com/video/play_url?video_id=" + mediaItem.getMovieId() + "&source=1&scheme=http";
            OkHttpClientManager.getAsyn(url, new OkHttpClientManager.StringCallback() {

                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(String response) {
                    FilmsPlayResponse playResponse = (FilmsPlayResponse) GsonUtil.JSONToObject(response, FilmsPlayResponse.class);
                    mediaItem.setHightUrl(playResponse.getData().get(0).url);
                    mTvName.setText(mediaItem.getMovieName());
                    mIsNetUri = utils.isNetUri(mediaItem.getHightUrl());
                    mIjkVideoView.release();
                    mIjkVideoView.setUrl(mediaItem.getHightUrl());
//                    mIjkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_DEFAULT);
                    mIjkVideoView.start();
                }
            });
        } else if (mUri != null) {
            mTvName.setText(mUri.toString());//设置视频的名称
            mIsNetUri = utils.isNetUri(mUri.toString());
            mIjkVideoView.setUrl(mUri.getPath());
//            mIjkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_DEFAULT);
            mIjkVideoView.start();
        } else if (mPlayUrl != null) {
            mTvName.setText(mMovieName);//设置视频的名称
            mIsNetUri = utils.isNetUri(mPlayUrl);
            mIjkVideoView.setUrl(mPlayUrl);
//            mIjkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_DEFAULT);
            mIjkVideoView.start();
        } else {
            Toast.makeText(SystemVideoActivity.this, "数据传递异常", Toast.LENGTH_SHORT).show();
            finish();
        }
        setButtonState();
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnVoice) {
            mIsMute = !mIsMute;
            // Handle clicks for mBtnVoice
            updataVoice(currentVoice, mIsMute);
        } else if (v == mBtnSwitchPlayer) {
            // Handle clicks for mBtnSwitchPlayer
        } else if (v == mBtnExit) {
            // Handle clicks for mBtnExit
            finish();
        } else if (v == mBtnVideoPre) {
            // Handle clicks for mBtnVideoPre
            playPreVideo();
        } else if (v == mBtnVideoStartOrPause) {
            // Handle clicks for mBtnVideoStartOrPause
            startAndPause();
        } else if (v == mBtnVideoNext) {
            // Handle clicks for mBtnVideoNext
            playNextVideo();
        } else if (v == mBtnVideoSwitchScreen) {
            // Handle clicks for mBtnVideoSwitchScreen
            setFullScreenAndDefault();
        }

        handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
    }

    private void startAndPause() {
        if (mIjkVideoView.isPlaying()) {
            //视频在播放-设置暂停
            mIjkVideoView.pause();
            //按钮状态设置播放
            mBtnVideoStartOrPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        } else {
            //视频播放
            mIjkVideoView.start();
            //按钮状态设置暂停
            mBtnVideoStartOrPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    /**
     * 播放上一个视频
     */
    private void playPreVideo() {
        if (mMediaItems != null && mMediaItems.size() > 0) {
            //播放上一个视频
            mPosition--;
            if (mPosition >= 0) {
                ll_loading.setVisibility(View.VISIBLE);
                FilmMediaItem mediaItem = mMediaItems.get(mPosition);
                String url = "http://front-gateway.mtime.com/video/play_url?video_id=" + mediaItem.getMovieId() + "&source=1&scheme=http";
                OkHttpClientManager.getAsyn(url, new OkHttpClientManager.StringCallback() {

                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        FilmsPlayResponse playResponse = (FilmsPlayResponse) GsonUtil.JSONToObject(response, FilmsPlayResponse.class);
                        mediaItem.setHightUrl(playResponse.getData().get(1).url);
                        mTvName.setText(mediaItem.getMovieName());
                        mIsNetUri = utils.isNetUri(mediaItem.getHightUrl());
                        mIjkVideoView.release();
                        mIjkVideoView.setUrl(mediaItem.getHightUrl());
//                        mIjkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_DEFAULT);
                        mIjkVideoView.start();
                        //设置按钮状态
                        setButtonState();
                    }
                });
            }
        } else if (mUri != null || mPlayUrl != null) {
            //设置按钮状态-上一个和下一个按钮设置灰色并且不可以点击
            setButtonState();
        }
    }

    /**
     * 播放下一个视频
     */
    private void playNextVideo() {
        if (mMediaItems != null && mMediaItems.size() > 0) {
            //播放下一个
            mPosition++;
            if (mPosition < mMediaItems.size()) {
                ll_loading.setVisibility(View.VISIBLE);
                FilmMediaItem mediaItem = mMediaItems.get(mPosition);
                String url = "http://front-gateway.mtime.com/video/play_url?video_id=" + mediaItem.getMovieId() + "&source=1&scheme=http";
                OkHttpClientManager.getAsyn(url, new OkHttpClientManager.StringCallback() {

                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        FilmsPlayResponse playResponse = (FilmsPlayResponse) GsonUtil.JSONToObject(response, FilmsPlayResponse.class);
                        mediaItem.setHightUrl(playResponse.getData().get(1).url);
                        mTvName.setText(mediaItem.getMovieName());
                        mIsNetUri = utils.isNetUri(mediaItem.getHightUrl());
                        mIjkVideoView.release();
                        mIjkVideoView.setUrl(mediaItem.getHightUrl());
//                        mIjkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_DEFAULT);
                        mIjkVideoView.start();
                        //设置按钮状态
                        setButtonState();
                    }
                });
            }
        } else if (mUri != null || mPlayUrl != null) {
            //设置按钮状态-上一个和下一个按钮设置灰色并且不可以点击
            setButtonState();
        }

    }

    private void setButtonState() {
        if (mMediaItems != null && mMediaItems.size() > 0) {
            if (mMediaItems.size() == 1) {
                setEnable(false);
            } else if (mMediaItems.size() == 2) {
                if (mPosition == 0) {
                    mBtnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    mBtnVideoPre.setEnabled(false);

                    mBtnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    mBtnVideoNext.setEnabled(true);

                } else if (mPosition == mMediaItems.size() - 1) {
                    mBtnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    mBtnVideoNext.setEnabled(false);

                    mBtnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    mBtnVideoPre.setEnabled(true);

                }
            } else {
                if (mPosition == 0) {
                    mBtnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    mBtnVideoPre.setEnabled(false);
                } else if (mPosition == mMediaItems.size() - 1) {
                    mBtnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    mBtnVideoNext.setEnabled(false);
                } else {
                    setEnable(true);
                }
            }
        } else if (mUri != null || mPlayUrl != null) {
            setEnable(false);
        }
    }

    private void setEnable(boolean isEnable) {
        if (isEnable) {
            mBtnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
            mBtnVideoPre.setEnabled(true);
            mBtnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
            mBtnVideoNext.setEnabled(true);
        } else {
            //两个按钮设置灰色
            mBtnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            mBtnVideoPre.setEnabled(false);
            mBtnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            mBtnVideoNext.setEnabled(false);
        }

    }


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_SPEED://显示网速
                    //1.得到网络速度
                    String netSpeed = utils.getNetSpeed(SystemVideoActivity.this);

                    //显示网络速
                    mLoadingNetSpeed.setText("玩命加载中..." + netSpeed);
                    mBufferNetSpeed.setText("缓存中..." + netSpeed);

                    //2.每两秒更新一次
                    handler.removeMessages(SHOW_SPEED);
                    handler.sendEmptyMessageDelayed(SHOW_SPEED, 2000);

                    break;
                case HIDE_MEDIA_CONTROLLER://隐藏控制面板
                    hideMediaController();
                    break;
                case PROGRESS:

                    //1.得到当前的视频播放进程
                    int currentPosition = mIjkVideoView.getCurrentPosition();//0

                    //2.SeekBar.setProgress(当前进度);
                    mSeekBarVideo.setProgress(currentPosition);


                    //更新文本播放进度
                    mTvCurrentTime.setText(utils.stringForTime(currentPosition));


                    //设置系统时间
                    mTvSystemTime.setText(getSystemTime());

                    //缓存进度的更新
                    if (mIsNetUri) {
                        //只有网络资源才有缓存效果
                        int buffer = mIjkVideoView.getBufferPercentage();//0~100
                        int totalBuffer = buffer * mSeekBarVideo.getMax();
                        int secondaryProgress = totalBuffer / 100;
                        mSeekBarVideo.setSecondaryProgress(secondaryProgress);
                    } else {
                        //本地视频没有缓冲效果
                        mSeekBarVideo.setSecondaryProgress(0);
                    }

                    //监听卡
                    if (!isUseSystem) {

                        if (mIjkVideoView.isPlaying()) {
                            int buffer = currentPosition - mLastPlayPosition;
                            if (buffer < 500) {
                                //视频卡了
                                mLlBuffer.setVisibility(View.VISIBLE);
                            } else {
                                //视频不卡了
                                mLlBuffer.setVisibility(View.GONE);
                            }
                        } else {
                            mLlBuffer.setVisibility(View.GONE);
                        }
                    }

                    mLastPlayPosition = currentPosition;

                    //3.每秒更新一次
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);

                    break;
            }
        }
    };

    /**
     * 得到系统时间
     *
     * @return
     */
    public String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
//                Toast.makeText(SystemVideoActivity.this, "我被长按了", Toast.LENGTH_SHORT).show();
            startAndPause();
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
//                Toast.makeText(SystemVideoActivity.this, "我被双击了", Toast.LENGTH_SHORT).show();
            setFullScreenAndDefault();
            return super.onDoubleTap(e);

        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
//                Toast.makeText(SystemVideoActivity.this, "我被单击了", Toast.LENGTH_SHORT).show();
            if (mIsShowMediaController) {
                //隐藏
                hideMediaController();
                //把隐藏消息移除
                handler.removeMessages(HIDE_MEDIA_CONTROLLER);

            } else {
                //显示
                showMediaController();
                //发消息隐藏
                handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
            }

            return super.onSingleTapConfirmed(e);
        }
    }

    private void setFullScreenAndDefault() {
        if (mIsFullScreen) {
            //默认
            setVideoType(SCREEN_SCALE_4_3);
        } else {
            //全屏
            setVideoType(SCREEN_SCALE_DEFAULT);
        }
    }

    private void setVideoType(int defaultScreen) {
        switch (defaultScreen) {
            case SCREEN_SCALE_DEFAULT://全屏
                //1.设置视频画面的大小-屏幕有多大就是多大
                mIjkVideoView.setScreenScale(SCREEN_SCALE_DEFAULT);
                //2.设置按钮的状态-默认
                mBtnVideoSwitchScreen.setBackgroundResource(R.drawable.btn_video_switch_screen_default_selector);
                mIsFullScreen = true;
                break;
            case SCREEN_SCALE_4_3://默认
                mIjkVideoView.setScreenScale(SCREEN_SCALE_4_3);
                //2.设置按钮的状态--全屏
                mBtnVideoSwitchScreen.setBackgroundResource(R.drawable.btn_video_switch_screen_full_selector);
                mIsFullScreen = false;
                break;
        }
    }

    private void setListener() {
        mIjkVideoView.setVideoListener(new VideoListener() {
            @Override
            public void onComplete() {
                playNextVideo();
            }

            @Override
            public void onPrepared() {
                //1.视频的总时长，关联总长度
                int duration = mIjkVideoView.getDuration();
                mSeekBarVideo.setMax(duration);
                mTvDuration.setText(utils.stringForTime(duration));

                hideMediaController();//默认是隐藏控制面板
                //2.发消息
                handler.sendEmptyMessage(PROGRESS);

                //屏幕的默认播放
                setVideoType(SCREEN_SCALE_DEFAULT);

                //把加载页面消失掉
                ll_loading.setVisibility(View.GONE);
                //按钮状态设置暂停
                mBtnVideoStartOrPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
//            mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//                @Override
//                public void onSeekComplete(MediaPlayer mp) {
//                    Toast.makeText(SystemVideoActivity.this, "拖动完成", Toast.LENGTH_SHORT).show();
//                }
//            });

            }

            @Override
            public void onError() {
                Toast.makeText(SystemVideoActivity.this, "播放出错了哦", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInfo(int what, int extra) {
                switch (what) {
                    //监听视频播放卡-系统的api
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START://视频卡了，拖动卡
//                    Toast.makeText(SystemVideoActivity.this, "卡了", Toast.LENGTH_SHORT).show();
                        mLlBuffer.setVisibility(View.VISIBLE);
                        break;

                    case MediaPlayer.MEDIA_INFO_BUFFERING_END://视频卡结束了，拖动卡结束了
//                    Toast.makeText(SystemVideoActivity.this, "卡结束了", Toast.LENGTH_SHORT).show();
                        mLlBuffer.setVisibility(View.GONE);
                        break;
                }
            }
        });

        //设置SeeKbar状态变化的监听
        mSeekBarVideo.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());

        mSeekBarVoice.setOnSeekBarChangeListener(new VoiceOnSeekBarChangeListener());

    }

    class VoiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                if (progress > 0) {
                    mIsMute = false;
                } else {
                    mIsMute = true;
                }
                updataVoice(progress, mIsMute);
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
        }
    }

    /**
     * 设置音量的大小
     *
     * @param progress
     */
    private void updataVoice(int progress, boolean isMute) {
        if (isMute) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            mSeekBarVoice.setProgress(0);
        } else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            mSeekBarVoice.setProgress(progress);
            currentVoice = progress;
        }
    }

    class VideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        /**
         * 当手指滑动的时候，会引起SeekBar进度变化，会回调这个方法
         *
         * @param seekBar
         * @param progress
         * @param fromUser 如果是用户引起的true,不是用户引起的false
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mIjkVideoView.seekTo(progress);
            }

        }

        /**
         * 当手指触碰的时候回调这个方法
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        }

        /**
         * 当手指离开的时候回调这个方法
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIjkVideoView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIjkVideoView.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mIjkVideoView.release();
        //移除所有的消息
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private float startY;
    private float startX;
    /**
     * 屏幕的高
     */
    private float touchRang;

    /**
     * 当一按下的音量
     */
    private int mVol;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //3.把事件传递给手势识别器
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://手指按下
                //1.按下记录值
                startY = event.getY();
                startX = event.getX();
                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchRang = Math.min(screenHeight, screenWidth);//screenHeight
                handler.removeMessages(HIDE_MEDIA_CONTROLLER);

                break;
            case MotionEvent.ACTION_MOVE://手指移动
                //2.移动的记录相关值
                float endY = event.getY();
                float endX = event.getX();
                float distanceY = startY - endY;

                if (endX < screenWidth / 2) {
                    //左边屏幕-调节亮度
                    final double FLING_MIN_DISTANCE = 0.5;
                    final double FLING_MIN_VELOCITY = 0.5;
                    if (distanceY > FLING_MIN_DISTANCE
                            && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
//                        Log.e(TAG, "up");
                        setBrightness(20);
                    }
                    if (distanceY < FLING_MIN_DISTANCE
                            && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
//                        Log.e(TAG, "down");
                        setBrightness(-20);
                    }
                } else {
                    //右边屏幕-调节声音
                    //改变声音 = （滑动屏幕的距离： 总距离）*音量最大值
                    float delta = (distanceY / touchRang) * maxVoice;
                    //最终声音 = 原来的 + 改变声音；
                    int voice = (int) Math.min(Math.max(mVol + delta, 0), maxVoice);
                    if (delta != 0) {
                        mIsMute = false;
                        updataVoice(voice, mIsMute);
                    }

                }


//                startY = event.getY();//不要加
                break;
            case MotionEvent.ACTION_UP://手指离开
                handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
                break;
        }
        return super.onTouchEvent(event);
    }

    private Vibrator vibrator;

    /*
     *
     * 设置屏幕亮度 lp = 0 全暗 ，lp= -1,根据系统设置， lp = 1; 最亮
     */
    public void setBrightness(float brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        // if (lp.screenBrightness <= 0.1) {
        // return;
        // }
        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern = {10, 200}; // OFF/ON/OFF/ON...
            vibrator.vibrate(pattern, -1);
        } else if (lp.screenBrightness < 0.2) {
            lp.screenBrightness = (float) 0.2;
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern = {10, 200}; // OFF/ON/OFF/ON...
            vibrator.vibrate(pattern, -1);
        }
//        Log.e(TAG, "lp.screenBrightness= " + lp.screenBrightness);
        getWindow().setAttributes(lp);
    }


    /**
     * 显示控制面板
     */
    private void showMediaController() {
        mMediaController.setVisibility(View.VISIBLE);
        mIsShowMediaController = true;
    }


    /**
     * 隐藏控制面板
     */
    private void hideMediaController() {
        mMediaController.setVisibility(View.GONE);
        mIsShowMediaController = false;
    }

    /**
     * 监听物理健，实现声音的调节大小
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            currentVoice--;
            updataVoice(currentVoice, false);
            showMediaController();
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 2000);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            currentVoice++;
            updataVoice(currentVoice, false);
            showMediaController();
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 2000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
