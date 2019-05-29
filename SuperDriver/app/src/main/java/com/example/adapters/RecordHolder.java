package com.example.adapters;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bean.Record;
import com.example.library.SingleFragmentActivity;
import com.example.superdriver.R;
import com.example.superdriver.RecordActivity;
import com.example.utils.DateUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import okhttp3.Call;

public class RecordHolder extends RecyclerView.ViewHolder {

    private Activity activity;
    private Record recordItem;

    private LinearLayout record_linearlayout1;
    private TextView record_startTime;
    private TextView record_startPlace;
    private TextView record_endTime;
    private TextView record_endPlace;

    private ProgressDialog progressDialog;

    public RecordHolder(View itemView) {
        super(itemView);
        // TODO Auto-generated constructor stub
        record_linearlayout1 = (LinearLayout) itemView.findViewById(R.id.record_linearlayout1);
        record_startTime = (TextView) itemView.findViewById(R.id.record_tv_startTime);
        record_startPlace = (TextView) itemView.findViewById(R.id.record_tv_startPlace);
        record_endTime = (TextView) itemView.findViewById(R.id.record_tv_endTime);
        record_endPlace = (TextView) itemView.findViewById(R.id.record_tv_endPlace);
        record_linearlayout1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                downloadRoute();
            }
        });
    }

    public void bindActivity(Activity activity) {
        if (activity != null)
            this.activity = activity;
    }


    public Record getVideoItem() {
        return recordItem;
    }

    public void bindRecordItem(final Record recordItem) {
        this.recordItem = recordItem;
        record_startPlace.setText(recordItem.getStartPlace());
        record_endPlace.setText(recordItem.getEndPlace());
        try {
            record_startTime.setText(DateUtils.sdf.format(DateUtils.sdf_GMT.parse(recordItem.getStartTime())));
            record_endTime.setText(DateUtils.sdf.format(DateUtils.sdf_GMT.parse(recordItem.getEndTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    void downloadRoute() {
        String path = recordItem.getRecordId() + ".txt";
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SuperDriver/" + path);
        if (file.exists()) {
            Intent intent = RecordActivity.newIntent(activity, recordItem);
            activity.startActivity(intent);
            return;
        }
        progressDialog = ProgressDialog.show(activity, "提示", "正在加载头像,请稍候！");
        OkHttpUtils.get()
                .url("http://39.106.196.211:8886/SuperDriver/download/route/"
                        + path)
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
                        Intent intent = RecordActivity.newIntent(activity, recordItem);
                        activity.startActivity(intent);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        // TODO Auto-generated method stub
                        Log.e("Like", "onError:" + arg1.getMessage());
                        new AlertDialog.Builder(activity).setTitle("注意").setMessage("下载文件失败")
                                .setPositiveButton("确定", null).show();
                        progressDialog.dismiss();
                    }
                });
    }
}
