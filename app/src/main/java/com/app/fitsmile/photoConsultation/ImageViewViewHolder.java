package com.app.fitsmile.photoConsultation;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;

public class ImageViewViewHolder extends RecyclerView.ViewHolder {
    public   TextView tvError;
    public TextView tvImageTitleInfo;
    public TextView tvInstruction;
    public ImageView ivSampleResource;
    public ImageView ivCapturedImage;
    public CardView cardCaptureImage;
    public CardView cardInfoImage;
    public View dummyView;

    public ImageViewViewHolder(View itemView) {
        super(itemView);
        ivSampleResource = itemView.findViewById(R.id.iv_sample_resource);
        ivCapturedImage = itemView.findViewById(R.id.iv_captured_image);
        tvImageTitleInfo = itemView.findViewById(R.id.tv_image_title_info);
        tvInstruction = itemView.findViewById(R.id.tv_instruction);
        cardCaptureImage = itemView.findViewById(R.id.card_capture_image);
        cardInfoImage = itemView.findViewById(R.id.card_info_image);
        dummyView = itemView.findViewById(R.id.view_dummy);
        tvError = itemView.findViewById(R.id.tv_error);

    }
}//end of holder

