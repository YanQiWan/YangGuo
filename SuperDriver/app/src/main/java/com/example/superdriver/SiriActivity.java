package com.example.superdriver;

import android.support.v4.app.Fragment;

import com.example.fragments.SiriFragment;
import com.example.library.SingleFragmentActivity;

public class SiriActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new SiriFragment();
    }
}
