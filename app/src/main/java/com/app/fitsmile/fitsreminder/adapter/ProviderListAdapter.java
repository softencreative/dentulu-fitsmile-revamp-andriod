package com.app.fitsmile.fitsreminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.BuildConfig;
import com.app.fitsmile.R;
import com.app.fitsmile.response.dentistlist.DoctorListDatas;
import com.app.fitsmile.shop.inter.RecyclerItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProviderListAdapter extends RecyclerView.Adapter<ProviderListAdapter.ViewHolder> {

    private Context context;
    private List<DoctorListDatas> mList;
    RecyclerItemClickListener mClickListener;
    int selectPosition=-1;
    public ProviderListAdapter(Context context, List<DoctorListDatas> list, RecyclerItemClickListener itemClickListener) {
        this.context = context;
        mList=list;
        mClickListener=itemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_provider_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mTextPatientName.setText(mList.get(position).getFirstname()+" "+mList.get(position).getLastname());
        holder.mTextPatientType.setText(mList.get(position).getRef_id());
        if (!mList.get(position).getImage().equals(""))
        Picasso.get().load(BuildConfig.BASE_IMAGEURL+mList.get(position).getImage()).placeholder(R.drawable.ic_profile_placeholder).into(holder.mImageUser);
        if (selectPosition==position){
            holder.mImageCheck.setVisibility(View.VISIBLE);
        }else {
            holder.mImageCheck.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.setClicks(position,2);
                selectPosition=position;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextPatientName;
        TextView mTextPatientType;
        CircleImageView mImageUser;
        ImageView mImageCheck;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
             mTextPatientName=itemView.findViewById(R.id.name);
             mTextPatientType=itemView.findViewById(R.id.type);
             mImageUser=itemView.findViewById(R.id.imageUser);
             mImageCheck=itemView.findViewById(R.id.imageSelect);
        }
    }
}

