package com.yuanyang.map.test.http;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.yuanyang.map.test.DataCenter;

import org.json.JSONObject;

public class HttpDeleteEventPoint {
    public interface HttpDeleteEventPointListener
    {
        void OnDeleteEventPointSuccess();
        void OnDeleteEventPointError(HttpEnumResult error);
    }
    private HttpDeleteEventPointListener listener;
    public void SetListener(HttpDeleteEventPointListener _listener)
    {
        listener = _listener;
    }

    public boolean DeletePoint(String uuid,String editor)
    {
        HttpParams params = new HttpParams();
        params.put("uuid", uuid);
        params.put("editor", editor);

        String url = DataCenter.getInstance().UrlDeleteEventPoint();
        OkGo.<String>post(url).params(params).retryCount(DataCenter.getInstance().GetHttpRetryCount()).execute(new StringCallback() {

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
                                listener.OnDeleteEventPointSuccess();
                                return;
                            }
                        }
                        else {
                            if (listener!=null){
                                listener.OnDeleteEventPointError(HttpEnumResult.RESULT_ERROR);
                                return;
                            }
                        }
                    }
                    else
                    {
                        if (listener!=null){
                            listener.OnDeleteEventPointError(HttpEnumResult.RETURN_CODE_ERROR);
                            return;
                        }
                    }


                }  catch (Exception e) {
                    e.printStackTrace();

                    if (listener!=null){
                        listener.OnDeleteEventPointError(HttpEnumResult.JSON_PARSE_ERROR);
                        return;
                    }
                }
            }

            @Override
            public void onError(Response<String> response) {
                if (listener!=null){
                    listener.OnDeleteEventPointError(HttpEnumResult.HTTP_VISIT_ERROR);
                    return;
                }
            }
        });

        return true;
    }
}
