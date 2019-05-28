
package com.example.adapters;

import java.util.List;

import com.example.bean.Record;
import com.example.superdriver.R;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RecordItemAdapter extends RecyclerView.Adapter<RecordHolder>{
	private static final String TAG = "RecordItemAdapter";
	private List<Record> mRecordItems;
	private Activity activity;
	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return mRecordItems.size();
	}
	public RecordItemAdapter(List<Record> RecordItems, Activity activity) {
		// TODO Auto-generated constructor stub
		this.mRecordItems = RecordItems;
		this.activity = activity;
		Log.i(TAG, "Background thread started");
	}
	
	@Override
	public void onBindViewHolder(RecordHolder recordItemHolder, int position) {
		// TODO Auto-generated method stub
		Record recordItem = mRecordItems.get(position);
		recordItemHolder.bindRecordItem(recordItem);
		recordItemHolder.bindActivity(activity);
	}

	@Override
	public RecordHolder onCreateViewHolder(ViewGroup viewgroup, int viewType) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(activity);
		View view = inflater.inflate(R.layout.record_item, viewgroup, false);
		return new RecordHolder(view);
	}
	
}

