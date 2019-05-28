package com.example.library;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SingleViewPagerFragment extends Fragment {

	// TabLayout
	protected TabLayout mTabLayout;
	// ViewPager
	protected ViewPager mViewPager;
	// Title
	protected List<String> mTitle;
	// Fragment
	protected List<Fragment> mFragment;
	// View
	protected View view;

	private int layout;
	
	@SuppressLint("ValidFragment")
	public SingleViewPagerFragment(int layout) {
		// TODO Auto-generated constructor stub
		this.layout = layout;
	}
	
	public SingleViewPagerFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	/*
	 */
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null) {
				parent.removeView(view);
			}
			return view;
		}
		view = inflater.inflate(layout, null);

		initData();
		initView(view);
		return view;
	}
	
	protected void initData() {

		mTitle = new ArrayList<String>();
		mFragment = new ArrayList<Fragment>();
	}

	/*
	 */
	protected void initView(View v) {

	}

	public static void setIndicator(Context context, TabLayout tabs, int leftDip, int rightDip) {
		Class<?> tabLayout = tabs.getClass();
		Field tabStrip = null;
		try {
			tabStrip = tabLayout.getDeclaredField("mTabStrip");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

		tabStrip.setAccessible(true);
		LinearLayout ll_tab = null;
		try {
			ll_tab = (LinearLayout) tabStrip.get(tabs);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		int left = (int) (getDisplayMetrics(context).density * leftDip);
		int right = (int) (getDisplayMetrics(context).density * rightDip);

		for (int i = 0; i < ll_tab.getChildCount(); i++) {
			View child = ll_tab.getChildAt(i);
			child.setPadding(0, 0, 0, 0);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,
					1);
			params.leftMargin = left;
			params.rightMargin = right;
			child.setLayoutParams(params);
			child.invalidate();
		}
	}

	public static DisplayMetrics getDisplayMetrics(Context context) {
		DisplayMetrics metric = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric;
	}

	public static float getPXfromDP(float value, Context context) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
				context.getResources().getDisplayMetrics());
	}

	protected void reflex(final TabLayout tabLayout) {
		tabLayout.post(new Runnable() {
			@Override
			public void run() {
				try {
					LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);

					int dp10 = dip2px(tabLayout.getContext(), 10);

					for (int i = 0; i < mTabStrip.getChildCount(); i++) {
						View tabView = mTabStrip.getChildAt(i);

						Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
						mTextViewField.setAccessible(true);

						TextView mTextView = (TextView) mTextViewField.get(tabView);

						tabView.setPadding(0, 0, 0, 0);

						int width = 0;
						width = mTextView.getWidth();
						if (width == 0) {
							mTextView.measure(0, 0);
							width = mTextView.getMeasuredWidth();
						}

						LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
						params.width = width;
						params.leftMargin = dp10;
						params.rightMargin = dp10;
						tabView.setLayoutParams(params);

						tabView.invalidate();
					}

				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		});

	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
