package com.app.fitsmile.fitsreminder.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.trayMinder.SmileProgressImageResult;
import com.app.fitsmile.shop.inter.RecyclerItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AlignerSmileAdapter extends RecyclerView.Adapter<AlignerSmileAdapter.ViewHolder> {

    private Context context;
    private List<SmileProgressImageResult> mList;
    RecyclerItemClickListener mClickListener;

    public AlignerSmileAdapter(Context context, List<SmileProgressImageResult> list,RecyclerItemClickListener clickListener) {
        this.context = context;
        mList=list;
        mClickListener=clickListener;

    }


    @NonNull
    @Override
    public AlignerSmileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_aligner_smile, parent, false);
        return new AlignerSmileAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlignerSmileAdapter.ViewHolder holder, int position) {
        holder.mTextAlignerDate.setText(Utils.getNewDate(context,mList.get(position).getCreated_date(),Utils.inputDate,Utils.outputDateAligner));
        if(mList.get(position).getAligner_name().isEmpty()){
            holder.mTextAlignerNo.setText(context.getResources().getString(R.string.aligner) + " #"+ mList.get(position).getAligner_no());
        }
        else {
           // holder.mTextAlignerNo.setText(mList.get(position).getAligner_name());
            holder.mTextAlignerNo.setText(context.getResources().getString(R.string.aligner) + " #"+ mList.get(position).getAligner_no());
        }

        if (mList.get(position).getCenter_image_url().equals("") && mList.get(position).getLeft_image_url().equals("") && mList.get(position).getRight_image_url().equals("") && mList.get(position).getFifth_image_url().equals("") && mList.get(position).getFifth_image_url().equals("")){
            holder.tv_view.setText(R.string.add_image);
            holder.img_View.setImageResource(R.drawable.ic_add);
            holder.mImageFront.setImageResource(R.drawable.front_teeth);
            holder.mImageLeft.setImageResource(R.drawable.left_teeth);
            holder.mImageRight.setImageResource(R.drawable.rightteeth);
            holder.mImageViewUp.setImageResource(R.drawable.up_teeth);
            holder.mImageviewDown.setImageResource(R.drawable.down_teeth);
        }
        else {
            holder.tv_view.setText(R.string.view);
            holder.img_View.setImageResource(R.drawable.ic_view);
        }


        if (!mList.get(position).getCenter_image_url().equals("")) {
            Picasso.get()
                    .load(mList.get(position).getCenter_image_url()).placeholder(R.drawable.front_teeth)
                    .into(holder.mImageFront);
            holder.mImageFront.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.setClicks(position,4);
                }
            });
        }else {
            holder.mImageFront.setVisibility(View.VISIBLE);
        }
        if (!mList.get(position).getLeft_image_url().equals("")) {
            Picasso.get()
                    .load(mList.get(position).getLeft_image_url()).placeholder(R.drawable.left_teeth)
                    .into(holder.mImageLeft);
            holder.mImageLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.setClicks(position,3);
                }
            });
        }else {
            holder.mImageLeft.setVisibility(View.VISIBLE);
        }
        if (!mList.get(position).getRight_image_url().equals("")) {
            Picasso.get()
                    .load(mList.get(position).getRight_image_url()).placeholder(R.drawable.rightteeth)
                    .into(holder.mImageRight);
            holder.mImageRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.setClicks(position,2);
                }
            });
        }else {
            holder.mImageRight.setVisibility(View.VISIBLE);
        }

        if (!mList.get(position).getFourth_image_url().equals("")) {
            Picasso.get()
                    .load(mList.get(position).getRight_image_url()).placeholder(R.drawable.up_teeth)
                    .into(holder.mImageViewUp);
            holder.mImageViewUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.setClicks(position,5);
                }
            });
        }else {
            holder.mImageViewUp.setVisibility(View.VISIBLE);
        }

        if (!mList.get(position).getFifth_image_url().equals("")) {
            Picasso.get()
                    .load(mList.get(position).getRight_image_url()).placeholder(R.drawable.down_teeth)
                    .into(holder.mImageviewDown);
            holder.mImageviewDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.setClicks(position,6);
                }
            });
        }else {
            holder.mImageviewDown.setVisibility(View.VISIBLE);
        }

        holder.img_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mList.get(position).getCenter_image_url().equals("") && mList.get(position).getLeft_image_url().equals("") && mList.get(position).getRight_image_url().equals("") && mList.get(position).getFourth_image_url().equals("") && mList.get(position).getFifth_image_url().equals("") ){
                    mClickListener.setClicks(position,7);
                }
                else {
                    mClickListener.setClicks(position,1);
                }

            }
        });



    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextAlignerNo;
        TextView mTextAlignerDate,tv_view, textRight;
        ImageView mImageFront,mImageLeft,mImageRight,mImageViewUp, mImageviewDown, img_View;
        CircleImageView mCircleImageFront,mCircleImageLeft,mCircleImageRight, mCircleImageUp, mCircleImageDown;
        CircleImageView mCircleImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextAlignerNo=itemView.findViewById(R.id.textAlignerNumber);
            mTextAlignerDate=itemView.findViewById(R.id.textAlignerDate);
            mImageFront=itemView.findViewById(R.id.imageFront);
            mImageLeft=itemView.findViewById(R.id.imageLeft);
            mImageRight=itemView.findViewById(R.id.imageRight);
            mCircleImageFront=itemView.findViewById(R.id.circleImageFront);
            mCircleImageLeft=itemView.findViewById(R.id.circleImageLeft);
            mCircleImageRight=itemView.findViewById(R.id.circleImageRight);
            mCircleImageView=itemView.findViewById(R.id.circleImageAdd);
            mCircleImageUp = itemView.findViewById(R.id.circleImageUp);
            mCircleImageDown = itemView.findViewById(R.id.circleImageDown);
            mImageViewUp = itemView.findViewById(R.id.imageUp);
            mImageviewDown = itemView.findViewById(R.id.imageDown);
            img_View=itemView.findViewById(R.id.img_view);
            textRight = itemView.findViewById(R.id.textright);
//            img_View.setMaxHeight(R.dimen._50sdp);
//            img_View.setMaxWidth(R.dimen._5sdp);
//            img_View.setPadding(10,10,10,10);
            tv_view=itemView.findViewById(R.id.tv_view);


        }
    }
}

