package com.mengdd.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.mengdd.arapp.R;
import com.mengdd.components.ViewModel;
import com.mengdd.utils.AppConstants;
import com.mengdd.utils.FileUtils;
import com.mengdd.utils.FileUtils.MediaType;

/**
 * 
 * Camera Module including the Camera and Camera Preview.
 * 
 * @author Dandan Meng <mengdandanno1@163.com>
 * @version 1.0
 * @since 2013-07-01
 * 
 */
public class CameraViewModel extends ViewModel
{
	private Camera mCamera = null;
	private CameraPreview mCameraPreview = null;
	private View mRootView = null;

	private int mNumberOfCameras;
	private int mCameraCurrentlyLocked;

	// The first rear facing camera
	private int mDefaultCameraId;



	public CameraViewModel(Activity activity)
	{
		super(activity);

	}

	@Override
	public View getView()
	{
		return mRootView;
	}

	@Override
	public void onCreate(Intent intent)
	{
		super.onCreate(intent);

		mRootView = mInflater.inflate(R.layout.camera_view_model, null);

		// Add CameraPreview to layout
		mCameraPreview = new CameraPreview(mActivity);

		FrameLayout preview = (FrameLayout) mRootView
				.findViewById(R.id.camera_preview);
		preview.addView(mCameraPreview, 0);

		// 使用按钮进行拍摄动作监听
		Button captureButton = (Button) mRootView
				.findViewById(R.id.button_capture);
		captureButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mCamera.takePicture(null, null, mPictureCallback);
			}
		});

		// 获取相机id
		mDefaultCameraId = getDefaultCameraId();

		mCameraCurrentlyLocked = mDefaultCameraId;
	}

	@Override
	public void onResume(Intent intent)
	{
		super.onResume(intent);

		mCamera = getCameraInstance(mCameraCurrentlyLocked);
		mCameraPreview.setCamera(mCamera);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		if (null != mCamera)
		{
			mCameraPreview.setCamera(null);
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void onStop()
	{
		super.onStop();
	}

	@Override
	public void onDestory()
	{
		super.onDestory();
	}

	/**
	 * 得到默认相机的ID
	 * 
	 * @return
	 */
	private int getDefaultCameraId()
	{
		Log.d(AppConstants.LOG_TAG, "getDefaultCameraId");
		int defaultId = -1;

		// Find the total number of cameras available
		mNumberOfCameras = Camera.getNumberOfCameras();

		// Find the ID of the default camera
		CameraInfo cameraInfo = new CameraInfo();
		for (int i = 0; i < mNumberOfCameras; i++)
		{
			Camera.getCameraInfo(i, cameraInfo);
			Log.d(AppConstants.LOG_TAG, "camera info, orientation: "
					+ cameraInfo.orientation);
			if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK)
			{
				defaultId = i;
			}
		}
		if (-1 == defaultId)
		{
			if (mNumberOfCameras > 0)
			{
				// 如果没有后向摄像头
				defaultId = 0;
			}
			else
			{
				// 没有摄像头
				Toast.makeText(mActivity, R.string.no_camera, Toast.LENGTH_LONG)
						.show();
			}
		}
		return defaultId;
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(int cameraId)
	{
		Log.d(AppConstants.LOG_TAG, "getCameraInstance");
		Camera c = null;
		try
		{
			c = Camera.open(cameraId); // attempt to get a Camera instance
		}
		catch (Exception e)
		{

			e.printStackTrace();
			Log.e(AppConstants.LOG_TAG, "Camera is not available");
		}
		return c; // returns null if camera is unavailable
	}

	private PictureCallback mPictureCallback = new PictureCallback()
	{

		@Override
		public void onPictureTaken(byte[] data, Camera camera)
		{
			Log.d(AppConstants.LOG_TAG, "onPictureTaken");

			File pictureFile = FileUtils.getOutputMediaFile(MediaType.Image);
			if (null == pictureFile)
			{
				Log.d(AppConstants.LOG_TAG,
						"Error creating media file, check storage permissions: ");
				return;
			}

			try
			{
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
			}
			catch (FileNotFoundException e)
			{
				Log.d(AppConstants.LOG_TAG, "File not found: " + e.getMessage());
			}
			catch (IOException e)
			{
				Log.d(AppConstants.LOG_TAG,
						"Error accessing file: " + e.getMessage());
			}

			// 拍照后重新开始预览
			mCamera.stopPreview();
			mCamera.startPreview();
		}
	};

	/** Check if this device has a camera */
	public boolean checkCameraHardware(Context context)
	{
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA))
		{
			// this device has a camera
			return true;
		}
		else
		{
			// no camera on this device
			return false;
		}
	}
	
	
	public void setCameraOrientation(int degree)
	{
		mCameraPreview.setDegree(degree);
	}

}