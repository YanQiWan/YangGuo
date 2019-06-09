package com.example.fragments;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.superdriver.R;

public class SiriFragment extends Fragment {
    private MediaPlayer mediaPlayer;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_siri, null);
        initView(view);
        mediaPlayer = new MediaPlayer();
        return view;
    }

    private void initView(View view) {
        Button bt_play = (Button)view.findViewById(R.id.fragment_siri_bt_play);
        bt_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mediaPlayer.isPlaying()){
                    mediaPlayer = MediaPlayer.create(getActivity(), R.raw.front_bicycle);
                    //用prepare方法，会报错误java.lang.IllegalStateException //mediaPlayer.prepare();
                    mediaPlayer.start();
                }
            }
        });
    }
}
