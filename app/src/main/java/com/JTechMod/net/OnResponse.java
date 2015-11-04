package com.JTechMod.net;

/**
 * 请求回调（ui）
 * 
 * @author wuxubaiyang
 *
 * @param <T>
 *            封装对象的泛型
 */
public interface OnResponse<T> {
	/**
	 * 请求成功回调
	 * 
	 * @param t
	 *            封装对象
	 * @param total
	 *            分页数据的总页数/总条数
	 */
	void responseOk(T t, int total);

	/**
	 * 请求失败回调
	 * 
	 * @param msg
	 *            失败描述
	 */
	void responseFail(String msg);
}