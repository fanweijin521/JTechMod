package com.JTechMod.fragment.base;

import com.android.volley.JVolley;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * fragment基类，可以直接使用于所有继承自activitybase的activity中
 * 
 * @author wuxubaiyang
 *
 */
public class BaseFragment extends Fragment {
	/**
	 * 主视图
	 */
	public View contentView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return contentView;
	}

	public void setContentView(int res) {
		if (contentView == null) {
			contentView = LinearLayout.inflate(getActivity(), res, null);
		}
	}

	/**
	 * 初始化控件
	 */
	public void initView() {

	}

	/**
	 * 显示一个toast
	 * 
	 * @param msg
	 *            toast内容
	 */
	public void showToast(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		JVolley.getInstance(getActivity()).cancelFromRequestQueue(getTag());
	}

	@Override
	public void onStop() {
		super.onStop();
		JVolley.getInstance(getActivity()).cancelFromRequestQueue(getTag());
	}
}