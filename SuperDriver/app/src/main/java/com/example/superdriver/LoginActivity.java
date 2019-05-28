package com.example.superdriver;

import android.support.v4.app.Fragment;

import com.example.fragments.LoginFragment;
import com.example.library.SingleFragmentActivity;

public class LoginActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }
}
