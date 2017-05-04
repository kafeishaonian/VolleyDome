package com.volley.client.volley.parser;

import com.volley.client.volley.exception.HttpClientAPIException;

import org.json.JSONException;

import java.io.IOException;

/**
 * 解析器基类
 *
 * Created by Administrator on 2017/4/10 0010.
 */

public interface IResultDataParser<T> {
    /**
     * 解析服务器响应
     * @param response
     * @return T 解析response后得到的数据类型
     * @throws JSONException 解析服务器响应的JSON
     * @throws HttpClientAPIException 服务器返回的错误代码
     * @throws IOException 获取服务器响应异常
     */
    T parse(Object response) throws JSONException, HttpClientAPIException, IOException;

}
