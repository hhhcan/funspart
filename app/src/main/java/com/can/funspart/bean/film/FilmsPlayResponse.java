package com.can.funspart.bean.film;

import java.util.List;

public class FilmsPlayResponse {
    private int code;
    private List<PlayData> data;
    private String msg;
    private String requestId;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setData(List<PlayData> data) {
        this.data = data;
    }
    public List<PlayData> getData() {
        return data;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }


}
