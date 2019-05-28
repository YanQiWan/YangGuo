package com.example.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.superdriver.R;
import com.example.superdriver.SiriActivity;

public class SiriFragmentTest extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_siri_test, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        Button bt = (Button)view.findViewById(R.id.fragment_siri_bt_test);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SiriActivity.class);
                startActivity(intent);
            }
        });
    }
}

