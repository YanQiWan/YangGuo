package com.example.superdriver;

import android.support.v4.app.Fragment;

import com.example.fragments.RecordFragment;
import com.example.library.SingleFragmentActivity;

public class RecordActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new RecordFragment();
    }
}
