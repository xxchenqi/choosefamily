package com.eju.zejia.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {

	public static boolean existsFile(String fileName) {
		String pathName = Environment.getExternalStorageDirectory()
				+ "/zejia/";
		File file = new File(pathName + fileName);
		return file.exists();
	}

	/**
	 * 判断sdcard是否被挂载
	 * 
	 * @return
	 */
	public static boolean isHasSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}
	
	public static void writeFileToSD(String text, String fileName) {
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
			return;
		}
		try {

			String pathName = Environment.getExternalStorageDirectory()
					+ "/clock/";
			File path = new File(pathName);
			File file = new File(pathName + fileName);
			if (!path.exists()) {
				path.mkdir();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream stream = new FileOutputStream(file);
			byte[] buf = text.getBytes();
			stream.write(buf);
			stream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	// 读取sd中的文件
//	public static String readSDCardFile(String path) throws IOException {
//		File file = new File(path);
//		FileInputStream fis = new FileInputStream(file);
//		return streamRead(fis);
//	}
//
//	// 在res目录下建立一个raw资源文件夹，这里的文件只能读不能写入。。。
//	public static String readRawFile(Context context, int fileId)
//			throws IOException {
//		// 取得输入流
//		InputStream is = context.getResources().openRawResource(fileId);
//		// 返回一个字符串
//		return streamRead(is);
//	}

//	private static String streamRead(InputStream is) throws IOException {
//		int buffersize = is.available();// 取得输入流的字节长度
//		byte buffer[] = new byte[buffersize];
//		is.read(buffer);// 将数据读入数组
//		is.close();// 读取完毕后要关闭流。
//		// 设置取得的数据编码，防止乱码
//		return EncodingUtils.getString(buffer, "UTF-8");
//	}

	// 在assets文件夹下的文件，同样是只能读取不能写入
//	public static String readAssetsFile(Context context, String filename)
//			throws IOException {
//		// 取得输入流
//		InputStream is = context.getResources().getAssets().open(filename);
//		// 返回一个字符串
//		return streamRead(is);
//	}

	// 往sd卡中写入文件
	public static void writeSDCardFile(String path, byte[] buffer)
			throws IOException {
		File file = new File(path);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(buffer);// 写入buffer数组。如果想写入一些简单的字符，可以将String.getBytes()再写入文件;
		fos.close();
	}

	// 将文件写入应用的data/data的files目录下
	public static void writeDateFile(Context context, String fileName,
			byte[] buffer) throws Exception {
		byte[] buf = fileName.getBytes("iso8859-1");
		fileName = new String(buf, "utf-8");
		// Context.MODE_PRIVATE：为默认操作模式，代表该文件是私有数据，只能被应用本身访问，在该模式下，写入的内容会覆盖原文件的内容，如果想把新写入的内容追加到原文件中。可以使用Context.MODE_APPEND
		// Context.MODE_APPEND：模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件。
		// Context.MODE_WORLD_READABLE和Context.MODE_WORLD_WRITEABLE用来控制其他应用是否有权限读写该文件。
		// MODE_WORLD_READABLE：表示当前文件可以被其他应用读取；MODE_WORLD_WRITEABLE：表示当前文件可以被其他应用写入。
		// 如果希望文件被其他应用读和写，可以传入：
		// openFileOutput("output.txt", Context.MODE_WORLD_READABLE +
		// Context.MODE_WORLD_WRITEABLE);
		FileOutputStream fos = context.openFileOutput(fileName,
				Context.MODE_APPEND);// 添加在文件后面
		fos.write(buffer);
		fos.close();
	}

	// 读取应用的data/data的files目录下文件数据
//	public static String readDateFile(Context context, String fileName)
//			throws Exception {
//		FileInputStream fis = context.openFileInput(fileName);
//		// 返回一个字符串
//		return streamRead(fis);
//	}

	public static File readAssetes(Context context, String name) {
		InputStream is;
		String path = Environment.getExternalStorageDirectory()
				+ "/clock/CameraCache";
		File file = getFilePath(path, name);
		try {
			is = context.getResources().getAssets().open(name);
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[8192];
			int count;
			while ((count = is.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}
			fos.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	private static File getFilePath(String filePath, String fileName) {
		File file = null;
		makeRootDirectory(filePath);
		try {
			file = new File(filePath, fileName);
			if(!file.exists()){
				file.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	private static void makeRootDirectory(String filePath) {
		File file;
		try {
			file = new File(filePath);
			if (!file.exists()) {
				file.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void cleanDir(String relativePaht){
		if (isHasSdcard()) {
			File photoDir = new File(Environment.getExternalStorageDirectory()
					+ relativePaht);
			if (!photoDir.exists() || !photoDir.isDirectory()) {
				return;
			}
			for (File file : photoDir.listFiles()) {
				if (file.isFile())
		        	file.delete();
			}
			photoDir.delete();
		}
	}
}
