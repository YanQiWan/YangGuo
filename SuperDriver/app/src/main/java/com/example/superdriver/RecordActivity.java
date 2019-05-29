package com.example.superdriver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.example.bean.Record;
import com.example.fragments.RecordFragment;
import com.example.library.SingleFragmentActivity;

public class RecordActivity extends SingleFragmentActivity {
    private static final String ORDER_EXTRA = "RecordActivity";
    @Override
    protected Fragment createFragment() {
        Record orderItem = (Record) getIntent().getSerializableExtra(ORDER_EXTRA);
        return RecordFragment.newInstance(orderItem);
    }

    public static Intent newIntent(Context packageContext, Record vItem){
        Intent intent = new Intent(packageContext, RecordActivity.class);
        intent.putExtra(ORDER_EXTRA, vItem);
        return intent;
    }
}
