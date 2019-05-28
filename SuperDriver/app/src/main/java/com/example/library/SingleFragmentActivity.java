package com.example.library;

import com.baidu.mapapi.SDKInitializer;
import com.example.application.SysApplication;
import com.example.superdriver.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS;


public abstract class SingleFragmentActivity extends AppCompatActivity {
	public static int OVERLAY_PERMISSION_REQ_CODE = 1234;
	protected abstract Fragment createFragment();

	public String[] getRequiredPermissions() {
		Activity activity = this;
		try {
			PackageInfo info =
					activity
							.getPackageManager()
							.getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS);
			String[] ps = info.requestedPermissions;
			if (ps != null && ps.length > 0) {
				return ps;
			} else {
				return new String[0];
			}
		} catch (Exception e) {
			return new String[0];
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestDrawOverLays();
		permissionAsk();
		supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if (Build.VERSION.SDK_INT >= 21) {
			View decorView = getWindow().getDecorView();
			decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
			getWindow().setStatusBarColor(Color.TRANSPARENT);
		}
		setContentView(R.layout.activity_fragment);

		SysApplication.getInstance().addActivity(this);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
				| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragment_container);

		if (fragment == null) {
			fragment = createFragment();
			fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
		}

		SDKInitializer.initialize(getApplicationContext());//百度地图初始化

		if(SysApplication.Screen_px_height==0){
			WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics dm = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(dm);
			SysApplication.Screen_px_width = dm.widthPixels;         // 屏幕宽度（像素）
			SysApplication.Screen_px_height = dm.heightPixels;       // 屏幕高度（像素）
			Log.e("Lin",SysApplication.Screen_px_width+"");
			Log.e("Lin",SysApplication.Screen_px_height+"");
		}//屏幕像素初始化

	}

	private void permissionAsk() {
		boolean flag = true;
		final String[] permissions = getRequiredPermissions();
		for (int i = 1; i < permissions.length; i++) {
			if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
			    if(!permissions[i].equals("android.permission.SYSTEM_ALERT_WINDOW"))
                {
                    flag = false;
                }
			}
			Toast.makeText(this, permissions[i],Toast.LENGTH_SHORT);
		}
		if (!flag) {
			Toast.makeText(this, "为了保证应用正常运行，需要内存卡访问权限以及位置信息获取权限！", Toast.LENGTH_SHORT).show();
			Dialog dialog = new AlertDialog.Builder(this).setMessage("为了保证应用正常运行，需要内存卡访问权限以及位置信息获取权限！")
					.setPositiveButton("允许", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							ActivityCompat.requestPermissions(SingleFragmentActivity.this, permissions, 001);
						}
					}).create();
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		boolean flag = true;
		if (requestCode == 001) {
			for (int i = 0; i < permissions.length; i++) {
				if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    if(!permissions[i].equals("android.permission.SYSTEM_ALERT_WINDOW"))
                    {
                        flag = false;
                        Log.e("Like",permissions[i]);
                    }

				}
			}
			if(flag){
				Toast.makeText(SingleFragmentActivity.this, "权限申请完毕", Toast.LENGTH_SHORT).show();}
			else{
				Dialog dialog = new AlertDialog.Builder(this).setMessage("缺少必需权限，程序可能运行异常")
						.setPositiveButton("确定", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {

							}
						}).create();
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
			}
		}
	}

    public void requestDrawOverLays() {
        if(!checkFloatPermission(this)) {
            Log.e("Like", Build.VERSION.SDK_INT+" "+Build.VERSION_CODES.M);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "can not DrawOverlays", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.getPackageName()));
                    startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                } else {
                    // Already hold the SYSTEM_ALERT_WINDOW permission, do addview or something.
                }
            }
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (checkFloatPermission(this)) {
                // SYSTEM_ALERT_WINDOW permission not granted...
                Toast.makeText(this, "Permission Denieddd by user.Please Check it in Settings", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Allowed", Toast.LENGTH_SHORT).show();
                // Already hold the SYSTEM_ALERT_WINDOW permission, do addview or something.
            }
        }
    }
*/
    public boolean checkFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                Class cls = Class.forName("android.content.Context");
                Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(cls);
                if (!(obj instanceof String)) {
                    return false;
                }
                String str2 = (String) obj;
                obj = cls.getMethod("getSystemService", String.class).invoke(context, str2);
                cls = Class.forName("android.app.AppOpsManager");
                Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
                declaredField2.setAccessible(true);
                Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                int result = (Integer) checkOp.invoke(obj, 24, Binder.getCallingUid(), context.getPackageName());
                return result == declaredField2.getInt(cls);
            } catch (Exception e) {
                return false;
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                if (appOpsMgr == null)
                    return false;
                int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context
                        .getPackageName());
                return mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED;
            } else {
                return Settings.canDrawOverlays(context);
            }
        }
    }
}
// https://www.cnblogs.com/silentteen/p/6599445.html