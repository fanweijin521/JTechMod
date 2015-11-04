package com.JTechMod.net;

import com.android.volley.Request.Method;
import com.android.volley.JVolley;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.JStringRequest;
import com.JTechMod.entity.ModEntity;
import com.android.volley.VolleyError;

import android.content.Context;

/**
 * 接口请求类
 * 
 * @author wuxubaiyang
 *
 */
public class JApi {
	private Context context;
	private static JApi jApi;
	/**
	 * 基本接口地址
	 */
	private String baseUrl = "";

	/**
	 * 主构造
	 * 
	 * @param context
	 *            关联上下文
	 */
	public JApi(Context context) {
		this.context = context;
	}

	/**
	 * 获取japi实例
	 * 
	 * @param context
	 *            关联上下文
	 * @return japi实例
	 */
	public static JApi getInstance(Context context) {
		if (null == jApi) {
			jApi = new JApi(context);
		}
		return jApi;
	}

	/**
	 * 通用的异常回调方法
	 * 
	 * @author "JTech"
	 *
	 */
	private class Error implements ErrorListener {
		private OnResponse<?> onResponse;

		public Error(OnResponse<?> onResponse) {
			this.onResponse = onResponse;
		}

		@Override
		public void onErrorResponse(VolleyError error) {
			onResponse.responseFail(error.getMessage());
		}
	}

	/**
	 * 请求范例
	 * 
	 * @param tag
	 * @param onResponse
	 */
	public void ModRequest(String tag, OnResponse<ModEntity> onResponse) {
		JStringRequest jStringRequest = new JStringRequest(Method.POST, baseUrl, new Response.Listener<String>() {
			@Override
			public void onResponse(String arg0) {

			}
		}, new Error(onResponse));
		jStringRequest.putParams("key", "value");
		JVolley.getInstance(context).addToRequestQueue(jStringRequest, tag);
	}
}