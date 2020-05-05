package com.yuanyang.map.test.http;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.yuanyang.map.test.DataCenter;
import com.yuanyang.map.test.model.EventPoint;

import org.json.JSONObject;

public class HttpInsertPoint {

    public interface HttpInsertPointListener{
        void OnInsertPointSuccess();
        void OnInsertPointError(HttpEnumResult error);
    }
    private HttpInsertPointListener listener;
    public void SetListener(HttpInsertPointListener _listener)
    {
        listener = _listener;
    }


    public boolean InsertPoint(EventPoint eventPoint)
    {
        HttpParams params = new HttpParams();
        params.put("longitude", eventPoint.longitude);
        params.put("latitude", eventPoint.latitude);
        params.put("title", eventPoint.title);
        params.put("level", eventPoint.level);
        params.put("editor", eventPoint.editor);
        params.put("remark", eventPoint.remark);

        String url = DataCenter.getInstance().UrlInsertEventPoint();
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
                                listener.OnInsertPointSuccess();
                                return;
                            }
                        }
                        else {
                            if (listener!=null){
                                listener.OnInsertPointError(HttpEnumResult.RESULT_ERROR);
                                return;
                            }
                        }
                    }
                    else
                    {
                        if (listener!=null){
                            listener.OnInsertPointError(HttpEnumResult.RETURN_CODE_ERROR);
                            return;
                        }
                    }


                }  catch (Exception e) {
                    e.printStackTrace();

                    if (listener!=null){
                        listener.OnInsertPointError(HttpEnumResult.JSON_PARSE_ERROR);
                        return;
                    }
                }
            }

            @Override
            public void onError(Response<String> response) {
                if (listener!=null){
                    listener.OnInsertPointError(HttpEnumResult.HTTP_VISIT_ERROR);
                    return;
                }
            }
        });
        return true;
    }
}
