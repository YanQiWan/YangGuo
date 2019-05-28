package com.example.superdriver;

import android.support.v4.app.Fragment;

import com.example.fragments.ChangeFragment;
import com.example.library.SingleFragmentActivity;

public class ChangeActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new ChangeFragment();
    }
}
