package com.app.fitsmile.book;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.response.dentisdetails.AvailableSlotsItem;

import java.util.ArrayList;
import java.util.List;

public class DocAvailableTimeAdapter extends RecyclerView.Adapter<DocAvailableTimeAdapter.ViewHolder> {


    private Activity context;
    private List<AvailableSlotsItem> items;
    private String strStatus="";
    private String strTimeZone="";

    public DocAvailableTimeAdapter(Activity context) {
        this.context = context;
        items = new ArrayList<>();
    }

    public void setCommonList(List<AvailableSlotsItem> item, String strStatus, String strTimeZone){
        if(item == null)
            return;
        this.strStatus = strStatus;
        this.strTimeZone = strTimeZone;
        items.clear();
        items.addAll(item);
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_doc_available_time,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (items.get(position).getDayname()!=null){
            if (items.get(position).getDayname().equalsIgnoreCase("Sunday")){
                holder.tvDay.setText(context.getResources().getString(R.string.sunday));
            }
            else if (items.get(position).getDayname().equalsIgnoreCase("Monday")){
                holder.tvDay.setText(context.getResources().getString(R.string.monday));
            }
            else if (items.get(position).getDayname().equalsIgnoreCase("Tuesday")){
                holder.tvDay.setText(context.getResources().getString(R.string.tuesday));
            }
            else if (items.get(position).getDayname().equalsIgnoreCase("Wednesday")){
                holder.tvDay.setText(context.getResources().getString(R.string.wednesday));
            }
            else if (items.get(position).getDayname().equalsIgnoreCase("Thursday")){
                holder.tvDay.setText(context.getResources().getString(R.string.thursday));
            } else if (items.get(position).getDayname().equalsIgnoreCase("Friday")){
                holder.tvDay.setText(context.getResources().getString(R.string.friday));
            }
            else if (items.get(position).getDayname().equalsIgnoreCase("Saturday")){
                holder.tvDay.setText(context.getResources().getString(R.string.saturday));
            }

        }
        //holder.tvDay.setText(items.get(position).getDayname());
        String strTime = items.get(position).getStarttime() + " - " + items.get(position).getEndtime();
        holder.tvTime.setText(strTime);
        /*if(strStatus.equalsIgnoreCase("1")) {
            holder.tvConvertedTime.setVisibility(View.GONE);
        } else {
            holder.tvConvertedTime.setVisibility(View.VISIBLE);
            String strFromDateConvert = items.get(position).getStarttime()  +" "+items.get(position).getDayname();
            String strToDateConvert   = items.get(position).getEndtime() +" "+items.get(position).getDayname();
            String fromFormat = "hh:mm a EEEE";
            String toFormat = "hh:mm a EEE";
            String strDateConvert1 = CommonClass.getNewDate(context, strFromDateConvert, fromFormat, toFormat);
            String strDateConvert2 = CommonClass.getNewDate(context, strToDateConvert, fromFormat, toFormat);
            holder.tvConvertedTime.setText(strDateConvert1+" - "+strDateConvert2);
        }*/
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDay;
        TextView tvTime;
        TextView tvConvertedTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tv_days);
            tvTime = itemView.findViewById(R.id.tv_timing);
            tvConvertedTime = itemView.findViewById(R.id.tv_converted_time);
        }
    }
}
