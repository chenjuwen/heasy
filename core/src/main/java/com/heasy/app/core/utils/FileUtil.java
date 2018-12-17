package com.heasy.app.core.utils;

import android.content.Context;

import com.heasy.app.core.configuration.ConfigBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Calendar;

public class FileUtil {
	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * 输入流输出到文件
	 */
	public static boolean writeFile(InputStream in, String destFile){
	    BufferedInputStream inStream = null;
	    BufferedOutputStream outStream = null;
	    try {
			File file = new File(destFile);
			if(file.exists()){
				file.delete();
			}
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}

			inStream = new BufferedInputStream(in);
	    	outStream = new BufferedOutputStream(new FileOutputStream(file));
	    	
	    	byte[] buf = new byte[4096];
	    	int i;
	    	while ((i = inStream.read(buf)) != -1) {
	    		outStream.write(buf, 0, i);
	    	}

			outStream.flush();
	    	outStream.close();
	    	outStream = null;

			inStream.close();
			inStream = null;
	    	
	    	return true;
	    } catch (IOException ex) {
			logger.error("", ex);
	    } finally {
	    	if (inStream != null) {
	    		try {
	    			inStream.close();
	    		} catch (IOException ex) {
	    			
	    		}
	    	}
	      
	      	if (outStream != null) {
	      		try {
	      			outStream.close();
	      		} catch (IOException ex) {
	      			
	      		}
	      	}
	    }
	    return false;
	}

	/**
	 * 字符串内容输出到文件
	 */
	public static boolean writeFile(String data, String filePath){
		OutputStreamWriter writer = null;
		try{
			File file = new File(filePath);
			if(file.exists()){
				file.delete();
			}
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}

			writer = new OutputStreamWriter(new FileOutputStream(file), Charset.forName(ConfigBean.DEFAULT_CHARSET_UTF8));
			writer.write(data);
			writer.flush();
			writer.close();
			writer = null;
			return true;
		}catch(Exception ex){
			logger.error("", ex);
		}finally{
			if(writer!=null) {
				try {
					writer.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 * 字节数组输出到文件
	 */
	public static boolean writeFile(byte[] byteData, String filePath){
		FileOutputStream out = null;
		try{
			File file = new File(filePath);
			if(file.exists()){
				file.delete();
			}
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}

			out = new FileOutputStream(file);
			out.write(byteData);
			out.flush();
			out.close();
			out = null;
			return true;
		}catch(Exception ex){
			logger.error("", ex);
		}finally{
			if(out!=null) {
				try {
					out.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 * 读取文本型文件内容
	 * @param filePath 文件路径
	 */
	public static String readTextFile(String filePath)throws Exception{
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));
			String line = null;
			while((line=reader.readLine())!=null){
				sb.append(line);
			}
		}catch(Exception ex){
			logger.error("", ex);
		}finally{
			if(reader != null){
				reader.close();
			}
		}
		return sb.toString();
	}

	public static byte[] readByteFile(String filePath){
		ByteArrayOutputStream outStream = null;
		byte[] bArray = null;

		DataInputStream dataStream = null;
		BufferedInputStream bufferStream = null;
		FileInputStream fileStream = null;
		try{
			outStream = new ByteArrayOutputStream();
			fileStream = new FileInputStream(filePath);
			bufferStream = new BufferedInputStream(fileStream);
			dataStream = new DataInputStream(bufferStream);

			byte[] buf = new byte[1024];
			while (true) {
				int len = dataStream.read(buf);
				if (len < 0)
					break;
				outStream.write(buf, 0, len);
			}

			bArray = outStream.toByteArray();

		}catch(Exception ex){
			logger.error("", ex);
		}finally{
			if(fileStream != null){
				try {
					fileStream.close();
					fileStream = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if(bufferStream != null){
				try {
					bufferStream.close();
					bufferStream = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if(dataStream != null){
				try {
					dataStream.close();
					dataStream = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if(outStream != null){
				try {
					outStream.close();
					outStream = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return bArray;
	}

	public static String readStream(InputStream in){
		BufferedInputStream inStream = null;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		String result = "";
		try {
			inStream = new BufferedInputStream(inStream);

			byte[] buf = new byte[4096];
			int i;
			while ((i = inStream.read(buf)) != -1) {
				outStream.write(buf, 0, i);
			}

			byte[] bytes = outStream.toByteArray();
			result = new String(bytes);

			inStream.close();
			inStream = null;

			outStream.close();
			outStream = null;

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException ex) {

				}
			}

			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException ex) {

				}
			}
		}

		return result;
	}

	/**
	 * 从assets目录加载文件
	 * @param context
	 * @param filename
	 * @return
	 */
	public static InputStream readFromAssets(Context context, String filename){
		try{
			InputStream inputStream = context.getAssets().open(filename);
			return inputStream;
		}catch(Exception ex){
			logger.error("", ex);
		}
		return null;
	}

	/**
	 * 删除N天前的文件
	 */
	public static void deleteFiles(String absolutePath, int days){
		try{
			File fileDir = new File(absolutePath);
			if(fileDir.isDirectory()){
				File[] files = fileDir.listFiles();
				if(files != null){
					for(int i=0; i<files.length; i++){
						File file = (File)files[i];
						if((Calendar.getInstance().getTimeInMillis() - file.lastModified())/1000/60/60/24 > days){
							file.delete();
						}
					}
				}
			}
		}catch(Exception ex){
			logger.error("", ex);
		}
	}

	public static void deleteFile(String filePath){
		try{
			File file = new File(filePath);
			if(file.exists() && file.isFile()){
				file.delete();
			}
		}catch(Exception ex){
			logger.error("", ex);
		}
	}

	public static boolean copyFromAssetsToSDCard(Context context, String fromPath, String toPath){
		try {
			File toFile = new File(toPath);

			//parent path
			File parentFile = new File(toFile.getParent());
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}

			if (!toFile.exists()) {
				InputStream in = context.getAssets().open(fromPath);
				return writeFile(in, toPath);
			}

		}catch (Exception ex){
			logger.error("", ex);
		}
		return false;
	}

	public static boolean makeDir(String dirPath) {
		File file = new File(dirPath);
		if (!file.exists()) {
			return file.mkdirs();
		} else {
			return true;
		}
	}

	/**
	 * InputStream对象 转成 字节数组
	 * @param inputStream
	 * @return
	 */
	public static byte[] inputStream2ByteArray(InputStream inputStream){
		if(inputStream == null){
			return null;
		}

		ByteArrayOutputStream out = null;
		int c = 0;
		byte[] retBytes = null;
		try{
			out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			while((c=inputStream.read(buf, 0, buf.length)) > 0){
				out.write(buf, 0, c);
			}

			retBytes = out.toByteArray();

			out.close();
			out = null;
		}catch(Exception ex){
			logger.error("", ex);
		}
		return retBytes;
	}
}
