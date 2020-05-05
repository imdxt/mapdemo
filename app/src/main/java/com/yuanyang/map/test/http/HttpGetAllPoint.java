package com.yuanyang.map.test.http;

import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.yuanyang.map.test.DataCenter;
import com.yuanyang.map.test.model.EventPoint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HttpGetAllPoint {

    public interface HttpGetAllPointListener
    {
        void OnGetAllPointSuccess(List<EventPoint> eventPoints);
        void OnGetAllPointError(HttpEnumResult error);
    }
    private HttpGetAllPointListener listener;
    public void SetListener(HttpGetAllPointListener _listener)
    {
        listener = _listener;
    }

    public boolean GetAllPoint()
    {
        String url = DataCenter.getInstance().UrlGetAllEventPoint();
        OkGo.<String>get(url).retryCount(DataCenter.getInstance().GetHttpRetryCount()).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();

                Log.d("wk","GetAllPointError!!!body="+body);

                try{
                    List<EventPoint> result = new ArrayList();

                    JSONObject jsonObject = new JSONObject(body);
                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("message");
                    JSONArray dataArray = jsonObject.optJSONArray("data");

                    if (code != 0) {
                        if (listener!=null){
                            listener.OnGetAllPointError(HttpEnumResult.RETURN_CODE_ERROR);
                            return;
                        }
                    }

                    for (int i=0;i<dataArray.length();i++)
                    {
                        EventPoint eventPoint = new EventPoint();
                        JSONObject jsonObject1 = (JSONObject)dataArray.get(i);
                        //point.id = jsonObject1.optInt("id");
                        eventPoint.uuid = jsonObject1.optString("uuid");
                        eventPoint.longitude = (float)jsonObject1.optDouble("longitude");
                        eventPoint.latitude = (float)jsonObject1.optDouble("latitude");
                        eventPoint.title = jsonObject1.optString("title");
                        eventPoint.level = jsonObject1.optInt("level");
                        eventPoint.editor = jsonObject1.optString("editor");
                        eventPoint.remark = jsonObject1.optString("remark");
                        result.add(eventPoint);
                    }

                    if (listener!=null){
                        listener.OnGetAllPointSuccess(result);
                        return;
                    }

                }  catch (Exception e) {
                    e.printStackTrace();

                    if (listener!=null){
                        listener.OnGetAllPointError(HttpEnumResult.JSON_PARSE_ERROR);
                        return;
                    }
                }
            }

            @Override
            public void onError(Response<String> response) {

                Log.d("wk","GetAllPointError!!!");

                if (listener!=null){
                    listener.OnGetAllPointError(HttpEnumResult.HTTP_VISIT_ERROR);
                    return;
                }
            }
        });
        return true;
    }
}
