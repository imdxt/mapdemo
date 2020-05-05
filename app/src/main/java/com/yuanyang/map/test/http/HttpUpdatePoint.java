package com.yuanyang.map.test.http;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.yuanyang.map.test.DataCenter;
import com.yuanyang.map.test.model.EventPoint;

import org.json.JSONObject;

public class HttpUpdatePoint {

    public interface HttpUpdatePointListener{
        void OnUpdatePointSuccess();
        void OnUpdatePointError(HttpEnumResult error);
    }
    private HttpUpdatePointListener listener;
    public void SetListener(HttpUpdatePointListener _listener)
    {
        listener = _listener;
    }

    public boolean UpdatePoint(EventPoint eventPoint)
    {
        HttpParams params = new HttpParams();
        params.put("uuid", eventPoint.uuid);
        params.put("level", eventPoint.level);
        params.put("editor", eventPoint.editor);
        params.put("remark", eventPoint.remark);

        String url = DataCenter.getInstance().UrlChangeEventPointLevel();
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
                                listener.OnUpdatePointSuccess();
                                return;
                            }
                        }
                        else {
                            if (listener!=null){
                                listener.OnUpdatePointError(HttpEnumResult.RESULT_ERROR);
                                return;
                            }
                        }
                    }
                    else
                    {
                        if (listener!=null){
                            listener.OnUpdatePointError(HttpEnumResult.RETURN_CODE_ERROR);
                            return;
                        }
                    }


                }  catch (Exception e) {
                    e.printStackTrace();

                    if (listener!=null){
                        listener.OnUpdatePointError(HttpEnumResult.JSON_PARSE_ERROR);
                        return;
                    }
                }
            }

            @Override
            public void onError(Response<String> response) {
                if (listener!=null){
                    listener.OnUpdatePointError(HttpEnumResult.HTTP_VISIT_ERROR);
                    return;
                }
            }
        });
        return true;
    }

}
