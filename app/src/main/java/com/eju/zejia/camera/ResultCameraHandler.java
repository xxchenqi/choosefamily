package com.eju.zejia.camera;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.eju.zejia.utils.FileUtil;
import com.eju.zejia.utils.ImageUtil;
import com.eju.zejia.utils.StringUtils;
import com.eju.zejia.utils.UIUtils;

import java.io.File;


/**
 * 相机返回处理类
 * 
 */
public class ResultCameraHandler {
	private static ResultCameraHandler _sInstance;
	private static boolean isCrop;
	private int width = 320;
	private int height = 320;

	private ResultCameraHandler() {
		super();
	}

	public static ResultCameraHandler getInstance() {
		if (_sInstance == null) {
			_sInstance = new ResultCameraHandler();
		}
		isCrop = false;
		return _sInstance;
	}

	public void getPhotoFile(Activity mActivity, Intent data, int requestCode,
			int resultCode, CameraImage cameraImage, CameraResult cameraResult) {
		Uri uriFile = null;
		if (resultCode == 0) {
			return;
		}
		if (data != null) {
			uriFile = data.getData();
		}
		if (uriFile == null) {
			if(CameraImage.tempFile != null){
				uriFile = Uri.fromFile(CameraImage.tempFile);
			}
		}
		if (uriFile != null) {
			switch (requestCode) {
			case CameraImage.CAMERA_WITH_DATA: // 照相功能
				if (isCrop) {
					cameraImage.crop(uriFile, width, height);
				} else {
					cameraResult.result(ImageUtil.zoomFileXY(CameraImage.tempFile, 720, 1280));
				}
				break;
			case CameraImage.PHOTO_REQUEST_CUT: // 剪切完毕
				cameraResult.result(CameraImage.tempFile);
				break;
			case CameraImage.PHOTO_REQUEST_GALLERY: // 相册
				dataHandler(mActivity, isCrop, cameraImage, width, height,
						cameraResult, uriFile);
				break;
			}
		} else {
			UIUtils.showToastSafe("获取图片失败!");
		}
	}

	private void dataHandler(Activity mActivity, boolean isCrop,
			CameraImage cameraImage, int width, int height,
			CameraResult cameraResult, Uri uriFile) {
		if (!FileUtil.isHasSdcard()) {
			UIUtils.showToastSafe("未找到存储卡！");
		} else {
			if (isCrop) {
				cameraImage.crop(uriFile, width, height);
			} else {
				String path = ImageUtil.getRealPathFromURI(mActivity, uriFile);
				if(StringUtils.isNotNullString(path)){
					File file = new File(path);
					cameraResult.result(ImageUtil.zoomFileXY(file, 720, 1280));
				}
			}
		}
	}

	public ResultCameraHandler setCrop(boolean isCrop) {
		ResultCameraHandler.isCrop = isCrop;
		return _sInstance;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public interface CameraResult {
		void result(File file);
	}

}
