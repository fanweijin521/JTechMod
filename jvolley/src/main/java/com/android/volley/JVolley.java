package com.android.volley;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.util.LruCache;
import android.widget.ImageView;

import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.JAsyncPost;

public class JVolley {
	/**
	 * 缓存大小，单位（M），默认为50M
	 */
	private static int CacheSize = 50;
	/**
	 * 默认标记
	 */
	private final static String TAG = "JVolley";
	/**
	 * 默认缓存目录为tag
	 */
	private static String DEFAULT_CACHE_DIR = TAG;

	private static JVolley jVolley;
	private static Context jcontext;
	private ImageLoader imageLoader;
	private RequestQueue jrequestQueue;

	private ArrayList<JAsyncPost> jAsyncPosts;
	private static ExecutorService executorService;

	/**
	 * 主构造
	 *
	 * @param context
	 */
	public JVolley(Context context) {
		jcontext = context;
		this.jrequestQueue = getRequestQueue();

		jAsyncPosts = new ArrayList<JAsyncPost>();
		executorService = Executors.newCachedThreadPool();
		imageLoader = new ImageLoader(jrequestQueue, new BitmapCache());
	}

	/**
	 * 同步获取一个实例
	 *
	 * @param context
	 * @return
	 */
	public static synchronized JVolley getInstance(Context context) {
		if (null == jVolley) {
			jVolley = new JVolley(context);
		}
		return jVolley;
	}

	/**
	 * 得到一个关联全局应用上下文的requestqueue
	 *
	 * @return 返回一个requestqueue
	 */
	public RequestQueue getRequestQueue() {
		if (null == jrequestQueue) {
			jrequestQueue = JVolley.newRequestQueue(jcontext
					.getApplicationContext());
		}
		return jrequestQueue;
	}

	/**
	 * 设置缓存大小，默认为50M
	 *
	 * @param cacheSize
	 *            缓存大小
	 */
	public static void setCacheSize(int cacheSize) {
		CacheSize = cacheSize;
	}

	/**
	 * 将请求添加到队列中
	 *
	 * @param request
	 */
	public <T> void addToRequestQueue(Request<T> request, String tag) {
		// 如果tag为空，则添加默认标记
		request.setTag(null == tag || "".equals(tag) ? TAG : tag);
		// 添加到消息队列中
		getRequestQueue().add(request);
	}

	/**
	 * 将请求添加到队列中
	 *
	 */
	public <T> void addToAsyncQueue(JAsyncPost jAsyncPost, String tag) {
		// 如果tag为空，则添加默认标记
		jAsyncPost.setTag(null == tag || "".equals(tag) ? TAG : tag);
		// 添加到消息队列中
		jAsyncPosts.add(jAsyncPost);
		// 执行方法
		jAsyncPost.executeOnExecutor(executorService, "");
	}

	/**
	 * 通过tag取消队列中的请求
	 *
	 * @param tag
	 *            消息标记
	 */
	public void cancelFromRequestQueue(String tag) {
		for (int i = 0; i < jAsyncPosts.size(); i++) {
			if (tag.equals(jAsyncPosts.get(i).getTag())) {
				jAsyncPosts.get(i).cancel(true);
				jAsyncPosts.remove(i);
			}
		}
		getRequestQueue().cancelAll(null == tag ? TAG : tag);
	}

	/**
	 * 加载图片
	 *
	 * @param imgUrl
	 *            图片地址
	 * @param imageView
	 *            图片容器
	 * @param defaultImageResId
	 *            默认图id
	 * @param errorImageResId
	 *            图片加载失败的图片id
	 */
	@SuppressWarnings("static-access")
	public void LoadImage(String imgUrl, ImageView imageView,
						  int defaultImageResId, int errorImageResId) {
		imageLoader.get(imgUrl, imageLoader.getImageListener(imageView,
				defaultImageResId, errorImageResId));
	}

	/**
	 * 获得imageloader实例
	 *
	 * @return imageloader实例
	 */
	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	/**
	 * 创建一个新的队列
	 *
	 * @param context
	 *            app全局上下文
	 * @param stack
	 *            网络类型
	 * @return 队列
	 */
	public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
		File cacheDir = new File(context.getCacheDir(),
				"".equals(DEFAULT_CACHE_DIR) || null == DEFAULT_CACHE_DIR ? TAG
						: DEFAULT_CACHE_DIR);

		String userAgent = "volley/0";
		try {
			String packageName = context.getPackageName();
			PackageInfo info = context.getPackageManager().getPackageInfo(
					packageName, 0);
			userAgent = packageName + "/" + info.versionCode;
		} catch (NameNotFoundException e) {
		}

		if (stack == null) {
			if (Build.VERSION.SDK_INT >= 9) {
				stack = new HurlStack();
			} else {
				// Prior to Gingerbread, HttpUrlConnection was unreliable.
				// See:
				// http://android-developers.blogspot.com/2011/09/androids-http-clients.html
				stack = new HttpClientStack(
						AndroidHttpClient.newInstance(userAgent));
			}
		}

		Network network = new BasicNetwork(stack);

		RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir),
				network);
		queue.start();
		return queue;
	}

	/**
	 * Creates a default instance of the worker pool and calls
	 * {@link RequestQueue#start()} on it.
	 *
	 * @param context
	 *            A {@link Context} to use for creating the cache dir.
	 * @return A started {@link RequestQueue} instance.
	 */
	public static RequestQueue newRequestQueue(Context context) {
		return newRequestQueue(context, null);
	}

	/**
	 * 图片缓存
	 *
	 * @author "JTech"
	 *
	 */
	class BitmapCache implements ImageCache {
		private LruCache<String, Bitmap> cache;

		public BitmapCache() {
			int maxSize = (CacheSize > 0 ? CacheSize : 50) * 1024 * 1024;
			cache = new LruCache<String, Bitmap>(maxSize) {
				@Override
				protected int sizeOf(String key, Bitmap value) {
					return value.getRowBytes() * value.getHeight();
				}
			};
		}

		@Override
		public Bitmap getBitmap(String url) {
			return cache.get(url);
		}

		@Override
		public void putBitmap(String url, Bitmap bitmap) {
			cache.put(url, bitmap);
		}
	}
}