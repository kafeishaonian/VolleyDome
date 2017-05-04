package com.volley.client.volley;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.center.AsyncRequestCenter;
import com.android.volley.center.RequestParams;
import com.volley.client.MyApplication;
import com.volley.client.volley.connect.DeviceBandwidthSampler;
import com.volley.client.volley.exception.HttpClientAPIException;
import com.volley.client.volley.parser.IResultDataParser;
import com.volley.client.volley.parser.IResultReceiver;
import com.volley.client.volley.util.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/10 0010.
 */

public class HttpTackManager {
    /**
     * TAG
     */
    private static final String TAG = HttpTackManager.class.getSimpleName();


    /**
     * handler
     */
    private static Handler mHandler;

    private static long mStartTime;
    private static long mEndTime;

    static {
        mHandler = new Handler();
    }


    public static <T> void startStringRequest(Request.Priority mPriority, final RequestParams requestParams, final IResultDataParser<T> parserObj,
                                              final IResultReceiver resultReceiver) {
        mStartTime = System.currentTimeMillis();

        if (requestParams == null) {
            Log.e(TAG, "startStringRequest error!!! requestParams is null");
            if (resultReceiver != null) {
                resultReceiver.onReceiveResult(ServerErrorCode.NO_CONNECTION_ERROR, null);
            }
            return;
        }

        //开始采样
        DeviceBandwidthSampler.getInstance().startSampling();

        boolean isNetAcailable = NetworkUtils.isOnline(MyApplication.getInstance());
        if (mPriority == null) {
            mPriority = Request.Priority.NORMAL;
        }

        //开启网络请求
        AsyncRequestCenter.getInstance().startStringRequest(mPriority, requestParams, new AsyncRequestCenter.RequestListener<String>() {
            @Override
            public void onSuccess(String result) {
                //停止采样
                DeviceBandwidthSampler.getInstance().stopSampling();

                // 解析器或者结果回调器为空则返回，不进行任何网络请求操作
                if ((parserObj == null) || (resultReceiver == null)) {
                    Log.e(TAG, "parserObj or resultReceiver is null...");
                    return;
                }

                // 判断数据返回结果是否为空
                if (TextUtils.isEmpty(result)) {
                    resultReceiver.onReceiveResult(ServerErrorCode.STATUS_EMPTY, null);
                    return;
                }

                // 数据处理逻辑过程
                onContentProviderProcess(result, parserObj, resultReceiver);
            }

            @Override
            public void onFailure(int errorCode, String errorMsg) {
                Log.d(TAG, "onFailure =====errorCode==== " + errorCode + " ==== errorMsg === " + errorMsg);
                // 停止采样
                DeviceBandwidthSampler.getInstance().stopSampling();

                // 数据处理逻辑过程
                onContentProviderProcess(errorMsg, parserObj, resultReceiver);
            }
        }, isNetAcailable);
    }


    public static <T> void startStringRequest(final RequestParams requestParams, final IResultDataParser<T> parserObj,
                                              final IResultReceiver resultReceiver) {
        startStringRequest(Request.Priority.NORMAL, requestParams, parserObj, resultReceiver);
    }


    /**
     * 数据解析和数据处理逻辑
     *
     * @param result
     * @param parserObj
     * @param resultReceiver create at 2014-4-9 下午3:38:30
     * @author xiangyutian
     */
    @SuppressWarnings("unchecked")
    private static <T> void onContentProviderProcess(final Object result, final IResultDataParser<T> parserObj,
                                                     final IResultReceiver resultReceiver) {
        // 进行解析
        T resultObj = null;
        int statusCode = ServerErrorCode.STATUS_SUCCESS;

        try {
            resultObj = parserObj.parse(result);
        } catch (HttpClientAPIException e) {
            Log.e(TAG, "EBusinessApiException break out", e);
            statusCode = e.getCode();
            resultObj = (T) e.getMessage();
        } catch (JSONException e) {
            Log.e(TAG, "JSONException break out", e);
            statusCode = ServerErrorCode.PARSE_ERROR;
        } catch (IOException e) {
            Log.e(TAG, "IOException break out", e);
            statusCode = ServerErrorCode.PARSE_ERROR;
        } finally {
            // 转换至UI线程
            runUiThread(resultReceiver, statusCode, resultObj);
        }
    }


    /**
     * 切换至UI线程
     *
     * @param resultReceiver
     * @param statuscode
     */
    private static <T> void runUiThread(final IResultReceiver resultReceiver, final int statuscode, final T resultObj) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mEndTime = System.currentTimeMillis();

                Log.d(TAG, "网络请求耗时:   " + (mEndTime - mStartTime));
                // 回调输出
                resultReceiver.onReceiveResult(statuscode, resultObj);
            }
        });
    }


    /**
     * 根据TAG,关闭对应页面http任务
     *
     * @param tag 请求标识 create at 2014-3-27 下午9:05:28
     */
    public static void cancelAllRequests(Object tag) {
        AsyncRequestCenter.getInstance().cancelAll(tag);
    }

}
