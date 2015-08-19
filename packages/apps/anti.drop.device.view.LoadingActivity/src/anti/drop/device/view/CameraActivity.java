package anti.drop.device.view;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import anti.drop.device.BaseApplication;
import anti.drop.device.R;
import anti.drop.device.utils.BluetoothLeClass;
import anti.drop.device.utils.BluetoothLeClass.camera_data_listener;
import anti.drop.device.utils.SharedPreferencesUtils;

/**
 * AUTHER wzb<wangzhibin_x@foxmail.com> 2015-8-5ÏÂÎç12:15:04
 */
public class CameraActivity extends Activity implements SurfaceHolder.Callback {

	private static final String TAG = "wzb";
	private static final boolean D = true;
	private int displayFrameLag = 0;
	private long lastMessageTime = 0;
	private long displayTimeLag = 0;

	private SurfaceHolder mSurfaceHolder;
	private SurfaceView mSurfaceView;
	private ImageView mImageView;
	private Camera mCamera;
	public int mCameraOrientation;
	public boolean mPreviewRunning = false;
	private boolean readyToProcessImage = true;
	static boolean camera_flag = true;

	private static int currentCamera = Camera.CameraInfo.CAMERA_FACING_BACK;
	private static String currentFlashMode = Camera.Parameters.FLASH_MODE_OFF;

	private BluetoothLeClass mBLE_camera;
	BaseApplication app;
	boolean mPreviewFlag=false;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SharedPreferencesUtils.getInstanse(this).setIsEnter(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (D)
			Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_layout);

		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mImageView = (ImageView) findViewById(R.id.imageView);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		app = (BaseApplication) getApplication();
		mBLE_camera = app.get_ble();
		if (!mBLE_camera.initialize()) {
			Log.d("wzb", "error");
		}
		mBLE_camera.set_camera_data_listener(m_camera_data_listener);
		camera_flag = true;

		lastMessageTime = System.currentTimeMillis();

