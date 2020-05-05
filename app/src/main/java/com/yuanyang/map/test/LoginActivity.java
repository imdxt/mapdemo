package com.yuanyang.map.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yuanyang.map.test.http.HttpEnumResult;
import com.yuanyang.map.test.http.HttpRequestLogin;

public class LoginActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        usernameEditText.setText("user");
        passwordEditText.setText("user");

        loginButton.setEnabled(true);
        final LoginActivity thisMain = this;

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username = usernameEditText.getText().toString();
                String passwdMd5 = MD5Utils.stringToMD5(passwordEditText.getText().toString());

                HttpRequestLogin login = new HttpRequestLogin();
                login.SetListener(new HttpRequestLogin.HttpRequestLoginListener() {
                    @Override
                    public void OnRequestLoginSuccess() {
                        DataCenter.getInstance().SetLoginUserName(username);
                        Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                        startActivityForResult(intent,111);
                    }

                    @Override
                    public void OnRequestLoginError(HttpEnumResult error) {
                        Toast.makeText( LoginActivity.this ,"账号密码错误",Toast.LENGTH_LONG).show();
                    }
                });
                login.Login(username,passwdMd5);
            }
        });
    }
}
