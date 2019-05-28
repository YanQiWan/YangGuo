package com.example.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.config.ALGORITHM;
import com.example.library.SingleRecyclerViewFragment;
import com.example.superdriver.R;

public class RecordFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_record_fl);
        if (fragment == null) {
            fragment = new SingleRecyclerViewFragment(ALGORITHM.newRecords.value(), null);
            fm.beginTransaction().add(R.id.fragment_record_fl, fragment).commit();
        }
    }
}
