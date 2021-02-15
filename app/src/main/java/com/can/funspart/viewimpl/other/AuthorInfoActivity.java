package com.can.funspart.viewimpl.other;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.can.funspart.R;
import com.can.funspart.base.BaseActivity;
import com.can.funspart.utils.ViewUtils;
import com.can.funspart.viewimpl.webview.WebviewActivity;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthorInfoActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.rootLayout)
    CoordinatorLayout rootLayout;
    @BindView(R.id.tv_blog)
    TextView tvBlog;
    @BindString(R.string.blog)
    String blog;
    @BindString(R.string.github)
    String github;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatus();
        setContentView(R.layout.activity_author_info);
        ButterKnife.bind(this);
//        applyKitKatTranslucency(getResources().getColor(R.color.transparent));
        initToolbar();
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backThActivity();
            }
        });
        collapsingToolbarLayout.setTitle("_~~欢迎来访~~_");
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        ViewUtils.setTextViewUnderline(tvBlog,blog);
    }


    @OnClick({R.id.tv_blog})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.tv_blog:
                intent=new Intent(this, WebviewActivity.class);
                intent.putExtra(WebviewActivity.EXTRA_URL,"https://blog.csdn.net/jb_home");
                startThActivityByIntent(intent);
                break;
        }
    }
}
