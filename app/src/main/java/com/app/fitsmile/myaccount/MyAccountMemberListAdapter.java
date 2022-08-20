package com.app.fitsmile.myaccount;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.patientlist.PatientListData;

import java.util.ArrayList;
import java.util.List;

public class MyAccountMemberListAdapter extends RecyclerView.Adapter<MyAccountMemberListAdapter.ViewHolder> {

    private Context context;
    private ItemClickMemberList listener;
    private ArrayList<PatientListData> items;


    public MyAccountMemberListAdapter(Context context, ItemClickMemberList listener) {
        this.context = context;
        this.listener = listener;
        items = new ArrayList<>();
    }

    public void setCommonList(List<PatientListData> patientListData) {
        if (patientListData == null)
            return;
        items.clear();
        items.addAll(patientListData);
        notifyDataSetChanged();
    }

    public interface ItemClickMemberList {
        void onItemClickAddMember();

        void onItemClickViewMember(PatientListData data);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PatientListData listData = items.get(position);
        String letter = "A";
        if (position == 0) {
            holder.txtCenter.setText("+");
            holder.name.setText(R.string.txt_new);
       }
//        else if (position == 1) {
//            String title = items.get(position).getFirstName();
//            if (title != null && !title.isEmpty()) {
//                letter = title.substring(0, 1);
//            }
//            holder.txtCenter.setText(Utils.getStr(letter));
//            holder.name.setText(Utils.getStr(listData.getFirstName()));
////           holder.name.setText(Utils.getStr(listData.getRelationshipType()));
//       }
       else {
            String title = items.get(position).getFirstName();
            if (title != null && !title.isEmpty()) {
                letter = title.substring(0, 1);
            }
            holder.txtCenter.setText(Utils.getStr(letter));
            holder.name.setText(Utils.getStr(listData.getFirstName()));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    listener.onItemClickAddMember();
                } else {
                    listener.onItemClickViewMember(listData);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView circle;
        public TextView name, txtCenter;
        public LinearLayout llParent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circle = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            txtCenter = itemView.findViewById(R.id.tv_center);
            llParent = itemView.findViewById(R.id.ll_parent);
        }
    }
}
