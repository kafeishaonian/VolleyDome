package com.volley.client.volley;

import com.android.volley.center.AsyncRequestCenter;

/**
 * Created by Administrator on 2017/4/10 0010.
 */

public class ServerErrorCode implements AsyncRequestCenter.ErrorCode{
    /**
     * TAG
     */
    public static final String TAG = ServerErrorCode.class.getCanonicalName();

    /**
     * 返回成功
     */
    public static final int STATUS_SUCCESS = 0;

    /**
     * 返回失败
     */
    public static final int STATUS_EMPTY = 1;

}
