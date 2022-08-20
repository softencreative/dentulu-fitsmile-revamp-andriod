package com.app.fitsmile.photoConsultation;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.app.fitsmile.R;
import com.app.fitsmile.common.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.app.fitsmile.common.Utils.isEmptyOrNull;
import static com.app.fitsmile.photoConsultation.Constants.CAMERA_INTENT;
import static com.app.fitsmile.photoConsultation.Constants.GALLERY_INTENT;
import static com.app.fitsmile.photoConsultation.Constants.TYPE_CHECK_BOX;
import static com.app.fitsmile.photoConsultation.Constants.TYPE_IMAGE;
import static com.app.fitsmile.photoConsultation.Constants.TYPE_INT_CHECK_BOX;
import static com.app.fitsmile.photoConsultation.Constants.TYPE_INT_IMAGE;
import static com.app.fitsmile.photoConsultation.Constants.TYPE_INT_RADIO;
import static com.app.fitsmile.photoConsultation.Constants.TYPE_INT_TEXT_AREA;
import static com.app.fitsmile.photoConsultation.Constants.TYPE_INT_TEXT_INPUT;
import static com.app.fitsmile.photoConsultation.Constants.TYPE_RADIO;
import static com.app.fitsmile.photoConsultation.Constants.TYPE_TEXT_AREA;
import static com.app.fitsmile.photoConsultation.Constants.TYPE_TEXT_INPUT;


public class QuestionsAdapter extends RecyclerView.Adapter {
    private Activity activity;
    private List<Question> questionList;
    private int imagePosition;


