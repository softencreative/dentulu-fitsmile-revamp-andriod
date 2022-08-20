package com.app.fitsmile.appointment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.appointmentlist.DoctorAppointmentlistdataPojo;

import java.util.ArrayList;
import java.util.Locale;

public class VirtualVideoAppAdapter extends RecyclerView.Adapter<VirtualVideoAppAdapter.ViewHolder> implements Filterable {

    private Context context;
    private AppointmentItemClick listener;
    private ArrayList<DoctorAppointmentlistdataPojo> items;
    private ArrayList<DoctorAppointmentlistdataPojo> mFilteredList;
    private boolean isHomeScreen;

    public VirtualVideoAppAdapter(Context context, ArrayList<DoctorAppointmentlistdataPojo> list,AppointmentItemClick listener,
                                  boolean isHomeScreen) {
        this.context = context;
        items = list;
        mFilteredList=list;
        this.isHomeScreen = isHomeScreen;
        this.listener = listener;
    }

    public interface AppointmentItemClick{
        void onAppointmentItemClick(String what, DoctorAppointmentlistdataPojo item, int position);
        void onEmptyList(int position);


    }

    public void setCommonList(){

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video_appointment,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final DoctorAppointmentlistdataPojo item = mFilteredList.get(position);
        holder.tvAppointmentId.setText(item.getRef_id());

        if(isHomeScreen){
            holder.tvViewFullDetails.setVisibility(View.GONE);
        }else {
            holder.tvViewFullDetails.setVisibility(View.VISIBLE);
        }

        holder.tvDoctorName.setText(Utils.getStr(item.getSpecialist_name()));
        holder.tvStatus.setText(Utils.getStr(item.getAppointment_status()));
        holder.tvCallDuration.setText(Utils.getStr(item.getDuration()));
        if(!Utils.getStr(item.getDuration()).isEmpty() || Utils.getStr(item.getDuration()).equals("0")){
            holder.tvCallDuration.setVisibility(View.VISIBLE);
            holder.tvCallDuration.setText(Utils.getStr(getMinFromSec(Integer.parseInt(item.getDuration())) + " "+context.getString(R.string.mins_value)));
        }else{
            holder.tvCallDuration.setText(Utils.getStr("0 "+context.getString(R.string.mins_value)));
        }


        String strDate = Utils.dateFormat(items.get(position).getBooking_date(), Utils.inputOnlyDateSmall, Utils.dateDisplayFormatFull);
        holder.tvCreatedOn.setText(Utils.getStr(strDate)+ "\n" +Utils.getStr(items.get(position).getBooking_time()));

          holder.itemView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  listener.onAppointmentItemClick("whole", items.get(position), position);
              }
          });
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDoctorName, tvStatus, tvCallDuration, tvCreatedOn, tvViewFullDetails,tvAppointmentId;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorName = itemView.findViewById(R.id.tv_doc_name);
            tvStatus = itemView.findViewById(R.id.tv_status_of_consult);
            tvCallDuration = itemView.findViewById(R.id.tv_call_duration);
            tvCreatedOn = itemView.findViewById(R.id.tv_created_on);
            tvViewFullDetails = itemView.findViewById(R.id.tv_view_full_details);
            tvAppointmentId=itemView.findViewById(R.id.tv_name);
        }
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredList = items;
                } else {
                    ArrayList<DoctorAppointmentlistdataPojo> filteredList = new ArrayList<>();
                    for (DoctorAppointmentlistdataPojo row : items) {
                        if (row.getAppointment_status().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<DoctorAppointmentlistdataPojo>) filterResults.values;
                listener.onEmptyList(mFilteredList.size());
                notifyDataSetChanged();
            }
        };
    }

    String getMinFromSec(int s) {
        if (s == 0) {
           return "0";
        } else {
            int sec = s % 60;
            int min = (s / 60) % 60;
            int hours = (s / 60) / 60;
            return String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, min, sec);
        }
    }

}
