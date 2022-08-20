package com.app.fitsmile.book;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.BuildConfig;
import com.app.fitsmile.R;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.dentistlist.DoctorListDatas;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchDentistAdapter extends RecyclerView.Adapter<SearchDentistAdapter.ViewHolder> implements Filterable {

    private Context context;
    private ItemClickListener listener;
    private ArrayList<DoctorListDatas> doctorListFilter;
    private ArrayList<DoctorListDatas> doctorList;
    //private ArrayFilter mFilter;
    private ArrayList<DoctorListDatas> mOriginalValues;
    private List<DoctorListDatas> dataListFull;

    public SearchDentistAdapter(Context context, ItemClickListener listener, ArrayList<DoctorListDatas> doctorList) {
        this.context = context;
        this.listener = listener;
       // this.doctorList = doctorList;
        this.doctorListFilter = doctorList;
        //mOriginalValues = new ArrayList<DoctorListDatas>(doctorList);
       dataListFull = new ArrayList<>(doctorListFilter);
    }


    public interface ItemClickListener {
        void onItemViewProfileClick(DoctorListDatas data);

        void onItemScheduleNowClick(DoctorListDatas data);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dentist_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String strDrName = context.getResources().getString(R.string.dr)+" " + doctorListFilter.get(position).getFirstname() + " " + doctorListFilter.get(position).getLastname();
        doctorListFilter.get(position).setFullname(strDrName);
        holder.tvDentistName.setText(Utils.getStr(strDrName));
        if (!doctorListFilter.get(position).getImage_url().equals("")) {
            Picasso.get()
                    .load(doctorListFilter.get(position).getImage_url())
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(holder.mImvDentist);
        }
        holder.tvViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemViewProfileClick(doctorListFilter.get(position));
            }
        });

        holder.tvDentistName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemViewProfileClick(doctorListFilter.get(position));
            }
        });
        holder.mImvDentist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemViewProfileClick(doctorListFilter.get(position));
            }
        });
        holder.tvScheduleNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemScheduleNowClick(doctorListFilter.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorListFilter.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvViewProfile, tvScheduleNow, tvDentistName;
        CircleImageView mImvDentist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvViewProfile = itemView.findViewById(R.id.tv_view_profile);
            tvScheduleNow = itemView.findViewById(R.id.tv_schedule_now);
            mImvDentist = itemView.findViewById(R.id.image);
            tvDentistName = itemView.findViewById(R.id.tv_dentist_name);
        }
    }
    public void setUpdatedList(ArrayList<DoctorListDatas> doctorList)
    {
        this.doctorListFilter = doctorList;
        dataListFull = new ArrayList<>(doctorListFilter);
        notifyDataSetChanged();

    }

    //implement fileration code
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    private Filter listFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<DoctorListDatas> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(dataListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (DoctorListDatas item : dataListFull) {
                    if (item.getFullname().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            doctorListFilter.clear();
            doctorListFilter.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


}
