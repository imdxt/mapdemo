package com.yuanyang.map.test;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class PointInfoSettingDialog extends DialogFragment {

    public interface PointInfoSettingDialogListener{
        void OnSaveRequest(String content);
        void OnCloseRequest();
        void OnDialogDestroy();
    }

    private Button closeButton = null;
    private Button saveButton = null;
    private EditText contentText = null;
    private String content = "";
    private boolean result = false;
    private PointInfoSettingDialogListener listener = null;

    public boolean getResult()
    {
        return result;
    }

    public String getContentDesc()
    {
        return content;
    }

    public void SetListener(PointInfoSettingDialogListener _listener)
    {
        listener = _listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.addpointinfo, null, false);

        closeButton = view.findViewById(R.id.close);
        saveButton = view.findViewById(R.id.save);
        contentText = view.findViewById(R.id.editText);

        dialog.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width =  WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.BOTTOM);

        closeButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null)
                {
                    listener.OnCloseRequest();
                }

                dialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = true;
                content = contentText.getText().toString();

                if (listener != null)
                {
                    listener.OnSaveRequest(content);
                }

                dialog.dismiss();
            }
        });

        return dialog;
    }

    @Override
    public void onDestroy() {

        if (listener != null)
        {
            listener.OnDialogDestroy();
        }

        super.onDestroy();
    }
}
