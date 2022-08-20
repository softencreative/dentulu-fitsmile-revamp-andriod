package com.app.fitsmile.noti;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.utils.ColorGenerator;
import com.app.fitsmile.utils.TextDrawable;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private ArrayList<NotificationResponse> arrayListitem = new ArrayList<>();
    private Context context;
    private NotificationClickListener listener;
    private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
    public TextDrawable mDrawableBuilder;

    public NotificationAdapter(Context context,ArrayList<NotificationResponse> arrayListitem,NotificationClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.arrayListitem = arrayListitem;
    }

    public interface NotificationClickListener{
        void onNotificationItemClick(NotificationResponse item,int pos);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item  , parent, false);
        return new NotificationAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(arrayListitem.get(position).getRef_id().equalsIgnoreCase("")) {
            holder.referralId.setVisibility(View.GONE);
        } else {
            holder.referralId.setVisibility(View.VISIBLE);
            holder.referralId.setText(arrayListitem.get(position).getRef_id());
        }

        if(arrayListitem.get(position).getSub_name().equalsIgnoreCase("")) {
            holder.title.setVisibility(View.GONE);
        } else {
            holder.title.setVisibility(View.VISIBLE);
            holder.title.setText(arrayListitem.get(position).getSub_name());
        }

        if(arrayListitem.get(position).getMessage().equalsIgnoreCase("")) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(arrayListitem.get(position).getMessage());
        }

        if(arrayListitem.get(position).getCreated_date().equalsIgnoreCase("")) {
            holder.time.setVisibility(View.GONE);
        } else {
            holder.time.setVisibility(View.VISIBLE);
            holder.time.setText(Utils.getNewDate(context, arrayListitem.get(position).getCreated_date(),Utils.inputDate,Utils.outputDate));
        }

        if (arrayListitem.get(position).getIs_read().equals("1")){
            holder.referralId.setTextColor(context.getResources().getColor(R.color.txt_color_body));
            holder.title.setTextColor(context.getResources().getColor(R.color.txt_color_body));
            holder.description.setTextColor(context.getResources().getColor(R.color.txt_color_body));
            holder.time.setTextColor(context.getResources().getColor(R.color.txt_color_body));
        }else {
            holder.referralId.setTextColor(context.getResources().getColor(R.color.black_light));
            holder.title.setTextColor(context.getResources().getColor(R.color.black_light));
            holder.description.setTextColor(context.getResources().getColor(R.color.black_light));
            holder.time.setTextColor(context.getResources().getColor(R.color.black_light));
        }
        String letter = "A";
        String title = arrayListitem.get(position).getMessage();
        if (title != null && !title.isEmpty()) {
            letter = title.substring(0, 1);
        }
        int color = mColorGenerator.getRandomColor();
        mDrawableBuilder = TextDrawable.builder().buildRound(letter, color);
        holder.mThumbnailImage.setImageDrawable(mDrawableBuilder);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNotificationItemClick(arrayListitem.get(position),position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListitem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView referralId,title, description,time;
        TextView mTextCenter;
        public ImageView mThumbnailImage;
        public ViewHolder(@NonNull View view) {
            super(view);
            referralId = (TextView) view.findViewById(R.id.referenceId);
            title = view.findViewById(R.id.title);
            description = (TextView) view.findViewById(R.id.description);
            time = (TextView)view.findViewById(R.id.time);
            mThumbnailImage=view.findViewById(R.id.thumbnail_image);
        }
    }
}
