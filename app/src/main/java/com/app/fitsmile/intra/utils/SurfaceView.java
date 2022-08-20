package com.app.fitsmile.intra.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

public class SurfaceView extends android.view.SurfaceView implements Callback {
	private SurfaceHolder m_holder = null;

	private Canvas m_canvas = null;
	private Paint m_paint = null;
	
	private Rect rect = null;

	public SurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		m_holder = getHolder();
		m_holder.addCallback(this);
		m_paint = new Paint();
		m_paint.setColor(Color.BLUE);
		m_paint.setAntiAlias(true);
		m_holder.setFormat(PixelFormat.TRANSPARENT);
		rect = new Rect(0, 0, this.getWidth(), this.getHeight());
	}

	public void SetBitmap(final Bitmap bmp) {
		if (bmp != null) {
			float degrees = 180; //rotation degree
			Matrix matrix = new Matrix();
			matrix.setRotate(degrees);
			Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
			m_canvas = m_holder.lockCanvas(rect);
			if (m_canvas == null)
				return;
			m_paint = new Paint();
			m_canvas.drawBitmap(bitmap, null, rect, m_paint);
			if (m_holder != null)
				m_holder.unlockCanvasAndPost(m_canvas);
		}
	}

	

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{

	}

	public void Stop()
	{

	}
}
