package com.example.superdriver;

import android.support.v4.app.Fragment;

import com.example.fragments.RecordListFragment;
import com.example.library.SingleFragmentActivity;

public class RecordListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new RecordListFragment();
    }
}
