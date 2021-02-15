package com.can.funspart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.can.funspart.R;
import com.can.funspart.base.EasyRecyclerViewAdapter;
import com.can.funspart.bean.film.FilmMediaItem;
import com.can.funspart.utils.DisplayImgUtis;
import com.can.funspart.utils.ScreenUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilmLiveAdapter extends EasyRecyclerViewAdapter<FilmMediaItem> {
    Context context;
    public FilmLiveAdapter(Context context){
        this.context=context;

    }
    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_film_live, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, FilmMediaItem data) {
        ViewHolder holder= (ViewHolder) viewHolder;

        holder.tvFilmName.setText(data.getMovieName());
        ViewGroup.LayoutParams params=holder.iVFilm.getLayoutParams();
        int width= ScreenUtils.getScreenWidthDp(context);
        int ivWidth=(width-ScreenUtils.dipToPx(context,80))/3;
        params.width=ivWidth;
        double height=(420.0/300.0)*ivWidth;
        params.height=(int)height;
        holder.iVFilm.setLayoutParams(params);
        if(data.getCoverImg()!=null) {
            DisplayImgUtis.getInstance().display(context, data.getCoverImg(), holder.iVFilm);
        }
        if(!TextUtils.isEmpty(""+data.getType())) {
            holder.tvFilmGrade.setText("类型:"+data.getType().get(0));
        }else{
            holder.tvFilmGrade.setText("暂无分类");
        }
    }

    class ViewHolder extends EasyViewHolder{
        @BindView(R.id.iV_film)
        ImageView iVFilm;
        @BindView(R.id.tv_film_name)
        TextView tvFilmName;
        @BindView(R.id.tv_film_grade)
        TextView tvFilmGrade;

        public   ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
