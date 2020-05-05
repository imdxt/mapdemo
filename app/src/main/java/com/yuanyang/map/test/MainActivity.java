package com.yuanyang.map.test;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.yuanyang.map.test.http.HttpDeleteEventPoint;
import com.yuanyang.map.test.http.HttpEnumResult;
import com.yuanyang.map.test.http.HttpGetAllPoint;
import com.yuanyang.map.test.http.HttpGetAllPointStatusSetting;
import com.yuanyang.map.test.http.HttpInsertPoint;
import com.yuanyang.map.test.http.HttpUpdatePoint;
import com.yuanyang.map.test.model.EventPoint;
import com.yuanyang.map.test.model.EventLevelSetting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    Switch sw_hide_outdate_point ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        //设置地图长按事件监听
        mBaiduMap.setOnMarkerClickListener(new MyMarkerClickListener());
        mBaiduMap.setOnMapLongClickListener(new MyMapLongClickListener());

        sw_hide_outdate_point = findViewById(R.id.switch_hide_outdate_point);
        sw_hide_outdate_point.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Refresh();
            }
        });

        LoadingUtil.Init(this.mMapView.getContext());

        Refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    /**
     * 将时间戳转换为时间
     */
    public String dateToStamp(long s) {
        String res;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(s);
            res = simpleDateFormat.format(date);
        } catch (Exception e) {
            return "";
        }
        return res;
    }

    List<Overlay> pointOverlays = new ArrayList<Overlay>();
    private void RefreshMapPoints(List<EventPoint> eventPoints)
    {
        //清空界面上所有的point点。
        if (pointOverlays.size() > 0)
        {
            for (int i=0;i<pointOverlays.size();i++)
                pointOverlays.get(i).remove();
        }
        pointOverlays.clear();

        //根据points的内容在baidumap上绘制。
        for (int i = 0; i< eventPoints.size(); i++)
        {
            EventPoint eventPoint = eventPoints.get(i);
            Bundle bundle = new Bundle();  //得到bundle对象
            bundle.putSerializable("point", eventPoint);

            LatLng map_point = new LatLng(eventPoint.latitude, eventPoint.longitude);
            //构建Marker图标
            int bitmap_var = 0;
            if (eventPoint.level == 1)
            {
                bitmap_var = R.drawable.location_green;
            }
            else if (eventPoint.level == 2)
            {
                bitmap_var = R.drawable.location_yellow;
            }
            else if (eventPoint.level == 3)
            {
                bitmap_var = R.drawable.location_red;
            }
            else //(point.current_status == 0)
            {
                if (sw_hide_outdate_point.isChecked())
                {
                    continue;
                }
                bitmap_var = R.drawable.location_black;
            }

            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(bitmap_var);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(map_point)
                    .icon(bitmap)
                    .extraInfo(bundle);
            //在地图上添加Marker，并显示
            Overlay ol = mBaiduMap.addOverlay(option);
            pointOverlays.add(ol);
        }
    }

    private void RefreshPointArray()
    {
        LoadingUtil.LoadingShow("正在获取Point信息...");
        HttpGetAllPoint httpGetAllPoint = new HttpGetAllPoint();
        httpGetAllPoint.SetListener(new HttpGetAllPoint.HttpGetAllPointListener() {
            @Override
            public void OnGetAllPointSuccess(List<EventPoint> eventPoints) {
                RefreshMapPoints(eventPoints);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //要延时的程序
                        LoadingUtil.LoadingClose();
                    }
                },1000);
            }

            @Override
            public void OnGetAllPointError(HttpEnumResult error) {

                LoadingUtil.LoadingClose();
            }
        });
        httpGetAllPoint.GetAllPoint();
    }



    public class MyMarkerClickListener implements BaiduMap.OnMarkerClickListener {

        @Override
        public boolean onMarkerClick(final Marker marker) {

            Bundle bundle = marker.getExtraInfo();
            final EventPoint eventPoint = (EventPoint)bundle.getSerializable("point");

            //调用方法,弹出View(气泡，意即在地图中显示一个信息窗口)，显示当前mark位置信息
            final LatLng latLng = marker.getPosition();

            View view = View.inflate(mMapView.getContext(), R.layout.infowidow, null);
            view.setBackgroundColor(Color.WHITE);
            EditText et = view.findViewById(R.id.editText);
            et.setText(eventPoint.title);

            final Button btnChangeStatus = view.findViewById(R.id.change_status);
            btnChangeStatus.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBaiduMap.hideInfoWindow();

                    Intent intent=new Intent(MainActivity.this, PointSettingActivity.class);
                    intent.putExtra("point", eventPoint);
                    intent.putExtra("operation","update");
                    startActivityForResult(intent,111);
                }
            });

            final Button btnclose = view.findViewById(R.id.close);
            btnclose.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBaiduMap.hideInfoWindow();
                }
            });

            final Button btnDeltePoint = view.findViewById(R.id.delete_point);
            btnDeltePoint.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(mMapView.getContext());
                    builder.setTitle("提示");
                    builder.setMessage("确定要删除<" + eventPoint.title+ ">点吗？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DeletePoint(eventPoint);
                        }
                    });
                    builder.show();
                }
            });

            //在地图中显示一个信息窗口，可以设置一个View作为该窗口的内容，也可以设置一个 BitmapDescriptor 作为该窗口的内容
            InfoWindow infoWindow = new InfoWindow(view, latLng, -47);

            mBaiduMap.showInfoWindow(infoWindow);
            return false;
        }
    }

    public class MyMapLongClickListener implements BaiduMap.OnMapLongClickListener
    {
        @Override
        public void onMapLongClick(final LatLng latLng)
        {
            //add a temp point mark
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.question);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(latLng)
                    .icon(bitmap);
            //在地图上添加Marker，并显示
            final Overlay ol = mBaiduMap.addOverlay(option);

            AlertDialog.Builder builder = new AlertDialog.Builder(mMapView.getContext());
            builder.setTitle("提示");
            builder.setMessage("确定要新增一个点吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ol.remove();

                    EventPoint eventPoint = new EventPoint();
                    eventPoint.latitude = (float) latLng.latitude;
                    eventPoint.longitude = (float) latLng.longitude;

                    Intent intent=new Intent(MainActivity.this, PointSettingActivity.class);
                    intent.putExtra("point", eventPoint);
                    intent.putExtra("operation","insert");
                    startActivityForResult(intent,111);
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ol.remove();
                }
            });
            builder.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //这个方法会在返回新的活动返回数据的时候调用，第一个参数是请求码，第二个参数是返回码,第三个参数是携带了回传数据的Intent
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111)
        {
            if (resultCode == 4)
                return;

            EventPoint eventPoint = (EventPoint)data.getSerializableExtra("point");
            String mOperation = data.getStringExtra("operation");
            if (mOperation.equalsIgnoreCase("update"))
            {
                Log.d("wk!", eventPoint.editor+ eventPoint.level + eventPoint.remark);

                HttpUpdatePoint httpUpdatePoint = new HttpUpdatePoint();
                httpUpdatePoint.SetListener(new HttpUpdatePoint.HttpUpdatePointListener() {
                    @Override
                    public void OnUpdatePointSuccess() {
                        Toast.makeText(mMapView.getContext(), "修改点位成功!", Toast.LENGTH_LONG).show();
                        Refresh();
                    }

                    @Override
                    public void OnUpdatePointError(HttpEnumResult error) {
                        Toast.makeText(mMapView.getContext(), "修改点位失败!", Toast.LENGTH_LONG).show();
                    }
                });
                httpUpdatePoint.UpdatePoint(eventPoint);
            }
            else if (mOperation.equalsIgnoreCase("insert"))
            {
                HttpInsertPoint httpInsertPoint = new HttpInsertPoint();
                httpInsertPoint.SetListener(new HttpInsertPoint.HttpInsertPointListener() {
                    @Override
                    public void OnInsertPointSuccess() {
                        Toast.makeText(mMapView.getContext(), "插入新点位成功!", Toast.LENGTH_LONG).show();
                        Refresh();
                    }

                    @Override
                    public void OnInsertPointError(HttpEnumResult error) {
                        Toast.makeText(mMapView.getContext(), "插入新点位失败!", Toast.LENGTH_LONG).show();
                    }
                });
                httpInsertPoint.InsertPoint(eventPoint);
            }
        }
    }


    private void Refresh()
    {
        mBaiduMap.hideInfoWindow();
        LoadingUtil.LoadingShow("正在获取Point设置信息...");
        HttpGetAllPointStatusSetting httpGetAllPointStatusSetting = new HttpGetAllPointStatusSetting();
        httpGetAllPointStatusSetting.SetListener(new HttpGetAllPointStatusSetting.HttpGetAllPointStatusSettingListener() {
            @Override
            public void OnGetAllPointStatusSettingSuccess(List<EventLevelSetting> points) {
                DataCenter.getInstance().SetListPointStatusSetting(points);

                RefreshPointArray();
            }

            @Override
            public void OnGetAllPointStatusSettingError(HttpEnumResult error) {
                LoadingUtil.LoadingClose();
            }
        });
        httpGetAllPointStatusSetting.GetAllPointStatusSetting();
    }


    private void DeletePoint(EventPoint eventPoint)
    {
        HttpDeleteEventPoint httpDeleteEventPoint = new HttpDeleteEventPoint();
        httpDeleteEventPoint.SetListener(new HttpDeleteEventPoint.HttpDeleteEventPointListener() {
            @Override
            public void OnDeleteEventPointSuccess() {
                Toast.makeText(mMapView.getContext(), "删除点位成功!", Toast.LENGTH_LONG).show();
                Refresh();
            }

            @Override
            public void OnDeleteEventPointError(HttpEnumResult error) {
                Toast.makeText(mMapView.getContext(), "删除点位失败!", Toast.LENGTH_LONG).show();
                Refresh();
            }
        });

        httpDeleteEventPoint.DeletePoint(eventPoint.uuid,DataCenter.getInstance().GetLoginUserName());
    }
}
