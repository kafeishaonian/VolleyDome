package com.volley.client.volley.url;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.center.RequestParams;

public class DataRequestUtils {
    private static final String TAG = "DataRequestUtils";

    /**
     * domain
     */
    private static final String GET_NONFARM_INFO = "/v3/gw";


    /**
     * params
     */
    private static final String FILE_SEPARATOR = "/";
    private static final int CACHE_TIMEOUT = 2 * 60 * 1000;//默认缓存时长
    private static final int CACHE_TIMEOUT_1H = 1 * 60 * 1000;//缓存时长

    private static String combineRequestUrl(String domain) {
        String url = DataHostUtils.getApiHost() + domain;
        return url;
    }

    public static String combineRequestUrl(String host, String method) {
        if (TextUtils.isEmpty(host)) {
            if (host.endsWith(FILE_SEPARATOR)) {
                host = host.substring(0, host.length() - 1);
            }
        }
        if (TextUtils.isEmpty(method)) {
            if (!method.startsWith(FILE_SEPARATOR)) {
                method = FILE_SEPARATOR + method;
            }
        }
        return host + method;
    }

    public static String combineRequestUrlWithParam(String method, long param) {
        String url = String.format(combineRequestUrl(method), param);
        return url;
    }

    public static String combineRequestUrlWithParam(String method, long param1, long param2) {
        String url = String.format(combineRequestUrl(method), param1, param2);
        return url;
    }

    private static void addBaseParams(RequestParams requestParams) {
        requestParams.addQueryParam("method", "item.info.get");
        requestParams.addQueryParam("format", "json");
        requestParams.addQueryParam("itemCodes", "yg8CVootoAc");
        requestParams.addQueryParam("appKey", "myKey");
    }

    /**
     * KLine接口
     *
     * @param tag
     * @return
     */
    public static RequestParams getKLineRequestParam(String tag, String page, String limit) {
        String url = combineRequestUrl(GET_NONFARM_INFO);
        RequestParams requestParams = new RequestParams(url, Request.Method.GET);
        requestParams.setTag(tag);
        addBaseParams(requestParams);
        requestParams.addQueryParam("page", page);
        requestParams.addQueryParam("limit", limit);
//        requestParams.setCacheTimeoutMs(CACHE_TIMEOUT);//可选
        return requestParams;
    }
}
