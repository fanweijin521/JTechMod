package com.JTechMod.utile;

import android.util.Log;

import com.JTechMod.BuildConfig;

/**
 * log
 * 
 * @author wuxubaiyang
 *
 */
public class LogUtils {
	public static boolean enabled = BuildConfig.DEBUG;

	public static void v(final String tag, final String message) {
		if (enabled) {
			Log.v(tag, message);
		}
	}
}