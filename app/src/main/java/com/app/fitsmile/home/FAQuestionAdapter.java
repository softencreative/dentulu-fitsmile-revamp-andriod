package com.app.fitsmile.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.response.myaccount.FAQResult;

import java.util.ArrayList;

public class FAQuestionAdapter extends  RecyclerView.Adapter<FAQuestionAdapter.ViewHolder> {
    private Context context;
    private ArrayList<FAQResult> rowitem;

    public FAQuestionAdapter(Context context, ArrayList<FAQResult> list){
        this.context=context;
        this.rowitem=list;
    }
    @Override
    public FAQuestionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_faq,parent,false);
        return new FAQuestionAdapter.ViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(final FAQuestionAdapter.ViewHolder holder, int position) {
        holder.text1.setText(rowitem.get(position).getQuestion());
        holder.text2.setText(rowitem.get(position).getAnswer());

        holder.down1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.up1.setVisibility(View.VISIBLE);
                holder.text2.setVisibility(View.VISIBLE);
                holder.down1.setVisibility(View.GONE);
            }
        });

        holder.up1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.up1.setVisibility(View.GONE);
                holder.text2.setVisibility(View.GONE);
                holder.down1.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rowitem.size();
    }

    public class ViewHolder  extends  RecyclerView.ViewHolder{
        public ImageView up1,down1;;
        public TextView text1,text2;
        public ViewHolder(View itemView) {
            super(itemView);
            text1 =itemView.findViewById(R.id.text1);
            text2 =itemView.findViewById(R.id.text2);
            up1 = itemView.findViewById(R.id.uparrow1);
            down1 =itemView.findViewById(R.id.downarrow1);
        }
    }
}

