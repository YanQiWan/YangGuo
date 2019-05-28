package com.example.superdriver;

import android.support.v4.app.Fragment;

import com.example.fragments.RegistFragment;
import com.example.library.SingleFragmentActivity;

public class RegistActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new RegistFragment();
    }
}
