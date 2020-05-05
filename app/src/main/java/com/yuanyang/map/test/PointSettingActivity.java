package com.yuanyang.map.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.yuanyang.map.test.model.EventPoint;
import com.yuanyang.map.test.model.EventLevelSetting;

public class PointSettingActivity extends AppCompatActivity {

    Spinner spinnerStatus = null;
    EditText txtTitle = null;
    EditText txtUser = null;
    EditText txtRemark = null;
    EventPoint mEventPoint;
    String mOperation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinnerStatus = findViewById(R.id.spinner_status);
        ArrayAdapter<EventLevelSetting> adapter = new ArrayAdapter<EventLevelSetting>(this,
                android.R.layout.simple_spinner_item, DataCenter.getInstance().GetListPointStatusSetting());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinnerStatus.setAdapter(adapter);

        txtTitle = findViewById(R.id.txt_title);
        txtUser = findViewById(R.id.txt_username);
        txtRemark = findViewById(R.id.txt_remark);

        Button btnSave = findViewById(R.id.button_save);
        Button btnEsc = findViewById(R.id.button_esc);

        btnSave.setOnClickListener(new SaveButtonClicked());
        btnEsc.setOnClickListener(new EscButtonClicked());

        Intent intent = getIntent();
        mEventPoint = (EventPoint) intent.getSerializableExtra("point");
        mOperation = intent.getStringExtra("operation");

        txtUser.setText( DataCenter.getInstance().GetLoginUserName());

        if (mOperation.equalsIgnoreCase("update"))
        {
            txtTitle.setText(mEventPoint.title );
            txtRemark.setText(mEventPoint.remark);

            int k= adapter.getCount();
            for(int i=0;i<k;i++){
                if(mEventPoint.level == ((EventLevelSetting)adapter.getItem(i)).level )
                {
                    spinnerStatus.setSelection(i);// 默认选中项
                    break;
                }
            }
        }

        txtUser.setEnabled(false);
    }

    public class SaveButtonClicked implements Button.OnClickListener{

        @Override
        public void onClick(View v) {

            Intent intent = new Intent();
            mEventPoint.title = txtTitle.getText().toString();
            mEventPoint.editor = txtUser.getText().toString();
            mEventPoint.level = ((EventLevelSetting) spinnerStatus.getSelectedItem()).level;
            mEventPoint.remark = txtRemark.getText().toString();
            intent.putExtra("point", mEventPoint);
            intent.putExtra("operation",mOperation);
            setResult(3, intent);
            finish();
        }
    }

    public class EscButtonClicked implements Button.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.putExtra("point", mEventPoint);
            intent.putExtra("operation",mOperation);
            setResult(4, intent);
            finish();
        }
    }
}