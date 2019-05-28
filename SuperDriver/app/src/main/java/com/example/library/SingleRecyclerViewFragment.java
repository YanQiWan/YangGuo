package com.example.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.adapters.RecordItemAdapter;
import com.example.application.SysApplication;
import com.example.bean.Record;
import com.example.superdriver.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Response;

public class SingleRecyclerViewFragment extends Fragment{
	
	private static final String TAG = "RecyclerViewFragment";
	private RecyclerView mRecordRecyclerView;
	private List<Record> mRecordList;
	private RecordItemAdapter mRecordItemAdapter;
	private Object obj;
	private int algorithm;
	private View v;

	@SuppressLint("ValidFragment")
	public SingleRecyclerViewFragment(int algorithm, Object obj) {
		// TODO Auto-generated constructor stub
		this.algorithm = algorithm;
		this.obj = obj;
	}
	
	public  SingleRecyclerViewFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		if(mRecordList==null)
		{
			mRecordList = new ArrayList<Record>();
			getData();
		}
	}
	
	private void getData() {
		// TODO Auto-generated method stub
		String url = "http://39.106.196.211:8886/SuperDriver/getRecordList"; //url设置成自己的json-server地址
		Map<String, String> params = new HashMap<String, String>();
		Log.e(TAG, algorithm+"");
		params.put("algorithm", algorithm+"");
		params.put("phoneNumber", SysApplication.login_user.getPhoneNumber());
        OkHttpUtils
                .get()//
                .params(params)
                .url(url)//
                .build()//
                .execute(new Callback<List<Record>>() //使用ListUserCallback回调List<Bean>数组，可自己定义JavaBean对象
                {
                	@Override
                    public void onError(Call call, Exception e, int id)
                    {
                    	Toast.makeText(getContext(), "onError:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    	Log.e("Like", "onError:" + e.getMessage());
                        //mTv为TextView打印Error信息
                    }

                    @Override
                    public void onResponse(List<Record> response, int id) {
                        //Toast.makeText(getContext(), "onResponse:" + response.get(0).toString(), Toast.LENGTH_SHORT).show();
                        mRecordList = response;
                        setupAdapter();
                    }


					@Override
					public List<Record> parseNetworkResponse(Response response, int arg1) throws Exception {
						// TODO Auto-generated method stub
						String string = response.body().string();
						Log.e(TAG, string);
				        List<Record> records =new Gson().fromJson(string, new TypeToken<List<Record>>(){}.getType());
				        Log.e(TAG, records.size()+"");
				        return records;
					}

					
                });
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(v==null){
			v = inflater.inflate(R.layout.fragment_recycler_view, container, false);

			mRecordRecyclerView = (RecyclerView)v.findViewById(R.id.recycler_view);
            mRecordRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));//设置表格格式
				    
			//设置分隔条
            mRecordRecyclerView.addItemDecoration(new RecyclerViewDivider(
				    getContext(), LinearLayoutManager.VERTICAL, R.drawable.divider_transparent));
			setupAdapter();
		}
		
	
		return v;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e(TAG, "Background thread destroyed");
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		Log.e(TAG, "clearQueue");
	}
	
	private void setupAdapter(){
		if(isAdded()){
			mRecordItemAdapter = new RecordItemAdapter(mRecordList, getActivity());
			mRecordRecyclerView.setAdapter(mRecordItemAdapter);
		}
	}
}
