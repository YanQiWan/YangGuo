package com.example.superdriver;


import com.example.application.SysApplication;
import com.example.fragments.MainViewPagerFragment;
import com.example.library.SingleFragmentActivity;

import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.widget.Toast;

public class MainViewPagerActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		return new MainViewPagerFragment(R.layout.fragment_main);
	}

	double exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000)
			{
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				SysApplication.getInstance().exit();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
