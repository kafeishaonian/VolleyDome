package com.volley.client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.volley.client.model.UserModel;
import com.volley.client.volley.HttpTackManager;
import com.volley.client.volley.parser.IResultReceiver;
import com.volley.client.volley.parser.JsonParserFactory;
import com.volley.client.volley.url.DataRequestUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
    }


    private void initData(){
        HttpTackManager.startStringRequest(
                DataRequestUtils.getKLineRequestParam(TAG, "1", "20"),
                JsonParserFactory.parseBaseModel(UserModel.class), new IResultReceiver() {
                    @Override
                    public void onReceiveResult(int resultCode, Object resultData) {
                        UserModel model = (UserModel) resultData;
                        Log.e(TAG, model.getMultiResult().getResults().get(0).getPicUrl() + "哈哈哈哈哈哈哈哈哈" + resultCode);
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpTackManager.cancelAllRequests(TAG);
    }
}
