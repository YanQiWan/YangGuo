package com.example.service;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.application.SysApplication;
import com.example.bean.Record;
import com.example.superdriver.LoginActivity;
import com.example.superdriver.R;
import com.example.utils.BitmapUtils;
import com.example.utils.LocalFileUtils;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;

import okhttp3.Call;
import okhttp3.Request;

public class UpdateRouteService extends Service {
    private final static String TAG = "UpdateRouteService";
    private Record record;
    private File tempFile;

    private final int NotificationID = 0x10000;
    private NotificationManager mNotificationManager = null;
    private NotificationCompat.Builder builder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("onStartCommand");
        try {
            record = (Record) intent.getSerializableExtra("record");
            String url = LocalFileUtils.getSDPath()+"/SuperDriver/"+record.getRecordId()+".txt";
            Log.e(TAG, url);
            tempFile = new File(url);
            uploadRoute();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     *
     * @param x
     *            当前值
     * @param total
     *            总值 [url=home.php?mod=space&uid=7300]@return[/url] 当前百分比
     * @Description:返回百分之值
     */
    private String getPercent(int x, int total) {
        String result = "";// 接受百分比的值
        double x_double = x * 1.0;
        double tempresult = x_double / total;
        // 百分比格式，后面不足2位的用0补齐 ##.00%
        DecimalFormat df1 = new DecimalFormat("0.00%");
        result = df1.format(tempresult);
        return result;
    }

    /**
     * 上传图片到数据库
     * @throws UnsupportedEncodingException
     */
    public void uploadRoute() throws UnsupportedEncodingException {
        OkHttpUtils.postString().url("http://39.106.196.211:8886/SuperDriver/addRecord")

                .content(new Gson().toJson(record)).build().execute(new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                // TODO Auto-generated method stub
                Log.e(TAG, arg0);
                if (arg0.equals("true"))
                {
                    Log.e(TAG,"上传路径成功，正在上传路径文件,请稍候！");
                    uploadRouteFile();
                }
                else if (arg0.equals("false"))
                {
                    Log.e(TAG,"上传路径失败！");
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                // TODO Auto-generated method stub
                Log.e(TAG, arg1.getMessage());
                Log.e(TAG,"修改失败!" + arg1.getMessage());
            }
        });

    }

    private void uploadRouteFile(){

        if (!tempFile.exists()) {
            Log.e(TAG, "文件不存在");
            return;
        }
        OkHttpUtils.post().addFile("file", record.getRecordId()+".txt", tempFile).url("http://39.106.196.211:8886/SuperDriver/upload").addParams("filepath","route").build()
                .execute(new StringCallback() {

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        System.out.println("开始上传文件");
                        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        builder = new NotificationCompat.Builder(getApplicationContext());
                        builder.setSmallIcon(R.drawable.regist_logo);
                        builder.setContentText("正在上传,请稍后...");
                        builder.setNumber(0);
                        builder.setAutoCancel(true);
                        mNotificationManager.notify(NotificationID, builder.build());
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        int x = Float.valueOf(progress).intValue();
                        int totalS = Long.valueOf(total).intValue();
                        builder.setProgress(totalS, x, false);
                        builder.setContentInfo(getPercent(x, totalS));
                        mNotificationManager.notify(NotificationID, builder.build());
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        // TODO Auto-generated method stub
                        Log.e(TAG, "Error:" + arg1.getMessage());
                        mNotificationManager.cancel(NotificationID);
                    }

                    @Override
                    public void onResponse(String arg0, int arg1) {
                        // TODO Auto-generated method stub
                        Log.e("TAG", arg0);
                        if(arg0.equals("true")){
                            Log.e(TAG,"路线上传成功");
                            builder.setContentText("上传完成");
                            mNotificationManager.notify(NotificationID, builder.build());
                            // 震动提示
                            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000L);// 参数是震动时间(long类型)
                            stopSelf();
                            mNotificationManager.cancel(NotificationID);
                        }else{
                            Log.e(TAG,"路线上传失败");
                            mNotificationManager.cancel(NotificationID);
                        }

                    }

                });
    }
}
