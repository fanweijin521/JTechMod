package com.JTechMod;

import java.util.Iterator;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.text.TextUtils;

/**
 * application对象
 * 
 * @author JTech
 *
 */
public class JApplication extends Application {
	private static JApplication jApplication;

	@Override
	public void onCreate() {
		super.onCreate();
		jApplication = this;
		// 防止重启两次,非相同名字的则返回
		if (!isSameAppName()) {
			return;
		}
	}

	/**
	 * 获取application对象
	 * 
	 * @return JApplication
	 */
	public static JApplication get() {
		return jApplication;
	}

	/**
	 * 判断是否为相同app名
	 * 
	 * @return
	 */
	private boolean isSameAppName() {
		int pid = android.os.Process.myPid();
		String processAppName = getProcessAppName(pid);
		if (TextUtils.isEmpty(processAppName) || !processAppName.equalsIgnoreCase(getPackageName())) {
			return false;
		}
		return true;
	}

	/**
	 * 获取processAppName
	 * 
	 * @param pid
	 * @return
	 */
	private String getProcessAppName(int pid) {
		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		Iterator<RunningAppProcessInfo> iterator = activityManager.getRunningAppProcesses().iterator();
		while (iterator.hasNext()) {
			RunningAppProcessInfo runningAppProcessInfo = (RunningAppProcessInfo) (iterator
					.next());
			try {
				if (runningAppProcessInfo.pid == pid) {
					return runningAppProcessInfo.processName;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "";
	}
}