package com.example.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.application.SysApplication;
import com.example.superdriver.ChangeActivity;
import com.example.superdriver.R;
import com.example.utils.InternetUtils;
import com.example.utils.PatternUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Call;
import okhttp3.Response;

public class RegistFragment extends Fragment {
    private static final String TAG = "RegistFragment";
    EditText et_phoneNumber;
    EditText et_passWord;
    EditText et_ensure_passWord;
    Button bt_regist;
    private ProgressDialog progressDialog;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_regist, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        et_phoneNumber = (EditText) view.findViewById(R.id.fragment_regist_et_phonenumber);
        et_passWord = (EditText) view.findViewById(R.id.fragment_regist_et_password);
        et_ensure_passWord = (EditText) view.findViewById(R.id.fragment_regist_et_ensure_password);
        bt_regist = (Button) view.findViewById(R.id.fragment_regist_bt_regist);
        bt_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!InternetUtils.isNetworkConnected(getContext())) {
                    new AlertDialog.Builder(getActivity()).setTitle("注意").setMessage("网络连接异常，请检查您的网络设置!")
                            .setPositiveButton("确定", null).show();
                    return;
                }
                if (!PatternUtils.checkMobileNumber(et_phoneNumber.getText().toString().trim())) {
                    new AlertDialog.Builder(getActivity()).setTitle("注意").setMessage("手机号格式不合法，请重新输入!")
                            .setPositiveButton("确定", null).show();
                    return;
                }
                if (et_passWord.length() < 6 || et_passWord.length() > 16) {
                    new AlertDialog.Builder(getActivity()).setTitle("注意").setMessage("密码长度必须为6-16位!")
                            .setPositiveButton("确定", null).show();
                    return;
                }
                int temp = PatternUtils.checkPassWord(et_passWord.getText().toString().trim());
                if (temp != PatternUtils.VALID) {
                    if (temp == PatternUtils.HAS_SPACE) {
                        new AlertDialog.Builder(getActivity()).setTitle("注意").setMessage("密码中不能有空格!")
                                .setPositiveButton("确定", null).show();
                        return;
                    }
                    if (temp == PatternUtils.INVALID_LETTER) {
                        new AlertDialog.Builder(getActivity()).setTitle("注意").setMessage("密码仅可由数字和字母组成!")
                                .setPositiveButton("确定", null).show();
                        return;
                    }
                    if (temp == PatternUtils.HAS_WORD) {
                        new AlertDialog.Builder(getActivity()).setTitle("注意").setMessage("密码中不能有汉字!")
                                .setPositiveButton("确定", null).show();
                        return;
                    }
                    if (temp == PatternUtils.LENGTH_INVALID) {
                        new AlertDialog.Builder(getActivity()).setTitle("注意").setMessage("密码长度仅可为6-16位!")
                                .setPositiveButton("确定", null).show();
                        return;
                    }
                }
                if (!et_ensure_passWord.getText().toString().trim().equals(et_passWord.getText().toString().trim())) {
                    new AlertDialog.Builder(getActivity()).setTitle("注意").setMessage("两次输入的密码不一致!")
                            .setPositiveButton("确定", null).show();
                    return;
                }
                bt_regist.setEnabled(false);
                bt_regist.getBackground().setAlpha(0x33);
                progressDialog = ProgressDialog.show(getActivity(), "注册", "正在注册,请稍候！");
                OkHttpUtils.get().addParams("phoneNumber", et_phoneNumber.getText().toString().trim())
                        .addParams("passWord", et_passWord.getText().toString().trim())
                        .url("http://39.106.196.211:8886/SuperDriver/regist").id(100).build().execute(new Callback<String>() {

                    @Override
                    public String parseNetworkResponse(Response response, int id) throws Exception {
                        String result = response.body().string();
                        Log.e("Like", result);
                        return result;
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        // TODO Auto-generated method stub
                        Log.e(TAG, "Error");
                        progressDialog.dismiss();
                        bt_regist.setEnabled(true);
                        bt_regist.getBackground().setAlpha(0xff);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        progressDialog.dismiss();
                        bt_regist.setEnabled(true);
                        bt_regist.getBackground().setAlpha(0xff);
                        if (response.equals("true")) {
                            SysApplication.login_user.setPhoneNumber(et_phoneNumber.getText().toString().trim());
                            SysApplication.login_user.setPassWord(et_passWord.getText().toString().trim());
                            Toast.makeText(getContext(), "注册成功，即将跳转到修改信息界面", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), ChangeActivity.class);
                            startActivity(intent);
                        } else if (response.equals("repeat")) {
                            new AlertDialog.Builder(getActivity()).setTitle("注意").setMessage("该账号已被注册过!")
                                    .setPositiveButton("确定", null).show();
                        } else {
                            new AlertDialog.Builder(getActivity()).setTitle("注意").setMessage("服务器内部错误!")
                                    .setPositiveButton("确定", null).show();
                            return;
                        }
                    }

                });

            }
        });
    }
}
