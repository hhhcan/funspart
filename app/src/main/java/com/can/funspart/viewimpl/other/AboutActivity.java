package com.can.funspart.viewimpl.other;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.can.funspart.R;
import com.can.funspart.base.BaseActivity;
import com.can.funspart.utils.ThemeUtils;
import com.jaeger.library.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AboutActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTranslucentStatus();
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
//        applyKitKatTranslucency();
        StatusBarUtil.setColorNoTranslucent(this,ThemeUtils.getThemeColor());
        toolbar.setTitle("关于");
        toolbar.setBackgroundColor(ThemeUtils.getThemeColor());
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backThActivity();
            }
        });
    }

}
