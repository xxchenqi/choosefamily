package com.eju.zejia.camera;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.eju.zejia.common.RequestCodeConstant;
import com.eju.zejia.utils.DateUtil;
import com.eju.zejia.utils.FileUtil;

import java.io.File;
import java.io.IOException;


/**
 * 照片操作
 * 
 * @author Neng
 * 
 */
public class CameraImage {

	private Activity mActivity;
	/**
	 * 拍照的照片存储位置
	 */
	private File PHOTO_DIR = null;
	/**
	 * 拍照照片名称
	 */
	private static final String PHOTO_NAME = "clocktemp.jpg";
	/**
	 * 拍照
	 */
	public static final int CAMERA_WITH_DATA = RequestCodeConstant.CAMERA_WITH_DATA;
	/**
	 * 相册
	 */
	public static final int PHOTO_REQUEST_GALLERY = RequestCodeConstant.PHOTO_REQUEST_GALLERY;

	/**
	 * 用来标识拍照剪切后的结果
	 */
	public static final int PHOTO_REQUEST_CUT = RequestCodeConstant.PHOTO_REQUEST_CUT;
	/**
	 * 拍照临时文件
	 */
	public static File tempFile;

	public CameraImage(Activity mActivity) {
		super();
		this.mActivity = mActivity;
		getPhotoDir();
	}

	/**
	 * 拍照
	 */
	public void doTakePhoto() {
		if (PHOTO_DIR != null && PHOTO_DIR.exists()) {
			Intent intent_camera = mActivity.getPackageManager()
	                .getLaunchIntentForPackage("com.android.camera");
			Intent intent;
			if(intent_camera != null){
				intent = new Intent();
				intent.setPackage("com.android.camera");
				intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			}else{
				// 激活相机
				intent = new Intent("android.media.action.IMAGE_CAPTURE");
			}
			// 判断存储卡是否可以用，可用进行存储
			/* 头像名称 */
			tempFile = new File(PHOTO_DIR, PHOTO_NAME);
			try {
				tempFile.createNewFile();
				judgFlie();
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
				// 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
				mActivity.startActivityForResult(intent, CAMERA_WITH_DATA);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 相册
	 */
	public void gallery() {
		// 激活系统图库，选择一张图片
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		// 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
		mActivity.startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
	}

	/**
	 * 剪切图片
	 * 
	 * @param uri
	 *            地址
	 * @param width
	 *            W
	 * @param height
	 *            H
	 * @return
	 */
	public void crop(Uri uri, int width, int height) {
		// 裁剪图片意图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// 裁剪框的比例，1：1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 裁剪后输出图片的尺寸大小
		intent.putExtra("outputX", width);
		intent.putExtra("outputY", height);
		tempFile = getTempFile();
		judgFlie();
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
		intent.putExtra("outputFormat", "JPEG");// 图片格式
		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", false);
		mActivity.startActivityForResult(intent, CameraImage.PHOTO_REQUEST_CUT);
	}

	/**
	 * 获取File
	 * 
	 * @return
	 */
	private File getTempFile() {
		if (FileUtil.isHasSdcard()) {
			// 判断目录是否存在
			if (!PHOTO_DIR.exists()) {
				PHOTO_DIR.mkdirs();// 创建照片的存储目录
			}
			String fileName = DateUtil.getCurrentDate("yyyyMMddHHmmss")
					+ ".jpg";
			File f = new File(PHOTO_DIR, fileName);
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.toString();
			}
			return f;
		}
		return null;
	}

	/**
	 * 获得目录
	 */
	private File getPhotoDir() {
		if (PHOTO_DIR == null) {
			if (FileUtil.isHasSdcard()) {
				PHOTO_DIR = new File(Environment.getExternalStorageDirectory()
						+ "/clock/CameraCache");
				if (!PHOTO_DIR.exists()) {
					PHOTO_DIR.mkdirs();// 创建照片的存储目录
				}
			}
		}
		return PHOTO_DIR;
	}

	/**
	 * 处理文件存在问题
	 */
	private void judgFlie() {
		if (!tempFile.exists()) {
			tempFile.mkdirs();
			try {
				tempFile.createNewFile();
			} catch (Exception e) {
				e.toString();
			}
		}
	}
	
	public static void cleanPhotoCacheDir(){
		FileUtil.cleanDir("/zejia/CameraCache");
	}

}
