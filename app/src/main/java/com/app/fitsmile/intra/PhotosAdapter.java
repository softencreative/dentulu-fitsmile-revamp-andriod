package com.app.fitsmile.intra;


import android.graphics.Bitmap;
import android.graphics.Point;
import android.text.format.DateFormat;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.fitsmile.R;
import com.app.fitsmile.intra.utils.MyImageView;
import com.app.fitsmile.intra.utils.NativeImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PhotosAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {
	private Point mPoint = new Point(0, 0);
	PlaybackActivity activity;
	private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
	private GridView mGridView;
	private List<String> list;
	protected LayoutInflater mInflater;
	public SparseBooleanArray mCheckStates;
	public boolean changeStatus;

	public PhotosAdapter(PlaybackActivity activity, List<String> list, GridView mGridView, boolean changeStatus) {
		this.activity = activity;
		this.list = list;
		this.mGridView = mGridView;
		this.changeStatus = changeStatus;
		mInflater = LayoutInflater.from(activity);
		mCheckStates = new SparseBooleanArray(list.size());
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		String path = list.get(position);
		String imageName = new File(path).getName();
		long imageDate = new File(list.get(position)).lastModified();

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_gridview_photos, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.child_image);
			viewHolder.mTextView = (TextView) convertView.findViewById(R.id.child_tv);
			viewHolder.mTextViewDate = (TextView) convertView.findViewById(R.id.child_date);
			viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

			viewHolder.mImageView.setOnMeasureListener(new MyImageView.OnMeasureListener() {

				@Override
				public void onMeasureSize(int width, int height)
				{
					mPoint.set(width, height);
				}
			});

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
			viewHolder.mTextView.setText("");
		}
		viewHolder.mImageView.setTag(path);

		Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint, new NativeImageLoader.NativeImageCallBack() {
			@Override
			public void onImageLoader(Bitmap bitmap, String path) {
				ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
				if (bitmap != null && mImageView != null) {
					mImageView.setImageBitmap(bitmap);
					String imageName = new File(path).getName();
					long imageDate = new File(list.get(position)).lastModified();
					String dateString = DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date(imageDate)).toString();
					viewHolder.mTextView.setText(imageName);
					viewHolder.mTextViewDate.setText(dateString);
				}
			}
		});

		if (bitmap != null) {
			viewHolder.mImageView.setImageBitmap(bitmap);
			viewHolder.mTextView.setText(imageName);
			String dateString = DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date(imageDate)).toString();
			viewHolder.mTextViewDate.setText(dateString);
		} else {
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
			viewHolder.mTextView.setText("");
			viewHolder.mTextViewDate.setText("");
		}

		if(changeStatus) {
			viewHolder.checkBox.setVisibility(View.VISIBLE);
			viewHolder.checkBox.setChecked(false);
		} else {
			viewHolder.checkBox.setVisibility(View.GONE);
			viewHolder.checkBox.setChecked(true);
		}

		viewHolder.checkBox.setTag(position);
		//viewHolder.checkBox.setChecked(mCheckStates.get(position, false));
		viewHolder.checkBox.setOnCheckedChangeListener(this);
		return convertView;
	}

	public boolean isChecked(int position) {
		return mCheckStates.get(position, false);
	}

	public void setChecked(int position, boolean isChecked) {
		mCheckStates.put(position, isChecked);

	}

	public void toggle(int position) {
		setChecked(position, !isChecked(position));
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		mCheckStates.put((Integer) buttonView.getTag(), isChecked);
	}

	public List<Integer> getSelectItems() {
		List<Integer> list = new ArrayList<Integer>();
		for (Iterator<Map.Entry<Integer, Boolean>> it = mSelectMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer, Boolean> entry = it.next();
			if (entry.getValue()) {
				list.add(entry.getKey());
			}
		}

		return list;
	}

	public void viewCheckBox() {
		changeStatus=true;
	}

	public class ViewHolder {
		public MyImageView mImageView;
		public TextView mTextView;
		public TextView mTextViewDate;
		public CheckBox checkBox;
	}
}