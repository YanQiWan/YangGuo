package com.example.adapters;

import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import com.example.superdriver.R;
public class MainPagerAdapter extends FragmentPagerAdapter {
	View view;
	Context context;
	private List<Fragment> mFragments;
	private List<String> mTitles;
	private int[] mapId = { R.drawable.main_tab1_normal, R.drawable.main_tab2_normal, R.drawable.main_tab3_normal, R.drawable.main_tab4_normal };

	public MainPagerAdapter(FragmentManager fm, List<Fragment> mFragment, List<String> mTitles, Context context) {
		super(fm);
		this.mFragments = mFragment;
		this.mTitles = mTitles;
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	public View getTabView(int position) {
		View v = LayoutInflater.from(context).inflate(R.layout.bottom_bar_item, null);
		view = (View) v.findViewById(R.id.bottom_view);
		view.setBackgroundResource(mapId[position]);
		return v;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mTitles.size();
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return mFragments.get(arg0);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mTitles.get(position);
	}
}
