package com.can.funspart.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.can.funspart.R;
import com.can.funspart.bean.news.NewsSummary;
import com.can.funspart.utils.DisplayImgUtis;
import com.can.funspart.utils.ScreenUtils;
import com.can.funspart.utils.ThemeUtils;
import com.can.funspart.viewimpl.webview.WebviewActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<NewsSummary> list;
    private int status = 1;
    public static final int LOAD_MORE = 0;
    public static final int LOAD_PULL_TO = 1;
    public static final int LOAD_NONE = 2;
    public static final int LOAD_END = 3;
    private static final int TYPE_TOP = -1;
    private static final int TYPE_FOOTER = -2;

    public NewsAdapter(Context context) {
        this.context = context;
        list=new ArrayList<>();

    }


    @Override
    public int getItemViewType(int position) {

        if (position+1  == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return position;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View view = View.inflate(parent.getContext(), R.layout.activity_view_footer, null);
            return new FooterViewHolder(view);
        } else {
            View rootView = View.inflate(parent.getContext(), R.layout.item_news, null);
            return new NewsViewHolder(rootView);
        }
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            footerViewHolder.bindItem();
        }else if (holder instanceof NewsViewHolder) {
            NewsViewHolder newsViewHolder = (NewsViewHolder) holder;
            if(list.size()>0) {
                newsViewHolder.bindItem(list.get(position), position);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size()+1;
    }

    /**
     * footer view
     */
    class FooterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_load_prompt)
        TextView tv_load_prompt;
        @BindView(R.id.progress)
        ProgressBar progress;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dipToPx(context, 40));
            itemView.setLayoutParams(params);
        }

        private void bindItem() {
            switch (status) {
                case LOAD_MORE:
                    progress.setVisibility(View.VISIBLE);
                    tv_load_prompt.setText("正在加载...");
                    itemView.setVisibility(View.VISIBLE);
                    break;
                case LOAD_PULL_TO:
                    progress.setVisibility(View.GONE);
                    tv_load_prompt.setText("上拉加载更多");
                    itemView.setVisibility(View.VISIBLE);
                    break;
                case LOAD_NONE:
                    System.out.println("LOAD_NONE----");
                    progress.setVisibility(View.GONE);
                    tv_load_prompt.setText("已无更多加载");
                    break;
                case LOAD_END:
                    itemView.setVisibility(View.GONE);
                default:
                    break;
            }
        }
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        ImageView iV;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_grade)
        TextView tvGrade;
        @BindView(R.id.tv_art)
        TextView tvArt;
        @BindView(R.id.ll_item_view)
        LinearLayout llItemView;

        NewsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            tvArt.setTextColor(ThemeUtils.getThemeColor());
        }
        private void bindItem(NewsSummary newsSummary, int positon) {
            DisplayImgUtis.getInstance().display(context,newsSummary.getImgsrc(),iV);
            if(!TextUtils.isEmpty(newsSummary.getTitle())) {
                tvName.setText(newsSummary.getTitle());
            }
            if(newsSummary.getDigest()!=null) {
                tvGrade.setText(newsSummary.getDigest());
            }
            if(!TextUtils.isEmpty(newsSummary.getLmodify())) {
                tvArt.setText("发布时间:" + newsSummary.getLmodify());
            }

            llItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context, WebviewActivity.class);
                    intent.putExtra(WebviewActivity.EXTRA_URL,newsSummary.getUrl());
                    context.startActivity(intent);
                }
            });

        }
    }

    public void updateLoadStatus(int status) {
        this.status = status;
        notifyDataSetChanged();
    }

    public List<NewsSummary> getList() {
        return list;
    }

    public void setList(List<NewsSummary> list) {
        this.list = list;
    }
}
