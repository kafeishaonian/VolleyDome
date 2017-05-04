package com.volley.client.volley.exception;

/**
 * Created by Administrator on 2017/4/10 0010.
 */

public class HttpClientAPIException extends Exception{
    /**
     * 错误码
     */
    private int code;
    private String message;


    public HttpClientAPIException(int errorCode, String errorMessage){
        super(errorMessage);
        code = errorCode;
        message = errorMessage;
    }

    public int getCode(){
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
