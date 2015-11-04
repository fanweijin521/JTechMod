package com.JTechMod.custom.imageacquire;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.Fragment;

/**
 * 图片获取工具(媒体库、照相机)
 * 
 * @author wuxubaiyang
 *
 */
public class ImageAcquire {
	/**
	 * 关联上下文
	 */
	private Activity activity;
	/**
	 * 图片加载回调
	 */
	private OnImageAcquire onImageAcquire;
	/***
	 * 图片缓存
	 */
	private final static String TAKE_PHOTO_CACHE_NAME = "image_acquire_cache.jpg";
	/**
	 * 图片缓存路径
	 */
	private final static String CACHE_FILE_NAME = "/image_acquire/";
	/**
	 * 从媒体库获取图片
	 */
	private final static int REQUEST_CODE_IMAGE = 101;
	/**
	 * 从照相机获取图片
	 */
	private final static int REQUEST_CODE_CAMERA = 102;

	/**
	 * 有参构造
	 * 
	 * @param onImageAcquire
	 */
	public ImageAcquire(Activity activity, OnImageAcquire onImageAcquire) {
		this.activity = activity;
		this.onImageAcquire = onImageAcquire;
	}

	public void setOnImageAcquire(OnImageAcquire onImageAcquire) {
		this.onImageAcquire = onImageAcquire;
	}

