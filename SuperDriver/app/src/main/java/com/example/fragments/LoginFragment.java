package com.example.fragments;

import com.example.application.SysApplication;
import com.example.bean.User;
import com.example.superdriver.MainViewPagerActivity;
import com.example.superdriver.R;
import com.example.superdriver.RegistActivity;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Response;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    private EditText et_PhoneNumber;
    private EditText et_Password;
    private Button bt_login;
    private Button bt_regist;

    private CheckBox cb_remember;
    private SharedPreferences preference;

    private ProgressDialog progressDialog;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        // TODO Auto-generated method stub
        preference = getActivity().getSharedPreferences("config", Activity.MODE_PRIVATE);
        et_PhoneNumber = (EditText) view.findViewById(R.id.fragment_login_et_PhoneNumber);
        et_Password = (EditText) view.findViewById(R.id.fragment_login_et_Password);
        bt_login = (Button) view.findViewById(R.id.fragment_login_bt_login);
        bt_regist = (Button) view.findViewById(R.id.fragment_login_bt_regist);
        cb_remember = (CheckBox) view.findViewById(R.id.cb_remember);
        if(SysApplication.login_user.getPhoneNumber()!=null){
            et_PhoneNumber.setText(SysApplication.login_user.getPhoneNumber());
            et_Password.setText(SysApplication.login_user.getPassWord());
        }

        boolean ischecked = preference.getBoolean("ischecked", false);
        cb_remember.setChecked(ischecked);
        if (ischecked) {
            et_PhoneNumber.setText(preference.getString("phoneNumber", ""));
            et_Password.setText(preference.getString("passWord", ""));
        }
        bt_login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (et_PhoneNumber.length() == 0) {
                    new AlertDialog.Builder(getActivity()).setTitle("注意").setMessage("用户名为空")
                            .setPositiveButton("确定", null).show();
                } else if (et_Password.length() == 0) {
                    new AlertDialog.Builder(getActivity()).setTitle("注意").setMessage("密码不能为空")
                            .setPositiveButton("确定", null).show();
                } else{
                    progressDialog = ProgressDialog.show(getActivity(), "登录", "正在登录,请稍候！");
                    OkHttpUtils.get().addParams("phoneNumber", et_PhoneNumber.getText().toString().trim())
                            .addParams("passWord", et_Password.getText().toString().trim())
                            .url("http://39.106.196.211:8886/SuperDriver/login").id(100).build().execute(new Callback<User>(){

                        @Override
                        public void onError(Call arg0, Exception arg1, int arg2) {
                            // TODO Auto-generated method stub
                            Log.e(TAG, "Error");
                        }

                        @Override
                        public void onResponse(User response, int arg1) {
                            // TODO Auto-generated method stub
                            progressDialog.dismiss();
                            if(response!=null){
                                SysApplication.login_user = response;
                                Editor edit = preference.edit();
                                String phoneNumber = response.getPhoneNumber();
                                String passWord = response.getPassWord();
                                boolean ischecked = cb_remember.isChecked();
                                edit.putBoolean("ischecked", ischecked);
                                if (ischecked) {
                                    edit.putString("phoneNumber", phoneNumber).putString("passWord", passWord);
                                } else {
                                    edit.remove("phoneNumber").remove("passWord");
                                }
                                edit.commit();// 提交到本地

                                Intent intent = new Intent(getActivity(), MainViewPagerActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getContext(), "Failed ", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public User parseNetworkResponse(Response response, int arg1) throws Exception {
                            // TODO Auto-generated method stub
                            String string = response.body().string();
                            Log.e(TAG, string);
                            User user = new Gson().fromJson(string, User.class);
                            return user;
                        }

                    });

                }
            }

        });

        bt_regist.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getContext(), RegistActivity.class);
                startActivity(intent);
            }
        });
    }

}
