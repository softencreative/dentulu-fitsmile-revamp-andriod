package com.app.fitsmile.appointment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.response.photoConsultancy.PhotoConsultancyDetail.QuestionImages;
import com.app.fitsmile.shop.inter.RecyclerItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoPatientTeethImageAdapter extends RecyclerView.Adapter<PhotoPatientTeethImageAdapter.ViewHolder> {

    private Context context;
    private List<QuestionImages> mList;
    RecyclerItemClickListener mClickListener;

    public PhotoPatientTeethImageAdapter(Context context, List<QuestionImages> list, RecyclerItemClickListener itemClickListener) {
        this.context = context;
        mList = list;
        mClickListener=itemClickListener;
    }


    @NonNull
    @Override
    public PhotoPatientTeethImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_patient_teeth_image, parent, false);
        return new PhotoPatientTeethImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoPatientTeethImageAdapter.ViewHolder holder, int position) {
        Picasso.get().load(mList.get(position).getValue()).into(holder.mImageTeeth);
        holder.mImageTeeth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.setClicks(position,1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageTeeth;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageTeeth = itemView.findViewById(R.id.imageTeeth);
        }
    }
}

