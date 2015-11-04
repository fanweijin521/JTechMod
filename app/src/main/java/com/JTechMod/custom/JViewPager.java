package com.JTechMod.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 禁止滑动的viewpager
 * 
 * @author wuxubaiyang
 *
 */
@SuppressLint("ClickableViewAccessibility")
public class JViewPager extends ViewPager {
	private boolean isCanScroll = false;

	public JViewPager(Context context) {
		super(context);
	}

	public JViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public boolean isCanScroll() {
		return isCanScroll;
	}

	public void setCanScroll(boolean isCanScroll) {
		this.isCanScroll = isCanScroll;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if (isCanScroll) {
			return super.onInterceptTouchEvent(arg0);
		} else {
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		if (isCanScroll) {
			return super.onTouchEvent(arg0);
		} else {
			return false;
		}
	}
}