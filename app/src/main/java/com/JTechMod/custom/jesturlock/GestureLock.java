package com.JTechMod.custom.jesturlock;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 手势锁
 * 
 * @author wuxubaiyang
 *
 */
public class GestureLock extends SurfaceView implements SurfaceHolder.Callback {
	/**
	 * tag
	 */
	private final static String TAG = "JGestureLock";
	/**
	 * mapkey
	 */
	private final static String CENTER_X_COORD = "CENTER_X_COORD";
	private final static String CENTER_Y_COORD = "CENTER_Y_COORD";
	private final static String START_X_COORD = "START_X_COORD";
	private final static String START_Y_COORD = "START_Y_COORD";
	private final static String END_X_COORD = "END_X_COORD";
	private final static String END_Y_COORD = "END_Y_COORD";
	/**
	 * 屏幕宽度
	 */
	private int viewWidth = 0;
	/**
	 * 屏幕高度
	 */
	private int viewHeight = 0;
	/**
	 * 点之间的间距
	 */
	private float dotSpace = 0;
	/**
	 * 点的宽度(等于高度)
	 */
	private float dotWidth = 0;
	/**
	 * 点的数量
	 */
	private int dotCount = 9;
	/**
	 * 点的间距数量+点的数量的比例
	 */
	private float dotSpaceRatio = 1.3f;
	/**
	 * 连接线的颜色
	 */
	private int lineColor = 0xFFF95667;
	/**
	 * 连接线的宽度
	 */
	private int lineWidth = 5;
	/**
	 * 是否显示连接线
	 */
	private boolean showLine = true;
	/**
	 * 是否显示跟随连接线
	 */
	private boolean showFollowLine = true;
	/**
	 * 点的点击态与常态图片对象
	 */
	private Bitmap bitmap_dot_u, bitmap_dot_d;
	/**
	 * 回调
	 */
	private OnJGesturLock onJGesturLock;

	/**
	 * holder
	 */
	private SurfaceHolder surfaceHolder;
	/**
	 * 点的起始坐标
	 */
	private ArrayList<HashMap<String, Float>> dotStartCoord;
	/**
	 * 点的结束坐标
	 */
	private ArrayList<HashMap<String, Float>> dotEndCoord;
	/**
	 * 点的中心坐标
	 */
	private ArrayList<HashMap<String, Float>> dotCenterCoord;
	/**
	 * 记录手势轨迹
	 */
	private ArrayList<Integer> gesturelTrack = new ArrayList<Integer>();

