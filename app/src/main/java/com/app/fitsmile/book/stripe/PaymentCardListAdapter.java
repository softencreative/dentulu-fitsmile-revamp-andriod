package com.app.fitsmile.book.stripe;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.fitsmile.R;
import com.app.fitsmile.common.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.app.fitsmile.BuildConfig.STRIPE_SECRET_KEY;

public class PaymentCardListAdapter extends RecyclerView.Adapter<PaymentCardListAdapter.MyViewHolder> {

    Activity context;
    private ArrayList<PaymentCardListDate> arrayListitem = new ArrayList<>();

    private OnItemClickDeleted onItemClickDeleted;
    private OnItemClicked onClick;

    public PaymentCardListAdapter(Activity context, ArrayList<PaymentCardListDate> arrayListitem) {
        this.arrayListitem = arrayListitem;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stripe_savedcards, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
//        holder.name.setText(arrayListitem.get(position).getBrand()+" ending in "+arrayListitem.get(position).getLast4());
        holder.name.setText("**** **** **** " + arrayListitem.get(position).getLast4());
        holder.date.setText(arrayListitem.get(position).getExp_month() + "/" + arrayListitem.get(position).getExp_year());
        if (arrayListitem.get(position).getIs_selected().equals("1")) {
            holder.itemView.setBackgroundResource(R.color.colorPrimary);
        } else {
            holder.itemView.setBackgroundResource(R.color.white);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setIcon(R.mipmap.ic_launcher)
                        .setMessage(context.getString(R.string.are_you_sure_you_want_to_delete_this_card))
                        .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteCard(context, StripePayment.customer_id, arrayListitem.get(position).getId(), position);
                            }
                        }).setNegativeButton(context.getString(R.string.no), null).show();
                StripePayment stripePayment = new StripePayment();
                arrayListitem.get(position).getId();
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position);
                for (int i = 0; i < arrayListitem.size(); i++) {
                    arrayListitem.get(i).setIs_selected("0");
                }
                arrayListitem.get(position).setIs_selected("1");
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListitem.size();
    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }

    public void deleteCard(Activity context, String customer_id, final String card_id, final int positions) {
        Utils.openProgressDialog(context);
        StringRequest request = new StringRequest(Request.Method.DELETE, "https://api.stripe.com/v1/customers/" + customer_id + "/sources/" + card_id, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utils.closeProgressDialog();
                if (!response.equals(null)) {
                    Log.e("Your Array Response", response);
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(response);
                        arrayListitem.remove(positions);
                        notifyDataSetChanged();
                        StripePayment.newdelete = card_id;
                        StripePayment.arrayList.add(card_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e("Your Array Response", "Data Null");
                }
            }

        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error is ", "" + error);
                Utils.closeProgressDialog();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Bearer " + STRIPE_SECRET_KEY);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    public interface OnItemClicked {
        void onItemClick(int position);
    }

    public interface OnItemClickDeleted {
        void onItemClickDelete(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, date;
        public ImageView delete;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.cardno);
            date = view.findViewById(R.id.validation);
            delete = view.findViewById(R.id.delete);
        }
    }
}

