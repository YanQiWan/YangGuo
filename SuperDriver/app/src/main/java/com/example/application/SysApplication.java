package com.example.application;

import java.util.LinkedList;
import java.util.List;
import com.example.bean.User;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class SysApplication extends Application {

	
	public static User login_user = null;
	// 运用list来保存们每一个activity是关键
	private List<Activity> mList = new LinkedList<Activity>();
	// 为了实现每次使用该类时不创建新的对象而创建的静态对象
	private static SysApplication instance;

	public static int Screen_px_width = 0;
	public static int Screen_px_height = 0;
	// 构造方法
	private SysApplication() {
		login_user = new User();
	}

	// 实例化一次
	public synchronized static SysApplication getInstance() {
		if (null == instance) {
			instance = new SysApplication();
		}
		return instance;
	}

	// add Activity
	public void addActivity(Activity activity) {
		mList.add(activity);
	}

	// 关闭每一个list内的activity
	public void exit() {
		try {
			for (Activity activity : mList) {
				if (activity != null)
					activity.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
			this.onLowMemory();
		}
	}

	// 杀进程
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}
	
}
