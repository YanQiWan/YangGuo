package com.example.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.application.SysApplication;
import com.example.library.ActionSheetDialog;
import com.example.library.CircleImageView;
import com.example.superdriver.MainViewPagerActivity;
import com.example.superdriver.R;
import com.example.utils.BitmapUtils;
import com.example.utils.InternetUtils;
import com.example.utils.LocalFileUtils;
import com.example.utils.PatternUtils;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;
import java.io.File;
import okhttp3.Call;

public class ChangeFragment extends Fragment {
    private static final String TAG = "ChangeFragment";
    private Spinner sp_sex;
    private Spinner sp_place;
    private CircleImageView civ_headImage;
    private ArrayAdapter<String> sex_adapter;
    private ArrayAdapter<String> place_adapter;
    private String sex[];
    private String place[] = null;
    private EditText et_name;
    private EditText et_email;
    private EditText et_brief_instruction;
    private EditText et_age;
    private EditText et_vehicle_type;
    private Button bt_ensure;

    private ProgressDialog progressDialog;
    private ProgressDialog headProgressDialog;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change, null);
        initView(view);
        return view;
    }


    private void initView(View view) {
        Log.e(TAG, SysApplication.login_user.getUserProvince()+" "+SysApplication.login_user.getUserSex());
        civ_headImage = (CircleImageView) view.findViewById(R.id.fragment_change_civ_head_image);
        civ_headImage.setImageResource(R.drawable.change_default_head);
        et_name = (EditText) view.findViewById(R.id.fragment_change_et_username);
        if (SysApplication.login_user.getUserName() != null) {
            et_name.setText(SysApplication.login_user.getUserName());
        }
        et_email = (EditText) view.findViewById(R.id.fragment_change_et_email);
        if (SysApplication.login_user.getUserEmail() != null) {
            et_email.setText(SysApplication.login_user.getUserEmail());
        }
        et_brief_instruction = (EditText) view.findViewById(R.id.fragment_change_et_introduction);
        if (SysApplication.login_user.getUserIntroduction() != null) {
            et_brief_instruction.setText(SysApplication.login_user.getUserIntroduction());
        }
        et_age = (EditText) view.findViewById(R.id.fragment_change_et_age);
        et_age.setText(SysApplication.login_user.getUserAge()+"");

        et_vehicle_type = (EditText)view.findViewById(R.id.fragment_change_et_vehicle_type);
        if (SysApplication.login_user.getVehicleType() != null) {
            et_vehicle_type.setText(SysApplication.login_user.getVehicleType());
        }

        sp_sex = (Spinner) view.findViewById(R.id.fragment_change_sp_sex);
        sp_place = (Spinner) view.findViewById(R.id.fragment_change_sp_place);

        if (SysApplication.login_user.getUserHeadImage() != null) {
            Log.e(TAG, SysApplication.login_user.getUserHeadImage());
            String localPath = LocalFileUtils.getSDPath()+"/SuperDriver/"+SysApplication.login_user.getUserHeadImage();
            File file = new File(localPath);
            if(file.exists()){
                civ_headImage.setImageBitmap(BitmapUtils.getScaledBitmap(localPath, getActivity()));
            }else{
                downloadHeadImage();
            }

        }
        civ_headImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new ActionSheetDialog(getActivity()).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                        .addSheetItem("拍照", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, BitmapUtils.REQUEST_TAKE_PHOTO);
                            }
                        }).addSheetItem("从相册中获取", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {

                    @Override
                    public void onClick(int which) {
                        // TODO Auto-generated method stub
                        Toast.makeText(getActivity(), "从相册中获取", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, BitmapUtils.REQUEST_CHOOSE_PHOTO);
                    }
                }).show();
            }
        });
        bt_ensure = (Button) view.findViewById(R.id.fragment_change_bt_ensure);
        bt_ensure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!InternetUtils.isNetworkConnected(getContext())) {
                    new AlertDialog.Builder(getActivity()).setTitle("注意").setMessage("网络连接异常，请检查您的网络设置!")
                            .setPositiveButton("确定", null).show();
                    return;
                }
                if(!PatternUtils.checkEmail(et_email.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "邮箱格式不合法，请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog = ProgressDialog.show(getActivity(), "修改信息", "正在修改信息,请稍候！");
                SysApplication.login_user.setUserIntroduction(et_brief_instruction.getText().toString().trim());
                SysApplication.login_user.setUserName(et_name.getText().toString().trim());
                SysApplication.login_user.setUserEmail(et_email.getText().toString().trim());
                SysApplication.login_user.setUserHeadImage(SysApplication.login_user.getPhoneNumber() + "_headImage.jpg");
                SysApplication.login_user.setUserAge(Integer.parseInt(et_age.getText().toString()));
                SysApplication.login_user.setVehicleType(et_vehicle_type.getText().toString().trim());
                bt_ensure.setEnabled(false);
                bt_ensure.getBackground().setAlpha(0x33);
                OkHttpUtils.postString().url("http://39.106.196.211:8886/SuperDriver/change")

                        .content(new Gson().toJson(SysApplication.login_user)).build().execute(new StringCallback() {

                    @Override
                    public void onResponse(String arg0, int arg1) {
                        // TODO Auto-generated method stub
                        Log.e("Like", arg0);
                        if (arg0.equals("true"))
                        {
                            progressDialog.dismiss();
                            headProgressDialog = ProgressDialog.show(getActivity(), "提示", "修改成功，正在上传头像,请稍候！");
                            uploadHeadImage();
                        }
                        else if (arg0.equals("false"))
                        {
                            Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                            bt_ensure.setEnabled(true);
                            bt_ensure.getBackground().setAlpha(0xFF);
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        // TODO Auto-generated method stub
                        Log.e(TAG, arg1.getMessage());
                        Toast.makeText(getContext(), "修改失败!" + arg1.getMessage(), Toast.LENGTH_SHORT).show();
                        bt_ensure.setEnabled(true);
                        bt_ensure.getBackground().setAlpha(0xFF);
                        progressDialog.dismiss();
                    }
                });
            }
        });

        sex = getResources().getStringArray(R.array.change_fragment_sex_spinner);
        place = getResources().getStringArray(R.array.change_fragment_place_spinner);

        sex_adapter = new ArrayAdapter<String>(getActivity(), R.layout.tipsprice_spinner, sex) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return setCentered(super.getView(position, convertView, parent));
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                return setCentered(super.getDropDownView(position, convertView, parent));
            }

            private View setCentered(View view) {
                return view;
            }
        };
        sex_adapter.setDropDownViewResource(R.layout.tipsprice_spinner);// 设置下拉列表的风格
        sp_sex.setAdapter(sex_adapter);// 将adapter2 添加到spinner中
        sp_sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                SysApplication.login_user.setUserSex(sex[position]);
                if(view==null)
                    return;
                TextView tv = (TextView) view;
                tv.setTextColor(Color.WHITE);
                tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                sp_sex.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        }); // 添加事件Spinner事件监听
        sp_sex.setVisibility(View.VISIBLE);// 设置默认值
        String sex = SysApplication.login_user.getUserSex();
        if(sex!=null){
            if(sex.equals("女")){
                sp_sex.setSelection(1, true);
            }
        }else {
            sp_sex.setSelection(0, true);
        }
        place_adapter = new ArrayAdapter<String>(getActivity(), R.layout.tipsprice_spinner, place) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return setCentered(super.getView(position, convertView, parent));
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                return setCentered(super.getDropDownView(position, convertView, parent));
            }

            private View setCentered(View view) {
                return view;
            }
        };
        place_adapter.setDropDownViewResource(R.layout.tipsprice_spinner);// 设置下拉列表的风格
        sp_place.setAdapter(place_adapter);// 将adapter2 添加到spinner中
        sp_place.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                SysApplication.login_user.setUserProvince(place[position]);
                if(view == null)
                    return;
                TextView tv = (TextView) view;
                tv.setTextColor(Color.WHITE);
                tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                sp_place.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }

        }); // 添加事件Spinner事件监听
        sp_place.setVisibility(View.VISIBLE);// 设置默认值
        String province = SysApplication.login_user.getUserProvince();
        if(SysApplication.login_user.getUserProvince()!=null){
            for(int i = 0;i<place.length;i++){
                if(place[i].equals(province)){
                    sp_place.setSelection(i, true);
                }
            }
        }else{
            sp_place.setSelection(0, true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, resultCode+" ");
        // TODO Auto-generated method stub
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case BitmapUtils.REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(getActivity(), "取消了拍照", Toast.LENGTH_LONG).show();
                    return;
                }
                Bitmap photo = data.getParcelableExtra("data");
                civ_headImage.setImageBitmap(photo);
                break;
            case BitmapUtils.REQUEST_CHOOSE_PHOTO:
                if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(getActivity(), "取消了选择", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    Uri imageUri = data.getData();
                    SysApplication.login_user.setUserHeadImage(imageUri.toString());
                    Log.e(TAG, imageUri+"");
                    civ_headImage.setImageURI(imageUri);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    void uploadHeadImage() {

        byte[] filebyte = BitmapUtils.getPicture(civ_headImage.getDrawable());
        String filename = SysApplication.login_user.getPhoneNumber() + "_headImage.jpg";
        Log.e(TAG, filename);
        File file = LocalFileUtils.createFileWithByte(filebyte, LocalFileUtils.getSDPath() + "/SuperDriver", filename);

        if (!file.exists()) {
            Log.e(TAG, "文件不存在");
            Toast.makeText(getActivity(), "file not exists", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e(TAG, file.getAbsolutePath());
        OkHttpUtils.post().addFile("file", filename, file).url("http://39.106.196.211:8886/SuperDriver/upload").addParams("filepath","headImage").build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        // TODO Auto-generated method stub
                        Log.e(TAG, "Error:" + arg1.getMessage());
                        bt_ensure.setEnabled(true);
                        bt_ensure.getBackground().setAlpha(0xFF);
                        headProgressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String arg0, int arg1) {
                        // TODO Auto-generated method stub
                        Log.e("TAG", arg0);
                        if(arg0.equals("true")){
                            Toast.makeText(getContext(), "头像上传成功", Toast.LENGTH_SHORT).show();
                            bt_ensure.setEnabled(true);
                            bt_ensure.getBackground().setAlpha(0xFF);
                            Intent intent = new Intent(getActivity(), MainViewPagerActivity.class);
                            startActivity(intent);
                            headProgressDialog.dismiss();
                        }else{
                            bt_ensure.setEnabled(true);
                            bt_ensure.getBackground().setAlpha(0xFF);
                            headProgressDialog.dismiss();
                            Log.e(TAG,"头像上传失败");
                        }

                    }

                });
    }

    void downloadHeadImage() {
        headProgressDialog = ProgressDialog.show(getActivity(), "提示", "正在加载头像,请稍候！");
        String path = SysApplication.login_user.getUserHeadImage();
        if (path == null){
            Log.e(TAG, "用户未设置头像，采用默认头像");
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
                        civ_headImage.setImageBitmap(BitmapUtils.getScaledBitmap(arg0.getAbsolutePath(), getActivity()));
                        headProgressDialog.dismiss();
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        // TODO Auto-generated method stub
                        Log.e("Like", "onError:" + arg1.getMessage());
                        headProgressDialog.dismiss();
                    }
                });
    }
}
