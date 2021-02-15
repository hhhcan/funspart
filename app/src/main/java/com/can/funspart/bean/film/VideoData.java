package com.can.funspart.bean.film;

import android.text.TextUtils;

import com.can.funspart.okhttp.http.OkHttpClientManager;
import com.can.funspart.utils.GsonUtil;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;


public class VideoData extends BaseVideoData {

    public static VideoData fromJSONData(String jsonStr) {
        VideoData data = new VideoData();
        if (TextUtils.isEmpty(jsonStr)) {
            return data;
        }
        try {
            JSONObject json = new JSONObject(jsonStr);
            //视频内容信息
            String url = "http://front-gateway.mtime.com/video/play_url?video_id=" + json.optInt("videoId") + "&source=1&scheme=http";
            OkHttpClientManager.getAsyn(url, new OkHttpClientManager.StringCallback() {

                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(String response) {
                    FilmsPlayResponse playResponse = (FilmsPlayResponse) GsonUtil.JSONToObject(response, FilmsPlayResponse.class);
                    if(playResponse!=null&& playResponse.getData()!=null){
                        data.videoPlayUrl = playResponse.getData().get(0).url;
                    }
                }
            });

            data.coverImgUrl = json.optString("imgUrl");
            data.dynamicCover = json.optString("imgUrl");
//            data.videoWidth = sizeJson.optInt("width");
//            data.videoHeight = sizeJson.optInt("height");
            data.title = json.optString("title");
//            data.createTime = json.optLong("create_time") * 1000;
            data.playCount = (long) (Math.random() * 1000);
            data.likeCount = (long) (Math.random() * 1000);
        } catch (Exception e) {
        }

        return data;
    }

    @Override
    public String toString() {
        return "videotitle=" + title + ",videoplayurl=" + videoPlayUrl + ",videodownloadurl=" + videoDownloadUrl + ",width=" + videoWidth + ",height=" + videoHeight
                + ",coverimgurl=" + coverImgUrl + ",musicname=" + musicName + ",musicimgurl=" + musicImgUrl + ",musicauthorname=" + musicAuthorName + ",authorname="
                + authorName + ",authorimgurl=" + authorImgUrl + ",playcount=" + playCount;
    }

}
