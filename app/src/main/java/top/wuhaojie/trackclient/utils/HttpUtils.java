package top.wuhaojie.trackclient.utils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 网络请求封装类
 * Created by wuhaojie on 2016/5/2 21:44.
 */
public class HttpUtils {


    /**
     * AsyncHttpClient网络框架客户端
     */
    private static AsyncHttpClient sHttpClient;

    /**
     * 上传文件
     *
     * @param file    文件对象
     * @param addr    访问地址
     * @param handler 回调接口
     * @throws FileNotFoundException
     */
    public static void uploadFile(File file, String addr, AsyncHttpResponseHandler handler) throws FileNotFoundException {
        RequestParams requestParams = new RequestParams();
        requestParams.put("file", file);
        if (sHttpClient == null) {
            sHttpClient = new AsyncHttpClient();
        }
        sHttpClient.setResponseTimeout(60 * 1000);
        sHttpClient.setTimeout(60 * 1000);
        sHttpClient.setConnectTimeout(60 * 1000);
        sHttpClient.post(addr, requestParams, handler);
    }


    public static String requestResponse(String addr) {
        final String[] response = {null};
        if (sHttpClient == null) {
            sHttpClient = new AsyncHttpClient();
        }
        sHttpClient.post(addr, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                response[0] = new String(bytes);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
        return response[0];
    }

}
