package com.heasy.app.core.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtil {
	private static Logger logger = LoggerFactory.getLogger(ImageUtil.class);

	/**
	 * 从绝对路径加载图像文件
	 * @param fileFullPath 文件绝对路径
	 * @return
	 */
	public static Bitmap readFromPath(String fileFullPath){
		Bitmap bitmap = BitmapFactory.decodeFile(fileFullPath);
		return bitmap;
	}

	/**
	 * Bitmap对象 转成 InputStream对象
	 * @param bitmap
	 * @param format
	 * @return
	 */
	public static InputStream bitmap2InputStream(Bitmap bitmap, CompressFormat format){
		if(bitmap == null){
			return null;
		}
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		bitmap.compress(format, 100, outputStream);
		InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		return inputStream;
	}

	/**
	 * Bitmap对象 转成 字节数组
	 * @param bitmap
	 * @param format
	 * @return
	 */
	public static byte[] bitmap2ByteArray(Bitmap bitmap, CompressFormat format){
		if(bitmap == null){
			return null;
		}

		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			bitmap.compress(format, 100, outputStream);
			return outputStream.toByteArray();
		} catch (Exception ex) {
			logger.error("", ex);
		}
		return null;
	}

	/**
	 * InputStream对象 转成 Bitmap对象
	 * @param inputStream
	 * @return
	 */
	public static Bitmap inputStream2Bitmap(InputStream inputStream){
		if(inputStream == null){
			return null;
		}
		
		Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
		return bitmap;
	}

	/**
	 * 字节数组 转成 Bitmap对象
	 * @param dataBytes
	 * @return
	 */
	public static Bitmap byteArray2Bitmap(byte[] dataBytes){
		if (dataBytes != null && dataBytes.length != 0) {  
            return BitmapFactory.decodeByteArray(dataBytes, 0, dataBytes.length);  
        }  
        return null;
	}

	/**
	 * 图像文件的InputStream对象 转成 Base64字符串
	 * @param imageFileInputStream
	 * @return
	 */
	public static String imageInputStream2Base64String(InputStream imageFileInputStream){
		byte[] data = null;
		try {
			data = new byte[imageFileInputStream.available()];
			imageFileInputStream.read(data);
			imageFileInputStream.close();
			
			String imageStringData = Base64.encodeToString(data, Base64.DEFAULT);
			return imageStringData;
		} catch (IOException ex) {
			logger.error("", ex);
		}
		return null;
	}

	/**
	 * 获取图片文件的宽度和高度
	 * @param filePath 图片文件的绝对路径
	 */
	public static int[] getImageWidthHeight(String filePath){
		BitmapFactory.Options options = new BitmapFactory.Options();
		BitmapFactory.decodeFile(filePath, options);
		return new int[]{options.outWidth, options.outHeight};
	}

	/**
	 * 图片缩放
	 * @param filePath 图片文件的绝对路径
	 * @param newWidth 新的宽度
	 * @param newHeight 新的高度
	 * @param format 图片格式
	 */
	public static void zoomImage(String filePath, int newWidth, int newHeight, CompressFormat format){
		Bitmap bitmap = BitmapFactory.decodeFile(filePath);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		logger.debug("old width=" + width + ", old height=" + height);
		logger.debug("new width=" + newWidth + ", new height=" + newHeight);

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		newBitmap.compress(format, 100, outputStream);

		FileUtil.writeFile(outputStream.toByteArray(), filePath);
	}

	/**
	 * 图片旋转
	 * @param angle 旋转角度
	 */
	public static Bitmap rotateImage(Bitmap bitmap, float angle){
		Matrix matrix = new Matrix();
		matrix.setRotate(angle, bitmap.getWidth() / 2, bitmap.getHeight() / 2); //以图片中心为原点进行旋转
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return newBitmap;
	}

	/**
	 * 图片水平翻转
	 */
	public static Bitmap hFlipImage(Bitmap bitmap){
		Matrix matrix = new Matrix();
		matrix.postScale(-1, 1);
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return newBitmap;
	}

	/**
	 * 图片垂直翻转
	 */
	public static Bitmap vFlipImage(Bitmap bitmap){
		Matrix matrix = new Matrix();
		matrix.postScale(1, -1);
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return newBitmap;
	}
	
}