    public QuestionsAdapter(Activity context, List<Question> questionList) {
        this.activity = context;
        this.questionList = questionList;
    }
//
    @Override
    public int getItemViewType(int position) {
        switch (questionList.get(position).getType()) {
            case TYPE_TEXT_AREA:
                return TYPE_INT_TEXT_AREA;
            case TYPE_CHECK_BOX:
                return TYPE_INT_CHECK_BOX;
            case TYPE_TEXT_INPUT:
                return TYPE_INT_TEXT_INPUT;
            case TYPE_IMAGE:
                return TYPE_INT_IMAGE;
            case TYPE_RADIO:
                return TYPE_INT_RADIO;
            default:
                return -1;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

//    @Override
//    public int getItemViewType(int position) {
//        return position;
//    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_INT_TEXT_AREA:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.textarea_adapter_layout, parent, false);
                return new TextAreaViewHolder(view);
            case TYPE_INT_CHECK_BOX:
            case TYPE_INT_RADIO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkbox_container_adapter_layout, parent, false);
                return new SelectContainerViewHolder(view);
            case TYPE_INT_TEXT_INPUT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.textinput_adapter_layout, parent, false);
                return new TextInputViewHolder(view);
            case TYPE_INT_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_adapter_layout, parent, false);
                return new ImageViewViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Question question = questionList.get(position);

        if (question != null) {
            switch (question.getType()) {
                case TYPE_TEXT_AREA:
                    TextAreaViewHolder textAreaViewHolder = ((TextAreaViewHolder) holder);

                    if (!isEmptyOrNull(question.getTitle()))
                        textAreaViewHolder.tvQuestionTitle.setText(question.getTitle());
                    else
                        textAreaViewHolder.tvQuestionTitle.setText("");

                    if (question.getInputData() != null) {

                        textAreaViewHolder.etTextArea.setText(question.getInputData());
                    }
                    if (question.isErrorVisible()) {
                        textAreaViewHolder.tvError.setVisibility(View.VISIBLE);
                    } else {
                        textAreaViewHolder.tvError.setVisibility(View.GONE);
                    }

                    textAreaViewHolder.etTextArea.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            question.setInputData(s.toString());
                            if (s.length() > 0) {
                                question.setSelected(true);
                            }
                        }
                    });
                    break;
                case TYPE_CHECK_BOX:
                    setSelectableViewHolder(holder, questionList.get(position), true);
                    break;
                case TYPE_TEXT_INPUT:
                    TextInputViewHolder textInputViewHolder = ((TextInputViewHolder) holder);
                    if (!isEmptyOrNull(question.getTitle()))
                        textInputViewHolder.tvQuestionTitle.setText(question.getTitle());
                    else
                        textInputViewHolder.tvQuestionTitle.setText("");

                    if (question.getInputData() != null) {

                        textInputViewHolder.etInput.setText(question.getInputData());
                    }
                    if (question.isErrorVisible()) {
                        textInputViewHolder.tvError.setVisibility(View.VISIBLE);
                    } else {
                        textInputViewHolder.tvError.setVisibility(View.GONE);
                    }

                    textInputViewHolder.etInput.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            question.setInputData(s.toString());
                            if (s.length() > 0) {
                                question.setSelected(true);
                            }
                        }
                    });
                    break;
                case TYPE_IMAGE:
                    ImageViewViewHolder imageViewViewHolder = ((ImageViewViewHolder) holder);

                    imageViewViewHolder.ivCapturedImage.setImageBitmap(question.getBitmap());
                    if (question.getValue() != null
                            && !question.getValue().isEmpty()
                            && !isEmptyOrNull(question.getValue().get(0).getValue())) {
                        imageViewViewHolder.cardInfoImage.setVisibility(View.VISIBLE);
                        imageViewViewHolder.dummyView.setVisibility(View.GONE);
                        // imageViewViewHolder.ivSampleResource.setImageResource(R.drawable.image2);
                        Picasso.get().load(question.getValue().get(0).getValue()).into(imageViewViewHolder.ivSampleResource);
                    } else {
                        imageViewViewHolder.cardInfoImage.setVisibility(View.GONE);
                        imageViewViewHolder.dummyView.setVisibility(View.VISIBLE);
                    }
                    if (!isEmptyOrNull(question.getTitle()))
                        imageViewViewHolder.tvImageTitleInfo.setText(question.getTitle());
                    else
                        imageViewViewHolder.tvImageTitleInfo.setText("");

                    if (question.isErrorVisible()) {
                        imageViewViewHolder.tvError.setVisibility(View.VISIBLE);
                    } else {
                        imageViewViewHolder.tvError.setVisibility(View.GONE);
                    }
                    imageViewViewHolder.cardCaptureImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imagePosition = position;
                            selectImage(activity);
                        }
                    });
                    break;
                case TYPE_RADIO:
                    setSelectableViewHolder(holder, question, false);
                    break;
                default:
            }//end of switch
        }//end of If
    }


    private void selectImage(Activity activity) {

        final CharSequence[] options = {activity.getResources().getString(R.string.take_photo_), activity.getResources().getString(R.string.lbl_choose_from_gallery)};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.choose_your_profile_picture));

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (Utils.checkSelfPermission(activity, Manifest.permission.CAMERA, 10)) {
                    if (options[item].equals(activity.getResources().getString(R.string.take_photo_))) {
                        dialog.dismiss();
                        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        activity.startActivityForResult(takePicture, CAMERA_INTENT);
                    } else if (options[item].equals(activity.getResources().getString(R.string.lbl_choose_from_gallery))) {
                        dialog.dismiss();
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        activity.startActivityForResult(pickPhoto, GALLERY_INTENT);
                    }
                }
            }
        });
        builder.show();
    }

    private void setSelectableViewHolder(@NonNull RecyclerView.ViewHolder holder, Question question, boolean isMultiSelect) {
        SelectContainerViewHolder selectContainerViewHolder = ((SelectContainerViewHolder) holder);
        if (!isEmptyOrNull(question.getTitle()))
            selectContainerViewHolder.tvQuestionTitle.setText(question.getTitle());
        else
            selectContainerViewHolder.tvQuestionTitle.setText("");

        if (question.isErrorVisible()) {
            selectContainerViewHolder.tvError.setVisibility(View.VISIBLE);
        } else {
            selectContainerViewHolder.tvError.setVisibility(View.GONE);
        }
        selectContainerViewHolder.rvOptionsList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        selectContainerViewHolder.rvOptionsList.setLayoutManager(layoutManager);
        selectContainerViewHolder.rvOptionsList.setItemAnimator(new DefaultItemAnimator());
        if (question.getValue() != null && !question.getValue().isEmpty()) {
            selectContainerViewHolder.rvOptionsList.setVisibility(View.VISIBLE);
            SelectableViewAdapter selectableViewAdapter = new SelectableViewAdapter(activity, question, isMultiSelect);
            selectContainerViewHolder.rvOptionsList.setAdapter(selectableViewAdapter);
        } else {
            selectContainerViewHolder.rvOptionsList.setVisibility(View.GONE);
        }
    }

    private String getString(int id) {
        return activity.getString(id);
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public void reloadData(List<Question> questions) {
        this.questionList = questions;
        notifyDataSetChanged();
    }

    public void setImageView(Bitmap bitmap) {
        questionList.get(imagePosition).setSelected(true);
        questionList.get(imagePosition).setBitmap(bitmap);
//        notifyDataSetChanged();
        notifyItemChanged(imagePosition);
        Utils.openProgressDialog(activity);
        new ImageConverterAsync(questionList.get(imagePosition)).execute(bitmap);
    }
}
