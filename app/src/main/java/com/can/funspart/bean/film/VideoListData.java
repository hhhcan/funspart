package com.can.funspart.bean.film;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VideoListData {

    public List<BaseVideoData> videoDataList = null;

    public static VideoListData fromJSONData(String str) {
        VideoListData data = new VideoListData();
        if (TextUtils.isEmpty(str)) {
            return data;
        }
        try {
            JSONObject json = new JSONObject(str);
            JSONArray videoAry = json.getJSONObject("data").getJSONArray("moviecomings");
            data.videoDataList = new ArrayList<BaseVideoData>();
            for (int i = 0; i < videoAry.length(); i++) {
                if(videoAry.getJSONObject(i).optInt("videoId")>0){
                    data.videoDataList.add(VideoData.fromJSONData(videoAry.getJSONObject(i).toString()));
                }
            }
        } catch (Exception e) {
        }
        return data;
    }

    public List<BaseVideoData> getVideoDataList() {
        return videoDataList;
    }
}
