package com.can.funspart.bean.film.event;

import com.can.funspart.bean.film.BaseVideoData;

import java.util.List;

public class RefreshEvent {

    List<BaseVideoData> mList;
    int position;
    long max_cursor;

    public RefreshEvent(List<BaseVideoData> mList, int position, long max_cursor) {
        this.mList = mList;
        this.position = position;
        this.max_cursor = max_cursor;
    }

    public List<BaseVideoData> getList() {
        return mList;
    }

    public int getPosition() {
        return position;
    }

    public long getMaxCursor() {
        return max_cursor;
    }
}
