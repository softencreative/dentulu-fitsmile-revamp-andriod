package com.app.fitsmile.intra.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.util.Log;


import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StreamSelf
{
	protected final static String TAG = StreamSelf.class.getSimpleName();
	private Context ctx;
	private SurfaceView mSurfaceView;
	private Handler mHandler;
	private NativeLibs mNativeLibs;

	private Media mMedia;

    public static int mVideoWidth = 0;
    public static int mVideoHeight = 0;

	private VideoParams mVideoParams = new VideoParams();

	private boolean isRunning = false;
	public static boolean isNeedTakePhoto = false;
	private boolean isNeedRecord = false;
	private boolean isRecording = false;
	private boolean isNewFrame = false;

	private byte[] recordData = null;
	private Bitmap recordBmp = null;
	// for thread pool
	private static final int CORE_POOL_SIZE = 3; // initial/minimum threads
	private static final int MAX_POOL_SIZE = 5; // maximum threads
	private static final int KEEP_ALIVE_TIME = 10; // time periods while keep
													// the idle thread
	protected static final ThreadPoolExecutor EXECUTER = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
			KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

	public StreamSelf(Context ctx, SurfaceView view, Handler handler)
	{
		// TODO Auto-generated constructor stub
		this.mSurfaceView = view;
		this.ctx = ctx;
		this.mHandler = handler;
		mMedia = new Media(ctx);
		mNativeLibs = new NativeLibs();
	}

    public NativeLibs getmNativeLibs() {
        return mNativeLibs;
    }

	public void stopStream() {
		isRecording = false;
		isNeedRecord = false;
		isRunning = false;
        NativeLibs.nativeAVIRecStop();
	}

	public void destroy() {
		stopStream();
		mNativeLibs.destroyCamera();
	}

	public void startStream() {
		if(isRunning)
			return;

		EXECUTER.execute(new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				isRunning = true;

				mNativeLibs.startPreview();

				do {
					mVideoParams.video_format = mNativeLibs.getVideoFormat(mVideoParams);
					msleep(200);
				}
				while (mVideoParams.video_format <= 0);

                mVideoWidth = mVideoParams.video_width;
                mVideoHeight = mVideoParams.video_height;

                doExecuteMJPEG();

				mNativeLibs.stopPreview();
			}
		});
	}

	public int getVideoWidth() {
		return mVideoParams.video_width;
	}

	public void takePhoto() {
		if (PathConfig.getSdcardAvilibleSize() > 100)
			isNeedTakePhoto = true;
		else
			mHandler.sendEmptyMessage(HandlerParams.SDCARD_FULL);
	}

	public void takeRecord() {
		if (isRecording) {
			isRecording = false;
			isNeedRecord = false;
			NativeLibs.nativeAVIRecStop();
		} else {

			if (PathConfig.getSdcardAvilibleSize() > 300) {
				isNeedRecord = true;
			} else {
				mHandler.sendEmptyMessage(HandlerParams.SDCARD_FULL);
			}
		}
	}

	private void setRecordData(byte[] data, Bitmap bmp) {
		if(!isNewFrame) {
			isNewFrame = true;
			this.recordData = data;
			this.recordBmp = bmp;
		}
	}


	private void startRecordThread(Bitmap bmp) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
		long time = System.currentTimeMillis();
		Date curDate = new Date(time);
		String timeString = format.format(curDate);
		String photoName = timeString + ".jpg";
		String videoName = timeString + ".avi";
		// take a snapshot as the video's thumbnail picture
		PathConfig.savePhotoBmp(ctx, PathConfig.getRootPath() + "/" + PathConfig.VIDEOS_PATH + "/" + timeString,
				photoName, bmp);

		final String videoPath = PathConfig.getRootPath() + "/" + PathConfig.VIDEOS_PATH + "/" + timeString + "/" + videoName;

		mHandler.sendEmptyMessage(HandlerParams.RECORD_START);

		NativeLibs.nativeAVIRecSetParams(bmp.getWidth(), bmp.getHeight(), 15);
		NativeLibs.nativeAVIRecStart(videoPath);

		EXECUTER.execute(new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.e(TAG, " EXECUTER RUN NOW.... " + isRecording);
				while (isRecording) {

					if (isNewFrame) {
						if (mVideoParams.video_format == NativeLibs.FORMAT_VIDEO_MJPEG)
							NativeLibs.nativeAVIRecAddData(recordData, recordData.length);
						else {
							//long time = System.currentTimeMillis();
							//这里弃用老的Native中的bmp2jpeg的方法，在小米2等手机测试录像花屏，改用java自带API压缩jpg
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							recordBmp.compress(Bitmap.CompressFormat.JPEG, 85, bos);
							byte[] recordData = bos.toByteArray();
							NativeLibs.nativeAVIRecAddData(recordData, recordData.length);
							//Log.d(TAG, "cost time:" + (System.currentTimeMillis() - time));
						}

						isNewFrame = false;
					}
					msleep(5);
				}

                NativeLibs.nativeAVIRecStop();
				mHandler.sendEmptyMessage(HandlerParams.RECORD_STOP);
                msleep(50);
			}
		});
	}

	private void msleep(int millisec) {
		try {
			Thread.sleep(millisec);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void doExecuteMJPEG() {
		Bitmap bmpBitmap = null;
		while (isRunning) {
			byte[] data = mNativeLibs.getOneFrameBuffer();
			if (data == null) {
				msleep(5);
				continue;
			}

			bmpBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			mSurfaceView.SetBitmap(bmpBitmap);

			if (isNeedTakePhoto) {
				Bitmap bitmap = rotateBitmap(bmpBitmap);
				PathConfig.savePhoto(ctx, bitmap);
				mMedia.playShutter();
				isNeedTakePhoto = false;
			}

			if (isNeedRecord) {
				isNeedRecord = false;
				isRecording = true;
				startRecordThread(rotateBitmap(bmpBitmap));
			}

			if (isRecording) {
				setRecordData(data, null);
			}
		}

        if(bmpBitmap != null)
            bmpBitmap.recycle();
	}

	public class VideoParams {
		public int video_format;
		public int video_width;
		public int video_height;
	}

	private Bitmap rotateBitmap(Bitmap bmpBitmap) {
		float degrees = 180; //rotation degree
		Matrix matrix = new Matrix();
		matrix.setRotate(degrees);
		Bitmap bitmap = Bitmap.createBitmap(bmpBitmap, 0, 0, bmpBitmap.getWidth(), bmpBitmap.getHeight(), matrix, true);
		return bitmap;
	}
}
