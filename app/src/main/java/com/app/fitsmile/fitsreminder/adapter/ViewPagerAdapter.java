package com.app.fitsmile.fitsreminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.app.fitsmile.R;
import com.app.fitsmile.response.trayMinder.SmileProgressImageResult;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
 
    private Context mContext;
    private List<SmileProgressImageResult> mResources;
 
    public ViewPagerAdapter(Context mContext, List<SmileProgressImageResult> mResources) {
        this.mContext = mContext;
        this.mResources = mResources;
    }
 
    @Override
    public int getCount() {
        return mResources.size();
    }
 
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }
 
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_view_aligner_images, container, false);
 
        TextView imageView =itemView.findViewById(R.id.textAlignerImageNumber);
        imageView.setText(mResources.get(position).getAligner_no());
 
        container.addView(itemView);
 
        return itemView;
    }
 
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}