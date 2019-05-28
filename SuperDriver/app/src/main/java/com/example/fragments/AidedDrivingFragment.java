package com.example.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.superdriver.DetectorActivity;
import com.example.superdriver.R;

public class AidedDrivingFragment extends android.support.v4.app.Fragment {
    Button bt_start;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aideddriving, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        bt_start = (Button)view.findViewById(R.id.aideddriving_bt_start);
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DetectorActivity.class);
                startActivity(intent);
            }
        });
    }
}
