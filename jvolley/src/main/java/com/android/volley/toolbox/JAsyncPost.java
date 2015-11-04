package com.android.volley.toolbox;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

/**
 * 异步接口请求方法
 *
 * @author JTech
 *
 */
public class JAsyncPost extends
		AsyncTask<String, Integer, HashMap<String, Object>> {
	private String tag = "";
	private String url = "";
	private String charsetName = "UTF-8";
	private Listener<String> listener;
	private ErrorListener errorListener;
	private MultipartEntity multipartEntity = new MultipartEntity(
			HttpMultipartMode.BROWSER_COMPATIBLE);

	/**
	 * 主构造
	 *
	 * @param url
	 *            请求地址
	 * @param listener
	 *            请求成功的回调监听
	 * @param errorListener
	 *            请求失败的回调监听
	 */
	public JAsyncPost(String url, Listener<String> listener,
					  ErrorListener errorListener) {
		this.url = url;
		this.listener = listener;
		this.errorListener = errorListener;
	}

	/**
	 * 获取请求标记
	 *
	 * @return 标记
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * 设置请求标记
	 *
	 * @param tag
	 *            标记
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * 获取编码格式
	 *
	 * @return 编码格式
	 */
	public String getCharsetName() {
		return charsetName;
	}

	/**
	 * 设置编码格式
	 *
	 * @param charsetName
	 *            编码格式
	 */
	public void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}

	/**
	 * 添加一个图片格式的文件（jpeg）
	 *
	 * @param key
	 *            键
	 * @param imgFile
	 *            文件
	 */
	public void putImageJPEG(String key, File imgFile) {
		putFile(key, imgFile, "image/jpeg");
	}

	/**
	 * 添加一个字符串
	 *
	 * @param key
	 *            键
	 * @param value
	 *            值
	 */
	public void putString(String key, String value) {
		try {
			multipartEntity.addPart(key,
					new StringBody(value, Charset.forName(charsetName)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加一个文件，需要指定格式
	 *
	 * @param key
	 *            键
	 * @param file
	 *            文件
	 * @param mimeType
	 *            格式
	 */
	public void putFile(String key, File file, String mimeType) {
		multipartEntity.addPart(key, new FileBody(file, mimeType));
	}

	@Override
	protected HashMap<String, Object> doInBackground(String... params) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		int code = -1;
		Object result;
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(multipartEntity);
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpPost);
			code = httpResponse.getStatusLine().getStatusCode();
			result = EntityUtils.toString(httpResponse.getEntity());
		} catch (ParseException e) {
			result = e;
			e.printStackTrace();
		} catch (IOException e) {
			result = e;
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			result = e;
			e.printStackTrace();
		}
		map.put("code", code);
		map.put("result", result);
		return map;
	}

	@Override
	protected void onPostExecute(HashMap<String, Object> map) {
		Object result = map.get("result");
		int code = (Integer) map.get("code");
		if (null != listener && !isCancelled()) {
			switch (code) {
				case 200:// 请求成功
					listener.onResponse((String) result);
					break;
				case -1:// 异常
					errorListener.onErrorResponse(new VolleyError(
							((Exception) result).getMessage()));
					break;
				default:// 请求失败
					errorListener.onErrorResponse(new VolleyError("错误码：" + code
							+ "\t错误描述：" + result));
					break;
			}
		}
	}
}