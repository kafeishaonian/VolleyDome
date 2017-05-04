package com.volley.client.volley.parser;

/**
 * Created by Administrator on 2017/4/10 0010.
 */

public interface IResultReceiver {
    /**
     * 返回数据接口
     * @param resultCode
     * @param resultData
     */
    void onReceiveResult(int resultCode, Object resultData);

}
