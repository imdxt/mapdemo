package com.yuanyang.map.test;

import com.yuanyang.map.test.model.EventLevelSetting;

import java.util.List;


public class DataCenter {

    private volatile static DataCenter mSingleInstance = null;
    private DataCenter ()
    {

    }

    public static DataCenter getInstance()
    {
        if (mSingleInstance == null) {
            synchronized (DataCenter.class) {
                if (mSingleInstance == null) {
                    mSingleInstance = new DataCenter();
                }
            }
        }
        return mSingleInstance;
    }

    private String loginUserName = "";
    public String GetLoginUserName()
    {
        return loginUserName;
    }
    public void SetLoginUserName(String v)
    {
        loginUserName = v;
    }

    List<EventLevelSetting> m_eventLevelSetting = null;
    public List<EventLevelSetting> GetListPointStatusSetting()
    {
        return m_eventLevelSetting;
    }
    public void SetListPointStatusSetting( List<EventLevelSetting> v)
    {
        m_eventLevelSetting = v;
    }

    private int httpRetryCount = 0;
    public int GetHttpRetryCount()
    {
        return httpRetryCount;
    }

    public String g_BaseUrl = "http://152.136.28.95:8012";
    public String UrlGetAllEventPoint()
    {
        return g_BaseUrl + "/api/event/point/select";
    }
    public String UrlGetAllEventLevelSetting()
    {
        return g_BaseUrl + "/api/event/setting/select";
    }
    public String UrlInsertEventPoint()
    {
        return g_BaseUrl + "/api/event/point/insert";
    }
    public String UrlChangeEventPointLevel()
    {
        return g_BaseUrl + "/api/event/point/updatelevel";
    }
    public String UrlDeleteEventPoint()
    {
        return g_BaseUrl + "/api/event/point/delete";
    }
    public String UrlLogin()
    {
        return g_BaseUrl + "/api/user/login";
    }
}
