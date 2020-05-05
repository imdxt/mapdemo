package com.yuanyang.map.test.http;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.yuanyang.map.test.DataCenter;

import org.json.JSONObject;

public class HttpRequestLogin {

    public interface HttpRequestLoginListener{
        void OnRequestLoginSuccess();
        void OnRequestLoginError(HttpEnumResult error);
    }
    private HttpRequestLoginListener listener;
    public void SetListener(HttpRequestLoginListener _listener)
    {
        listener = _listener;
    }

    public boolean Login(String account,String passwd)
    {
        HttpParams params = new HttpParams();
        params.put("account", account);
        params.put("passwd", passwd);

        String url = DataCenter.getInstance().UrlLogin();
        OkGo.<String>post(url).retryCount(DataCenter.getInstance().GetHttpRetryCount()).params(params).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();

                try{

                    JSONObject jsonObject = new JSONObject(body);
                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("message");
                    JSONObject dataObject= jsonObject.optJSONObject("data");

                    if (code == 0)
                    {
                        boolean result = dataObject.optBoolean("result");

                        if(result) {
                            if (listener != null) {
                                listener.OnRequestLoginSuccess();
                                return;
                            }
                        }
                        else {
                            if (listener!=null){
                                listener.OnRequestLoginError(HttpEnumResult.RESULT_ERROR);
                                return;
                            }
                        }
                    }
                    else
                    {
                        if (listener!=null){
                            listener.OnRequestLoginError(HttpEnumResult.RETURN_CODE_ERROR);
                            return;
                        }
                    }


                }  catch (Exception e) {
                    e.printStackTrace();

                    if (listener!=null){
                        listener.OnRequestLoginError(HttpEnumResult.JSON_PARSE_ERROR);
                        return;
                    }
                }
            }

            @Override
            public void onError(Response<String> response) {
                if (listener!=null){
                    listener.OnRequestLoginError(HttpEnumResult.HTTP_VISIT_ERROR);
                    return;
                }
            }
        });
        return true;
    }

}
