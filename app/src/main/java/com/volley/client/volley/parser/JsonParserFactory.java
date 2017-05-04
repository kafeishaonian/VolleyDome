package com.volley.client.volley.parser;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.volley.client.model.AbstractBaseModel;
import com.volley.client.model.UserModel;
import com.volley.client.volley.ServerErrorCode;
import com.volley.client.volley.exception.HttpClientAPIException;

import org.json.JSONException;

import java.io.IOException;

public class JsonParserFactory {
    /**
     * TAG
     */
    private static final String TAG = "JsonParserFactory";

    public static <T extends UserModel> T parseStringJson(Class<T> cls, Object context) throws JSONException,
            HttpClientAPIException, IOException {
        final T response;
        try {
            response = new Gson().fromJson((String) context, cls);
        } catch (JsonSyntaxException e) {
            throw new JSONException(e.getMessage());
        } catch (JsonIOException e) {
            throw new IOException(e.getMessage());
        } catch (JsonParseException e) {
            throw new JSONException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new JSONException(e.getMessage());
        }

        if (response == null) {
            throw new JSONException(TAG + " JsonParser is null.");
        }

//        if (response.getCode() != ServerErrorCode.STATUS_SUCCESS) {
//            throw new HttpClientAPIException(response.getCode(), response.getMsg());
//        }

        return response;
    }

    /**
     * 解析基本数据类型
     *
     * @param cls
     * @return
     */
    public static <T extends UserModel> IResultDataParser<T> parseBaseModel(final Class<T> cls) {

        return new IResultDataParser<T>() {
            @Override
            public T parse(Object response) throws JSONException, HttpClientAPIException, IOException {
                return parseStringJson(cls, (String) response);
            }
        };
    }

}