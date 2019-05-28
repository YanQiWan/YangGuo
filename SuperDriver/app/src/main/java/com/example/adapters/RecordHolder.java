package com.example.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bean.Record;
import com.example.superdriver.R;

public class RecordHolder extends RecyclerView.ViewHolder {

    @SuppressLint("SimpleDateFormat")
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Activity activity;
    private Record recordItem;

    private LinearLayout record_linearlayout1;
    private TextView record_startTime;
    private TextView record_startPlace;
    private TextView record_endTime;
    private TextView record_endPlace;

    //Add
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
//				Intent intent = OrderDetailsActivity.newIntent(activity, orderItem);
//				activity.startActivity(intent);
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
        record_startTime.setText(recordItem.getStartTime());
        record_startPlace.setText(recordItem.getStartPlace());
        record_endTime.setText(recordItem.getEndTime());
        record_endPlace.setText(recordItem.getEndPlace());
    }

}