	/**
	 * 设置activity返回值
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void ActivityResult(int requestCode, int resultCode, Intent data) {
		Uri uri = null;
		if (requestCode == REQUEST_CODE_IMAGE) {// 照片库返回
			if (null != data) {
				uri = data.getData();
				ImageDispose(uri, false, onImageAcquire);
			}
		} else if (requestCode == REQUEST_CODE_CAMERA) {// 拍照返回
			uri = Uri.fromFile(new File(getSDCachePatch() + TAKE_PHOTO_CACHE_NAME));
			ImageDispose(uri, true, onImageAcquire);
		}
	}

	/**
	 * 跳转到拍照界面获取图片
	 */
	public void getPicByCamera(Activity activity) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(getSDCachePatch() + TAKE_PHOTO_CACHE_NAME)));
		activity.startActivityForResult(intent, REQUEST_CODE_CAMERA);
	}

	/**
	 * 跳转到拍照界面获取图片
	 */
	public void getPicByCamera(Fragment fragment) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(getSDCachePatch() + TAKE_PHOTO_CACHE_NAME)));
		fragment.startActivityForResult(intent, REQUEST_CODE_CAMERA);
	}

	/**
	 * 跳转到媒体库获取图片
	 */
	public void getPicByMedia(Activity activity) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		activity.startActivityForResult(intent, REQUEST_CODE_IMAGE);
	}

	/**
	 * 跳转到媒体库获取图片
	 */
	public void getPicByMedia(Fragment fragment) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		fragment.startActivityForResult(intent, REQUEST_CODE_IMAGE);
	}

	/**
	 * 获取默认的sd卡缓存目录
	 * 
	 * @return
	 */
	private String getSDCachePatch() {
		String SDCachePatch = "";
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String SDRoot = Environment.getExternalStorageDirectory().toString();
			SDCachePatch = SDRoot + CACHE_FILE_NAME;
		} else {
			File cacheDirFile = activity.getCacheDir();
			SDCachePatch = cacheDirFile.getPath() + CACHE_FILE_NAME;
		}
		File cacheFile = new File(SDCachePatch);
		if (!cacheFile.exists()) {
			cacheFile.mkdirs();
		}
		return SDCachePatch;
	}

	/**
	 * 图片处理方法
	 * 
	 * @param uri
	 *            图片uri
	 * @param isTakePhoto
	 *            是否为拍照图片的标记
	 */
	private void ImageDispose(Uri uri, final boolean isTakePhoto, final OnImageAcquire onImageAcquire) {
		new AsyncTask<Uri, Integer, Object>() {
			@Override
			protected Object doInBackground(Uri... params) {
				Uri uri = params[0];
				Object result = null;
				try {
					String fileName = System.currentTimeMillis() / 1000 + ".jpg";
					result = saveImage(uri, new File(getSDCachePatch() + fileName));
				} catch (Exception e) {
					e.printStackTrace();
					result = e.toString();
				}
				return result;
			}

			protected void onPostExecute(Object result) {
				if (null != onImageAcquire) {
					if (result instanceof String) {
						onImageAcquire.LoadFail(String.valueOf(result));
					} else {
						File file = (File) result;
						Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
						onImageAcquire.LoadOk(file, bitmap);
					}
				}
			};

			/**
			 * 获取到的图片本地保存至指定目录
			 *
			 * @param file
			 *            文件路径
			 * @return
			 * @throws Exception
			 */
			private File saveImage(Uri uri, File file) throws Exception {
				Bitmap bitmap = getBitmap(uri);
				if (bitmap != null) {
					// 质量压缩图片
					int quality = 100;// 文件压缩比例
					int fileSize = 100;// 文件大小（KB）
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
					while (byteArrayOutputStream.toByteArray().length / 1024 > fileSize) {
						byteArrayOutputStream.reset();
						bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
						quality -= 10;
						if (quality <= 0) {
							break;
						}
					}
					// 判断缓存文件夹是否存在，不存在则创建
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					// 将压缩后的图片写入到本地
					FileOutputStream fileOutputStream = new FileOutputStream(file);
					fileOutputStream.write(byteArrayOutputStream.toByteArray());
					fileOutputStream.flush();
					fileOutputStream.close();
				} else {
					throw new Exception("图片源为空");
				}
				return file;
			}

			/**
			 * uri转bitmap处理
			 * 
			 * @param uri
			 *            传入uri
			 * @return bitmap
			 * @throws URISyntaxException
			 * @throws IOException
			 */
			private Bitmap getBitmap(Uri uri) throws URISyntaxException, IOException {
				Bitmap bitmap = null;
				if (!isTakePhoto) {
					bitmap = Media.getBitmap(activity.getContentResolver(), uri);
					if (null == bitmap) {
						Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
						if (cursor != null) {
							cursor.moveToFirst();
							// 获取图片路径
							String filePath = cursor.getString(cursor.getColumnIndex(Media.DATA));
							// 获取旋转的角度
							String orientation = cursor.getString(cursor.getColumnIndex(Media.ORIENTATION));
							if (filePath != null) {
								bitmap = decodeBitmap(filePath);
								// 判断图片是否发生了翻转
								int angle = 0;
								if (orientation != null && !"".equals(orientation)) {
									angle = Integer.parseInt(orientation);
								}
								if (angle != 0) {
									Matrix matrix = new Matrix();
									int width = bitmap.getWidth();
									int height = bitmap.getHeight();
									matrix.setRotate(angle);
									bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
								}
							}
						}
					}
				} else {
					String filePath = new File(new URI(uri.toString())).toString();
					bitmap = decodeBitmap(filePath);
					Matrix matrix = new Matrix();
					int width = bitmap.getWidth();
					int height = bitmap.getHeight();
					matrix.setRotate(readPictureDegree(filePath));
					bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
				}
				return bitmap;
			}

			/**
			 * 读取图片缩略图
			 * 
			 * @param filePath
			 *            绝对路径
			 * @return bitmap
			 */
			private Bitmap decodeBitmap(String filePath) {
				// 得到图片尺寸
				Options options = new Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(filePath, options);
				// 成比例缩放至480
				options.inSampleSize = calculateInSampleSize(options, 480,
						options.outHeight * (480 / options.outWidth));
				options.inJustDecodeBounds = false;
				return BitmapFactory.decodeFile(filePath, options);
			}

			/**
			 * 获取图片缩放比例
			 * 
			 * @param options
			 *            配置
			 * @param reqWidth
			 *            需要宽
			 * @param reqHeight
			 *            需要高
			 * @return 返回比例
			 */
			private int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
				final int height = options.outHeight;
				final int width = options.outWidth;
				int inSampleSize = 1;

				if (height > reqHeight || width > reqWidth) {

					final int halfHeight = height / 2;
					final int halfWidth = width / 2;
					while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
						inSampleSize *= 2;
					}
				}
				return inSampleSize;
			}

			/**
			 * 读取图片属性：旋转的角度
			 * 
			 * @param path
			 *            图片绝对路径
			 * @return degree旋转的角度
			 * @throws IOException
			 */
			public int readPictureDegree(String path) throws IOException {
				int degree = 0;
				ExifInterface exifInterface = new ExifInterface(path);
				int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
				}
				return degree;
			}
		}.execute(uri);
	}
}