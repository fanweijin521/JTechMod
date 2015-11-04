package com.JTechMod.custom;

import com.android.volley.JVolley;
import com.android.volley.toolbox.NetworkImageView;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 重写的网络图片加载控件
 * 
 * @author wuxubaiyang
 *
 */
public class JImageView extends NetworkImageView {

	public JImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public JImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public JImageView(Context context) {
		super(context);
	}

	/**
	 * 显示网络图片
	 * 
	 * @param imgUrl
	 *            图片地址
	 */
	public void displayImage(String imgUrl) {
		displayImage(imgUrl, 0, 0);
	}

	/**
	 * 显示网络图片
	 * 
	 * @param imgUrl
	 *            图片地址
	 * @param defaultImage
	 *            默认图片
	 * @param errorImage
	 *            错误图片
	 */
	public void displayImage(String imgUrl, int defaultImage, int errorImage) {
		// 设置错误图
		setErrorImageResId(errorImage);
		// 设置默认图
		setDefaultImageResId(defaultImage);
		// 加载图片
		setImageUrl(imgUrl, JVolley.getInstance(getContext()).getImageLoader());
	}
}