	/**
	 * 更新handler
	 */
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (0 == msg.what) {// 绘制路径
				Bundle bundle = msg.getData();
				float fx = bundle.getFloat("fx");
				float fy = bundle.getFloat("fy");
				for (int i = 0; i < dotStartCoord.size(); i++) {
					HashMap<String, Float> startMap = dotStartCoord.get(i);
					HashMap<String, Float> endMap = dotEndCoord.get(i);
					float startX = startMap.get(START_X_COORD);
					float endX = endMap.get(END_X_COORD);
					float startY = startMap.get(START_Y_COORD);
					float endY = Math.abs(endMap.get(END_Y_COORD));
					// 判断是否在范围内
					if (fx > startX && fx < endX && fy > startY && fy < endY) {
						boolean flag = true;
						for (int j = 0; j < gesturelTrack.size(); j++) {
							if (i == gesturelTrack.get(j)) {
								flag = false;
								break;
							}
						}
						if (flag) {
							gesturelTrack.add(i);
						}
						break;
					}
				}
				// 绘图
				draw2Canvas(fx, fy);
			} else {// 回调参数,清除路径，还原状态
				if (gesturelTrack.size() != 0) {
					StringBuffer pwd = new StringBuffer();
					for (int i = 0; i < gesturelTrack.size(); i++) {
						pwd.append(gesturelTrack.get(i) + "");
					}
					// 回调结果
					onJGesturLock.Over(pwd.toString());
					// 清空集合
					gesturelTrack = new ArrayList<Integer>();
					// 绘图
					draw2Canvas(-1f, -1f);
				}
			}
		};
	};

	public GestureLock(Context context) {
		super(context);
		init();
	}

	public GestureLock(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GestureLock(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public OnJGesturLock getOnJGesturLock() {
		return onJGesturLock;
	}

	public void setOnJGesturLockOver(OnJGesturLock onJGesturLock) {
		this.onJGesturLock = onJGesturLock;
	}

	public int getDotCount() {
		return dotCount;
	}

	public void setDotCount(int dotCount) {
		this.dotCount = dotCount;
	}

	public int getLineColor() {
		return lineColor;
	}

	public void setLineColor(int lineColor) {
		this.lineColor = lineColor;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	public boolean isShowLine() {
		return showLine;
	}

	public void setShowLine(boolean showLine) {
		this.showLine = showLine;
	}

	public boolean isShowFollowLine() {
		return showFollowLine;
	}

	public void setShowFollowLine(boolean showFollowLine) {
		this.showFollowLine = showFollowLine;
	}

	public ArrayList<Integer> getGesturelTrack() {
		return gesturelTrack;
	}

	public void setGesturelTrack(ArrayList<Integer> gesturelTrack) {
		this.gesturelTrack = gesturelTrack;
	}

	/**
	 * 设置点击态以及常态图片
	 * 
	 * @param bitmap_dot_u
	 *            常态图片
	 * @param bitmap_dot_d
	 *            点击太图片
	 */
	public void setBitmap(Bitmap bitmap_dot_d, Bitmap bitmap_dot_u) {
		this.bitmap_dot_d = bitmap_dot_d;
		this.bitmap_dot_u = bitmap_dot_u;
		// 对bitmap进行处理
		disposeBitmap();
	}

	/**
	 * 初始化
	 */
	private void init() {
		// 得到holder
		surfaceHolder = this.getHolder();
		surfaceHolder.addCallback(this);
		// 设置背景为透明
		setZOrderOnTop(true);
		surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
	}

	@Override
	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouchEvent(MotionEvent event) {
		float fy = event.getY();
		float fx = event.getX();
		Bundle bundle = new Bundle();
		bundle.putFloat("fx", fx);
		bundle.putFloat("fy", fy);
		Message message = new Message();
		message.setData(bundle);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:// 手指按下状态
			message.what = 0;
			handler.sendMessage(message);
			break;
		case MotionEvent.ACTION_MOVE:// 手指移动状态
			message.what = 0;
			handler.sendMessage(message);
			break;
		case MotionEvent.ACTION_UP:// 手指抬起
		case MotionEvent.ACTION_CANCEL:// 取消状态
			message.what = 1;
			handler.sendMessage(message);
			break;
		}
		return true;
	}

	/**
	 * 绘制图案
	 * 
	 * @param fingerX
	 *            手指当前所在的x点
	 * @param fingerY
	 *            手指当前坐在的y点
	 */
	private void draw2Canvas(float fingerX, float fingerY) {
		synchronized (surfaceHolder) {
			// 锁定画布
			Canvas canvas = surfaceHolder.lockCanvas();
			// 清除画布内容
			canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			// 实例化画笔
			Paint paint = new Paint();
			// 抗锯齿
			paint.setAntiAlias(true);
			// 记录上一次中心点的坐标
			float lastCenterX = 0f;
			float lastCenterY = 0f;
			// 判断是否显示连接线
			if (showLine) {
				if (-1 != fingerX && -1 != fingerY) {
					// 设置连接线的颜色
					paint.setColor(lineColor);
					// 设置连接线的宽度
					paint.setStrokeWidth(lineWidth);
					// 设置连接线的填充方式
					paint.setStyle(Paint.Style.FILL);
					// 绘制连接线
					for (int i = 0; i < gesturelTrack.size(); i++) {
						HashMap<String, Float> map = dotCenterCoord.get(gesturelTrack.get(i));
						float centerX = map.get(CENTER_X_COORD);
						float centerY = map.get(CENTER_Y_COORD);
						// 判断是不是第一个点
						if (0 != lastCenterX && 0 != lastCenterY) {
							canvas.drawLine(lastCenterX, lastCenterY, centerX, centerY, paint);
						}
						// 记录当前点坐标作为下一个点连线的起始点坐标
						lastCenterX = centerX;
						lastCenterY = centerY;
					}
				}
			}
			// 判断是否需要显示跟随线
			if (showFollowLine) {
				if (-1 != fingerX && -1 != fingerY) {
					// 判断是不是清空的状态
					if (gesturelTrack.size() > 0) {
						canvas.drawLine(lastCenterX, lastCenterY, fingerX, fingerY, paint);
					}
				}
			}
			// 绘制默认的点
			drawDefault(canvas, paint);
			// 点图片不为空的时候绘制点
			if (null != bitmap_dot_d && null != bitmap_dot_u) {
				// 绘制点
				for (int i = 0; i < gesturelTrack.size(); i++) {
					HashMap<String, Float> map = dotStartCoord.get(gesturelTrack.get(i));
					float startX = map.get(START_X_COORD);
					float startY = map.get(START_Y_COORD);
					// 绘制点击态
					canvas.drawBitmap(bitmap_dot_d, startX, startY, paint);
				}
			}
			// 解锁画布
			surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}

	/**
	 * 绘制默认的圆
	 */
	private void drawDefault(Canvas canvas, Paint paint) {
		// 绘制点
		for (int i = 0; i < dotStartCoord.size(); i++) {
			HashMap<String, Float> map = dotStartCoord.get(i);
			float startX = map.get(START_X_COORD);
			float startY = map.get(START_Y_COORD);
			canvas.drawBitmap(bitmap_dot_u, startX, startY, paint);
		}
	}

	@Override
	@SuppressLint("DrawAllocation")
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 获取view宽度
		viewWidth = getMeasuredWidth();
		// 获取view高度
		viewHeight = getMeasuredHeight();
		// log
		Log.d(TAG, "控件宽度" + viewWidth + "控件高度" + viewHeight);
		// 开平方计算行数(列数)
		int lineCount = (int) Math.sqrt(dotCount);
		// 计算点的间距
		dotSpace = viewWidth / ((lineCount + lineCount + 1) * dotSpaceRatio);
		// 计算点的宽度
		dotWidth = (viewWidth - dotSpace * (lineCount + 1)) / lineCount;
		// 实例化坐标集合对象
		dotEndCoord = new ArrayList<HashMap<String, Float>>();
		dotStartCoord = new ArrayList<HashMap<String, Float>>();
		dotCenterCoord = new ArrayList<HashMap<String, Float>>();
		// 半径
		float radius = dotWidth / 2;
		// 计算点的起始坐标和结束坐标
		for (int i = 0; i < lineCount; i++) {
			float startY = 0f;
			float endY = 0f;
			// 起始点Y坐标
			startY = dotSpace * (i + 1) + dotWidth * i;
			// 结束点Y坐标
			endY = startY + dotWidth;
			for (int j = 0; j < lineCount; j++) {
				float startX = 0f;
				float endX = 0f;
				float centerX = 0f;
				float centerY = 0f;
				// 起始点的x坐标
				startX = dotSpace * (j + 1) + dotWidth * j;
				// 结束点的x坐标
				endX = startX + dotWidth;
				// 中心点X坐标
				centerX = startX + radius;
				// 中心点Y坐标
				centerY = startY + radius;
				// 添加XY的起始坐标
				dotStartCoord.add(getMap(START_X_COORD, startX, START_Y_COORD, startY));
				// 添加XY的结束坐标
				dotEndCoord.add(getMap(END_X_COORD, endX, END_Y_COORD, endY));
				// 添加点的中心点坐标
				dotCenterCoord.add(getMap(CENTER_X_COORD, centerX, CENTER_Y_COORD, centerY));
			}
		}
		// 对bitmap进行处理
		disposeBitmap();
	}

	/**
	 * 处理图片
	 */
	private void disposeBitmap() {
		if (null != bitmap_dot_d && null != bitmap_dot_u && 0 != dotWidth) {
			// 缩放比例
			float ratio = dotWidth / bitmap_dot_u.getWidth();
			// 缩放图片
			bitmap_dot_u = bitmapScale(bitmap_dot_u, ratio);
			bitmap_dot_d = bitmapScale(bitmap_dot_d, ratio);
		}
	}

	/**
	 * bitmap缩放
	 * 
	 * @param bitmap
	 *            原图片
	 * @return 缩放后的图片
	 */
	private Bitmap bitmapScale(Bitmap bitmap, float ratio) {
		Matrix matrix = new Matrix();
		matrix.postScale(ratio, ratio);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	/**
	 * 封装坐标map
	 */
	private HashMap<String, Float> getMap(String coordkey1, float coord1, String coordkey2, float coord2) {
		HashMap<String, Float> map = new HashMap<String, Float>();
		map.put(coordkey1, coord1);
		map.put(coordkey2, coord2);
		return map;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		draw2Canvas(-1f, -1f);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}
}