package com.app.fitsmile.appointment;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.photoConsultancy.PhotoConsultancyDetail.Doctor_options;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class TreatmentPlanShowAdapter extends RecyclerView.Adapter<TreatmentPlanShowAdapter.ViewHolder> {
    private Activity context;
    private ArrayList<Doctor_options> doctorOptionsArrayList;

    public TreatmentPlanShowAdapter(Activity context, ArrayList<Doctor_options> doctorOptionsArrayList) {
        this.context = context;
        this.doctorOptionsArrayList = doctorOptionsArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_treatments, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctor_options doctorOptions = doctorOptionsArrayList.get(position);
        holder.tvPrice.setText("$" + doctorOptions.getEstimated_cost());
        holder.tvNoOfAppointment.setText(doctorOptions.getNo_of_appointment());
        holder.tvClinicalEvaluation.setText(doctorOptions.getDescription());
        holder.tvTreatmentType.setText(doctorOptions.getCase_type());
        holder.tvSpecialOffer.setText(doctorOptions.getSpecial_offer());
        holder.tvEstimationInsurance.setText(doctorOptions.getInsurance_coverage());

        if (Utils.isEmptyOrNull(doctorOptions.getBefore_after_image()) && Utils.isEmptyOrNull(doctorOptions.getAfter_image())) {
            holder.tvAboutBeforeAfter.setVisibility(View.GONE);
            holder.tvLblBeforeAfterImage.setVisibility(View.GONE);
            holder.ll_before_after_image_layout.setVisibility(View.GONE);
        }else{
            holder.tvAboutBeforeAfter.setVisibility(View.VISIBLE);
            holder.tvLblBeforeAfterImage.setVisibility(View.VISIBLE);
            holder.ll_before_after_image_layout.setVisibility(View.VISIBLE);
        }

        holder.tvAboutBeforeAfter.setText(doctorOptions.getBefore_after_name());
        if (Utils.isEmptyOrNull(doctorOptions.getBefore_after_image())) {
            holder.cv_before_image.setVisibility(View.GONE);
        } else {
            holder.cv_before_image.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(doctorOptions.getBefore_after_image())
                    .placeholder(R.drawable.friends_sends_pictures_no)
                    .into(holder.iv_before_image);
        }
        if (Utils.isEmptyOrNull(doctorOptions.getAfter_image())) {
            holder.cv_after_image.setVisibility(View.GONE);
        } else {
            holder.cv_after_image.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(doctorOptions.getAfter_image())
                    .placeholder(R.drawable.friends_sends_pictures_no)
                    .into(holder.iv_after_image);
        }
    }


    @Override
    public int getItemCount() {
        return doctorOptionsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView  iv_before_image, iv_after_image;
        LinearLayout llTreatmentOptionBrief,ll_before_after_image_layout;
        TextView tvLblEstimationCost, tvLblNoOfAppointment, tvLblClinicalEvaluation, tvLblTreatmentPlan, tvLblBeforeAfterImage,
                tvLblSpecialOffer, tvLblEstimatedInsurance, tvLblRecordedVideo;

        TextView tvSpecialOffer, tvEstimationInsurance, tvAboutBeforeAfter, tvTreatmentType, tvClinicalEvaluation, tvNoOfAppointment, tvPrice;
        CardView cv_before_image, cv_after_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llTreatmentOptionBrief = itemView.findViewById(R.id.ll_treatment_brief);
            tvLblEstimationCost = itemView.findViewById(R.id.tv_label_estimated_cost);
            tvLblNoOfAppointment = itemView.findViewById(R.id.tv_label_no_of_appointment);
            tvLblClinicalEvaluation = itemView.findViewById(R.id.tv_label_clinical_evaluation);
            tvLblTreatmentPlan = itemView.findViewById(R.id.tv_label_treatment_type);
            tvLblBeforeAfterImage = itemView.findViewById(R.id.tv_label_before_after_image);
            tvLblSpecialOffer = itemView.findViewById(R.id.tv_label_special_offer);
            tvSpecialOffer = itemView.findViewById(R.id.tv_special_offer);
            tvLblEstimatedInsurance = itemView.findViewById(R.id.tv_label_estimated_insurance);
            tvEstimationInsurance = itemView.findViewById(R.id.tv_estimated_insurance);
            tvAboutBeforeAfter = itemView.findViewById(R.id.tv_about_before_after_image);
            tvTreatmentType = itemView.findViewById(R.id.tv_treatment_type);
            tvClinicalEvaluation = itemView.findViewById(R.id.tv_clinical_evaluation);
            tvNoOfAppointment = itemView.findViewById(R.id.tv_no_of_appointment);
            tvPrice = itemView.findViewById(R.id.tv_price);

            iv_before_image = itemView.findViewById(R.id.iv_before_image);
            iv_after_image = itemView.findViewById(R.id.iv_after_image);
            cv_before_image = itemView.findViewById(R.id.cv_before_image);
            cv_after_image = itemView.findViewById(R.id.cv_after_image);
            ll_before_after_image_layout = itemView.findViewById(R.id.ll_before_after_image_layout);
        }
    }
}
