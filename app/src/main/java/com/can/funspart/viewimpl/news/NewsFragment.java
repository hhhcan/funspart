package com.can.funspart.viewimpl.news;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.can.funspart.R;
import com.can.funspart.adapter.NewsViewpagerAdapter;
import com.can.funspart.api.ApiConstants;
import com.can.funspart.base.BaseFragment;
import com.can.funspart.bean.news.NewsChannelTable;
import com.can.funspart.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NewsFragment extends BaseFragment implements ViewPager.OnPageChangeListener {


    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.appbarlayout)
    AppBarLayout appbarlayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.coordinatorlayout)
    CoordinatorLayout coordinatorlayout;
    // TabLayout中的tab标题
    private String[] mTitles;

    private NewsViewpagerAdapter mViewPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public static NewsFragment newInstance() {

        Bundle args = new Bundle();
        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /**
         * 请求新闻模块的栏目树
         */
        initNewsTree();
        initViews();
    }

    public static ArrayList<NewsChannelTable> newsChannelTables = new ArrayList<>();

    private void initNewsTree() {
        List<String> channelName = Arrays.asList(getResources().getStringArray(R.array.news_channel_name));
        List<String> channelId = Arrays.asList(getResources().getStringArray(R.array.news_channel_id));
        mTitles = new String[channelName.size()];
        for (int i = 0; i < channelName.size(); i++) {
            NewsChannelTable entity = new NewsChannelTable(channelName.get(i), channelId.get(i)
                    , ApiConstants.getType(channelId.get(i)));
            newsChannelTables.add(entity);
            mTitles[i] = entity.getNewsChannelName();
        }
    }

    private void initViews() {
        // 初始化ViewPager的适配器，并设置给它
        mViewPagerAdapter = new NewsViewpagerAdapter(getChildFragmentManager(), mTitles);
        viewpager.setAdapter(mViewPagerAdapter);
        // 设置ViewPager最大缓存的页面个数
        viewpager.setOffscreenPageLimit(4);
        // 给ViewPager添加页面动态监听器（为了让Toolbar中的Title可以变化相应的Tab的标题）
        viewpager.addOnPageChangeListener(this);

        tablayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tablayout.setSelectedTabIndicatorColor(ThemeUtils.getThemeColor());
        tablayout.setTabTextColors(getResources().getColor(R.color.text_gray_6), ThemeUtils.getThemeColor());
        // 将TabLayout和ViewPager进行关联，让两者联动起来
        tablayout.setupWithViewPager(viewpager);
        // 设置Tablayout的Tab显示ViewPager的适配器中的getPageTitle函数获取到的标题
        tablayout.setTabsFromPagerAdapter(mViewPagerAdapter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
