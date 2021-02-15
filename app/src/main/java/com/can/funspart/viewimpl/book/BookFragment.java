package com.can.funspart.viewimpl.book;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.can.funspart.R;
import com.can.funspart.adapter.BookViewpagerAdapter;
import com.can.funspart.base.BaseFragment;
import com.can.funspart.bean.book.AndroidCategory;
import com.can.funspart.utils.ThemeUtils;
import com.google.gson.Gson;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class BookFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    //获取wanAndroid 的栏目树
    public static final String GET_CATEGORY_URL = "https://www.wanandroid.com/project/tree/json";
    public static AndroidCategory androidCategory;
    OkHttpClient client = new OkHttpClient();

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

    private BookViewpagerAdapter mViewPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        ButterKnife.bind(this, view);
        /**
         * 请求Android模块的栏目树
         */
        requestCategoryTree();
        return view;
    }


    public static BookFragment newInstance() {

        Bundle args = new Bundle();
        BookFragment fragment = new BookFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void requestCategoryTree() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder().url(GET_CATEGORY_URL).build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Gson gson = new Gson();
                        androidCategory = gson.fromJson(response.body().string(), AndroidCategory.class);
                        if (androidCategory != null) {
                            mTitles = new String[androidCategory.getData().size()];
                            for (int i = 0; i < androidCategory.getData().size(); i++) {//内部不锁定，效率最高，但在多线程要考虑并发操作的问题。
                                mTitles[i] = androidCategory.getData().get(i).getName();
                                Log.i("BookFragment", "requestCategoryTree" + mTitles[i]);
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initViews();
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initViews() {
        // 初始化ViewPager的适配器，并设置给它
        mViewPagerAdapter = new BookViewpagerAdapter(getChildFragmentManager(), mTitles);
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
        viewpager.setOffscreenPageLimit(5);

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


    @Override
    public void onDetach() {
        super.onDetach();

    }
}
