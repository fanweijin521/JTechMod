package com.android.volley.toolbox;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache.Entry;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

public class JStringRequest extends Request<String> {
	private String url = "";
	private String charsetName = "";
	private int method = Method.POST;
	private Listener<String> listener;
	private Map<String, String> mapHeader;
	private Map<String, String> mapParams;

	public JStringRequest(int method, String url, Listener<String> listener,
						  ErrorListener errorListener) {
		super(method, url, errorListener);
		this.listener = listener;
		this.method = method;
		this.url = url;
	}

	public JStringRequest putHeader(String key, String value) {
		if (null == mapHeader) {
			mapHeader = new HashMap<String, String>();
		}
		mapHeader.put(key, value);
		return this;
	}

	public JStringRequest putParams(String key, String value) {
		if (null == mapParams) {
			mapParams = new HashMap<String, String>();
		}
		mapParams.put(key, value);
		return this;
	}

	public String getCharsetName() {
		return charsetName;
	}

	public void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}

	@Override
	public String getCacheKey() {
		return super.getCacheKey();
	}

	@Override
	public Entry getCacheEntry() {
		return super.getCacheEntry();
	}

	@Override
	public String getUrl() {
		switch (method) {
			case Method.GET:// get方法需要做处理
				if (null != mapParams) {
					boolean flag = false;
					Iterator<String> iterator = mapParams.keySet().iterator();
					while (iterator.hasNext()) {
						String key = (String) iterator.next();
						String value = mapParams.get(key);
						if (!flag) {
							flag = true;
							url += "?" + key + "=" + value;
						} else {
							url += "&" + key + "=" + value;
						}
					}
				}
				return url;
			default:
				return super.getUrl();
		}
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return null == mapHeader ? super.getHeaders() : mapHeader;
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return null == mapParams ? super.getParams() : mapParams;
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonStr = new String(response.data,
					"".equals(charsetName) ? HttpHeaderParser
							.parseCharset(response.headers) : charsetName);
			return Response.success(jsonStr,
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return Response.error(new VolleyError(e));
		}
	}

	@Override
	protected void deliverResponse(String response) {
		if (null != listener) {
			listener.onResponse(response);
		}
	}
}