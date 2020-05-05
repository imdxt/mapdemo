package com.yuanyang.map.test.http;

import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.yuanyang.map.test.DataCenter;
import com.yuanyang.map.test.model.EventLevelSetting;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HttpGetAllPointStatusSetting {
    public interface HttpGetAllPointStatusSettingListener
    {
        void OnGetAllPointStatusSettingSuccess(List<EventLevelSetting> points);
        void OnGetAllPointStatusSettingError(HttpEnumResult error);
    }
    private HttpGetAllPointStatusSettingListener listener;
    public void SetListener(HttpGetAllPointStatusSettingListener _listener)
    {
        listener = _listener;
    }

    public boolean GetAllPointStatusSetting()
    {
        String url =  DataCenter.getInstance().UrlGetAllEventLevelSetting();
        OkGo.<String>get(url).retryCount(DataCenter.getInstance().GetHttpRetryCount()).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();

                try{
                    List<EventLevelSetting> result = new ArrayList();

                    JSONObject jsonObject = new JSONObject(body);
                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("message");
                    JSONArray dataArray = jsonObject.optJSONArray("data");

                    if (code != 0) {
                        if (listener!=null){
                            listener.OnGetAllPointStatusSettingError(HttpEnumResult.RETURN_CODE_ERROR);
                            return;
                        }
                    }

                    for (int i=0;i<dataArray.length();i++)
                    {
                        EventLevelSetting point = new EventLevelSetting();
                        JSONObject jsonObject1 = (JSONObject)dataArray.get(i);
                        point.id = jsonObject1.optInt("id");
                        point.level = jsonObject1.optInt("level");
                        point.title = jsonObject1.optString("title");
                        point.description = jsonObject1.optString("description");
                        result.add(point);
                    }

                    if (listener!=null){
                        listener.OnGetAllPointStatusSettingSuccess(result);
                        return;
                    }

                }  catch (Exception e) {
                    e.printStackTrace();

                    if (listener!=null){
                        listener.OnGetAllPointStatusSettingError(HttpEnumResult.JSON_PARSE_ERROR);
                        return;
                    }
                }
            }

            @Override
            public void onError(Response<String> response) {

                Log.d("wk", "onError: ");
                
                if (listener!=null){
                    listener.OnGetAllPointStatusSettingError(HttpEnumResult.HTTP_VISIT_ERROR);
                    return;
                }
            }
        });
        return true;
    }
}
