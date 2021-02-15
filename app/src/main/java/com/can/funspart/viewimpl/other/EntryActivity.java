package com.can.funspart.viewimpl.other;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.can.funspart.R;
import com.can.funspart.base.BaseActivity;
import com.can.funspart.viewimpl.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EntryActivity extends BaseActivity {

    @BindView(R.id.iv_entry)
    ImageView mSplashImage;

    private static final int ANIMATION_TIME = 2000;

    private static final float SCALE_END = 1.13F;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatus();
        setContentView(R.layout.activity_entry);
        ButterKnife.bind(this);
        startAnim();
    }

    private void startAnim() {

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(mSplashImage, "scaleX", 1f, SCALE_END);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(mSplashImage, "scaleY", 1f, SCALE_END);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(ANIMATION_TIME).play(animatorX).with(animatorY);
        set.start();
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startActivity(new Intent(EntryActivity.this, MainActivity.class));
                EntryActivity.this.finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//                实现两个 Activity 切换时的动画。在Activity中使用
//                有两个参数：进入动画和出去的动画。
//
//                注意
//                1、必须在 StartActivity()  或 finish() 之后立即调用。
                //实现淡入浅出的效果
//                startActivity(new Intent(EntryActivity.this, MainActivity.class));
//                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

// 由左向右滑入的效果
//                startActivity(new Intent(EntryActivity.this, MainActivity.class));
//                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);

// 实现zoommin 和 zoomout (自;定义的动画)即类似iphone的进入和退出时的效果
//                startActivity(new Intent(EntryActivity.this, MainActivity.class));
//                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
    }
}


