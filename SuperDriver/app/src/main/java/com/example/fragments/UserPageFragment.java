package com.example.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.application.SysApplication;
import com.example.library.CircleImageView;
import com.example.superdriver.ChangeActivity;
import com.example.superdriver.R;
import com.example.superdriver.RecordActivity;
import com.example.utils.BitmapUtils;
import com.example.utils.LocalFileUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;

public class UserPageFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "UserPageFragment";
    private CircleImageView civ_head;
    private TextView tv_userName;
    private TextView tv_introduction;
    private TextView tv_age;
    private TextView tv_email;
    private TextView tv_vehicle_type;
    private TextView tv_province;
    private TextView tv_record;
    private TextView tv_habit;
    private Button bt_edit;
    private ProgressDialog progressDialog;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userpage, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        civ_head = (CircleImageView)view.findViewById(R.id.fragment_user_page_civ_head_image);
        tv_userName = (TextView)view.findViewById(R.id.fragment_user_page_tv_username);
        tv_introduction = (TextView)view.findViewById(R.id.fragment_user_page_tv_introduction);
        tv_age = (TextView)view.findViewById(R.id.fragment_user_page_tv_age);
        tv_email = (TextView)view.findViewById(R.id.fragment_user_page_tv_email);
        tv_vehicle_type = (TextView)view.findViewById(R.id.fragment_user_page_tv_vehicle_type);
        tv_province = (TextView)view.findViewById(R.id.fragment_user_page_tv_province);
        tv_record = (TextView)view.findViewById(R.id.fragment_user_page_tv_record);
        tv_habit = (TextView)view.findViewById(R.id.fragment_user_page_tv_habit);
        bt_edit = (Button)view.findViewById(R.id.fragment_userpage_bt_edit);

        tv_userName.setText(SysApplication.login_user.getUserName());
        tv_introduction.setText(SysApplication.login_user.getUserIntroduction());
        tv_age.setText(SysApplication.login_user.getUserAge()+"");
        tv_email.setText(SysApplication.login_user.getUserEmail());
        tv_vehicle_type.setText(SysApplication.login_user.getVehicleType());
        tv_province.setText(SysApplication.login_user.getUserProvince());
        tv_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RecordActivity.class);
                startActivity(intent);
            }
        });
        tv_habit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChangeActivity.class);
                startActivity(intent);
            }
        });

        if(SysApplication.login_user.getUserHeadImage()!=null){

            String localPath = LocalFileUtils.getSDPath() + "/SuperDriver/" + SysApplication.login_user.getUserHeadImage();
            Log.e(TAG, localPath);
            File file = new File(localPath);
            if (!file.exists()) {
                // 连接服务器下载
                progressDialog = ProgressDialog.show(getActivity(), "", "正在获取头像,请稍候！");
                downloadHeadImage();
            } else {
                // 从本地获取
                civ_head.setImageBitmap(BitmapUtils.getScaledBitmap(localPath, getActivity()));
            }
        }else{
            civ_head.setImageResource(R.drawable.change_default_head);
        }

    }


    void downloadHeadImage() {
        String path = SysApplication.login_user.getUserHeadImage();
        if (path == null){
            Log.e(TAG, "用户未设置头像，采用默认头像");
            civ_head.setImageResource(R.drawable.change_default_head);
            return;
        }
        OkHttpUtils.get()
                .url("http://39.106.196.211:8886/SuperDriver/download/headImage/"
                        + SysApplication.login_user.getUserHeadImage())
                .id(100).build()
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SuperDriver",
                        path) {

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        // TODO Auto-generated method stub
                        Log.e("Like", "inProgress:" + (int) (100 * progress));
                    }

                    @Override
                    public void onResponse(File arg0, int arg1) {
                        // TODO Auto-generated method stub
                        civ_head.setImageBitmap(BitmapUtils.getScaledBitmap(arg0.getAbsolutePath(), getActivity()));
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        // TODO Auto-generated method stub
                        Log.e("Like", "onError:" + arg1.getMessage());
                        progressDialog.dismiss();
                    }
                });
    }
}
