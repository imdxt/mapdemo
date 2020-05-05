package com.yuanyang.map.test;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

public class LoadingUtil {
    private static Dialog dialog = null;
    private static TextView tv = null;

    public static void Init(Context context) {
        if(dialog==null){
            AVLoadingIndicatorView view = new AVLoadingIndicatorView(context);
            view.setIndicator("BallGridPulseIndicator");
            view.setIndicatorColor(Color.RED);
            LinearLayout ll = new LinearLayout(context);
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.setGravity(Gravity.CENTER);
            ll.addView(view,new LinearLayout.LayoutParams(200,200));

            tv = new TextView(context);
            tv.setTextColor(Color.GREEN);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            tv.setText("...");

            ll.addView(tv,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            dialog = new Dialog(context, R.style.Loading);
            dialog.setContentView(ll);// 设置布局
            //view.setBackgroundColor(Color.parseColor("#00000000"));
        }
    }

    public static void LoadingClose() {
        if(dialog!=null)
            dialog.cancel();
    }

    public static void LoadingShow(String txt)
    {
        if (dialog!=null) {
            tv.setText(txt);
            dialog.show();
        }
    }
}