		Timer mTimer = new Timer();
		mTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (displayFrameLag > 1) {
					displayFrameLag--;
				}
				if (displayTimeLag > 1000) {
					displayTimeLag -= 1000;
				}
			}
		}, 0, 1000);
		
		SharedPreferencesUtils.getInstanse(this).setIsEnter(true);
		
	}
	
	
	
	Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case 0:
				doSnap();
				break;
				default:
					break;
			}
		}
	};

	private BluetoothLeClass.camera_data_listener m_camera_data_listener = new camera_data_listener() {

		@Override
		public void camera_data(String value) {
			Log.d("wzb", "camera data=" + value+"mPreviewFlag="+mPreviewFlag);
			if(mPreviewFlag){
			if (value.equals("b1") || value.equals("b2")) {
				mHandler.sendEmptyMessage(0);
			}
			}
		}
	};

	public void setCameraDisplayOrientation() {
		Camera.CameraInfo info = new Camera.CameraInfo();
		mCamera.getCameraInfo(currentCamera, info);
		int rotation = this.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}
		int resultA = 0, resultB = 0;
		if (currentCamera == Camera.CameraInfo.CAMERA_FACING_BACK) {
			resultA = (info.orientation - degrees + 360) % 360;
			resultB = (info.orientation - degrees + 360) % 360;
			mCamera.setDisplayOrientation(resultA);
		} else {
			resultA = (360 + 360 - info.orientation - degrees) % 360;
			resultB = (info.orientation + degrees) % 360;
			mCamera.setDisplayOrientation(resultA);
		}
		Camera.Parameters params = mCamera.getParameters();
		params.setRotation(resultB);
		mCamera.setParameters(params);
		mCameraOrientation = resultB;
	}

	public void doFlash(int arg0) {
		if (arg0 == 0)
			currentFlashMode = Camera.Parameters.FLASH_MODE_OFF;
		else if (arg0 == 1)
			currentFlashMode = Camera.Parameters.FLASH_MODE_AUTO;
		else if (arg0 == 2)
			currentFlashMode = Camera.Parameters.FLASH_MODE_ON;

		if ((mCamera != null) && mPreviewRunning) {
			Camera.Parameters p = mCamera.getParameters();
			p.setFlashMode(currentFlashMode);
			mCamera.setParameters(p);
		}
	}

	public void doSwitch(int arg0) {
		Log.d(TAG, String.format("doSwitch(%d)", arg0));

		int oldCurrentCamera = currentCamera;

		if (Camera.getNumberOfCameras() >= 2) {
			if (arg0 == 1) {
				currentCamera = Camera.CameraInfo.CAMERA_FACING_FRONT;
			} else {
				currentCamera = Camera.CameraInfo.CAMERA_FACING_BACK;
			}
		} else {
			currentCamera = Camera.CameraInfo.CAMERA_FACING_BACK;
		}

		if ((oldCurrentCamera != currentCamera) && mPreviewRunning) {
			surfaceDestroyed(mSurfaceHolder);
			if (arg0 == 0) {
				surfaceCreated(mSurfaceHolder);
				surfaceChanged(mSurfaceHolder, 0, 0, 0);
			} else {
				surfaceCreated(mSurfaceHolder);
				surfaceChanged(mSurfaceHolder, 0, 0, 0);
			}
		}
	}

	public void doSnap() {
		mPreviewFlag=false;
		if (mCamera == null || !mPreviewRunning) {
			if (D)
				Log.d(TAG, "tried to snap when camera was inactive");
			return;
		}
		
		Log.d("wzb","do snap=================");
		Camera.Parameters params = mCamera.getParameters();
		List<Camera.Size> sizes = params.getSupportedPictureSizes();
		Camera.Size size = sizes.get(0);
		for (int i = 0; i < sizes.size(); i++) {
			if (sizes.get(i).width > size.width)
				size = sizes.get(i);
		}
		params.setPictureSize(size.width, size.height);
		mCamera.setParameters(params);
		Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
			public void onPictureTaken(byte[] data, Camera camera) {
				FileOutputStream outStream = null;
				try {
					String filename = String.format(
							"/sdcard/DCIM/Camera/img_wear_%d.jpg",
							System.currentTimeMillis());
					outStream = new FileOutputStream(filename);
					outStream.write(data);
					outStream.close();
					if (D)
						Log.d(TAG, "wrote bytes: " + data.length);
					sendBroadcast(new Intent(
							Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
							Uri.parse("file://" + filename)));
					BitmapFactory.Options opts = new BitmapFactory.Options();
					opts.inSampleSize = 4;
					Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,
							data.length, opts);
					mImageView.setImageBitmap(bmp);
					mImageView.setX(0);
					mImageView.setRotation(0);
					mImageView.setVisibility(View.VISIBLE);
					mImageView.animate().setDuration(500)
							.translationX(mImageView.getWidth()).rotation(40)
							.withEndAction(new Runnable() {
								public void run() {
									mImageView.setVisibility(View.GONE);
								}
							});
					int smallWidth, smallHeight;
					int dimension = 280;
					if (bmp.getWidth() > bmp.getHeight()) {
						smallWidth = dimension;
						smallHeight = dimension * bmp.getHeight()
								/ bmp.getWidth();
					} else {
						smallHeight = dimension;
						smallWidth = dimension * bmp.getWidth()
								/ bmp.getHeight();
					}
					Bitmap bmpSmall = Bitmap.createScaledBitmap(bmp,
							smallWidth, smallHeight, false);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					bmpSmall.compress(Bitmap.CompressFormat.WEBP, 50, baos);
					// sendToWearable("result", baos.toByteArray(), null);
					mCamera.startPreview();
					mPreviewFlag=true;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		mCamera.takePicture(null, null, jpegCallback);
	}

	public void surfaceView_onClick(View view) {
		Log.d("wzb","click");
		doSnap();
		
	}

	@Override
	public void onPause() {
		Log.d(TAG, "onPause");
		SharedPreferencesUtils.getInstanse(this).setIsEnter(false);
		super.onPause();
	}

	@Override
	public void onResume() {
		Log.d(TAG, "onResume");
		lastMessageTime = System.currentTimeMillis();
		SharedPreferencesUtils.getInstanse(this).setIsEnter(true);
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		SharedPreferencesUtils.getInstanse(this).setIsEnter(false);
		super.onStop();
	}
	
	
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

		if (mPreviewRunning) {
			mCamera.stopPreview();
		}
		if (mSurfaceHolder.getSurface() == null) {
			return;
		}
		Camera.Parameters p = mCamera.getParameters();
		List<String> focusModes = p.getSupportedFocusModes();
		if (focusModes
				.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
			p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		}
		p.setFlashMode(currentFlashMode);
		mCamera.setParameters(p);
		try {
			if (mCamera != null) {
				mCamera.setPreviewDisplay(arg0);
				setCameraDisplayOrientation();
				mCamera.setPreviewCallback(new Camera.PreviewCallback() {
					public void onPreviewFrame(byte[] data, Camera arg1) {
						if (readyToProcessImage
								&& mPreviewRunning
								&& displayFrameLag < 6
								&& displayTimeLag < 2000
								&& System.currentTimeMillis() - lastMessageTime < 4000) {
							readyToProcessImage = false;
							Camera.Size previewSize = mCamera.getParameters()
									.getPreviewSize();

							int[] rgb = decodeYUV420SP(data, previewSize.width,
									previewSize.height);
							Bitmap bmp = Bitmap.createBitmap(rgb,
									previewSize.width, previewSize.height,
									Bitmap.Config.ARGB_8888);
							int smallWidth, smallHeight;
							int dimension = 200;
							// stream is lagging, cut resolution and catch up
							if (displayTimeLag > 1500) {
								dimension = 50;
							} else if (displayTimeLag > 500) {
								dimension = 100;
							} else {
								dimension = 200;
							}
							if (previewSize.width > previewSize.height) {
								smallWidth = dimension;
								smallHeight = dimension * previewSize.height
										/ previewSize.width;
							} else {
								smallHeight = dimension;
								smallWidth = dimension * previewSize.width
										/ previewSize.height;
							}

							Matrix matrix = new Matrix();
							matrix.postRotate(mCameraOrientation);

							Bitmap bmpSmall = Bitmap.createScaledBitmap(bmp,
									smallWidth, smallHeight, false);
							Bitmap bmpSmallRotated = Bitmap.createBitmap(
									bmpSmall, 0, 0, smallWidth, smallHeight,
									matrix, false);
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							bmpSmallRotated.compress(
									Bitmap.CompressFormat.WEBP, 30, baos);
							displayFrameLag++;

							bmp.recycle();
							bmpSmall.recycle();
							bmpSmallRotated.recycle();
							readyToProcessImage = true;
						}
					}
				});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCamera.startPreview();
		mPreviewRunning = true;
		mPreviewFlag=true;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open(currentCamera);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mPreviewRunning = false;
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
		}
	}

	public int[] decodeYUV420SP(byte[] yuv420sp, int width, int height) {
		final int frameSize = width * height;
		int rgb[] = new int[width * height];
		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) {
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y < 0)
					y = 0;
				if ((i & 1) == 0) {
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}
				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);
				if (r < 0)
					r = 0;
				else if (r > 262143)
					r = 262143;
				if (g < 0)
					g = 0;
				else if (g > 262143)
					g = 262143;
				if (b < 0)
					b = 0;
				else if (b > 262143)
					b = 262143;
				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
						| ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}
		return rgb;
	}

}
