package com.app.fitsmile.photoConsultation;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.fitsmile.R;
import com.app.fitsmile.utils.LocaleManager;

import static com.app.fitsmile.common.Utils.isEmptyOrNull;

public class QuestionsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private StepData stepData;
    private TextView tvShortDescription;
    private TextView tvDescription;
    private RecyclerView rvQuestions;
    private QuestionsAdapter questionsAdapter;
    private View view = null;

    public QuestionsFragment() {
    }

    public QuestionsFragment(StepData stepData) {
        this.stepData = stepData;
    }

    public static QuestionsFragment newInstance(String param1, String param2) {
        QuestionsFragment fragment = new QuestionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_question, container, false);
            initView(view);
            if (stepData != null) {
                setData();
            }
        }
        LocaleManager.setLocale(getActivity());
        return view;
    }

    private void initView(View view) {
        tvShortDescription = view.findViewById(R.id.tv_short_description);
        tvDescription = view.findViewById(R.id.tv_description);
        rvQuestions = view.findViewById(R.id.rv_questions);
    }

    private void setData() {
        if (isEmptyOrNull(stepData.getDescription())) {
            tvDescription.setVisibility(View.GONE);
        } else {
            tvDescription.setVisibility(View.VISIBLE);
            tvDescription.setText(stepData.getDescription());
        }
        if (isEmptyOrNull(stepData.getShort_description())) {
            tvShortDescription.setVisibility(View.GONE);
        } else {
            tvShortDescription.setVisibility(View.VISIBLE);
            tvShortDescription.setText(stepData.getShort_description());
        }
        rvQuestions.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvQuestions.setLayoutManager(layoutManager);
        rvQuestions.setItemAnimator(new DefaultItemAnimator());
        rvQuestions.setItemViewCacheSize(stepData.getQuestions().size());
        /*rvQuestions.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));*/
        setAdapter();
    }

    private void setAdapter() {
        questionsAdapter = new QuestionsAdapter(getActivity(), stepData.getQuestions());
        rvQuestions.setAdapter(questionsAdapter);
//        if (questionsAdapter == null) {
//            questionsAdapter = new QuestionsAdapter(getActivity(), stepData.getQuestions());
//            rvQuestions.setAdapter(questionsAdapter);
//        } else {
//            questionsAdapter.reloadData(stepData.getQuestions());
//        }
    }

    public boolean checkValidations(){
        boolean isValidate=checkValidation(stepData);
        questionsAdapter.notifyDataSetChanged();

        return isValidate;
    }

    public boolean checkValidation(StepData stepData) {
        boolean isValid = true;
        for (Question question : stepData.getQuestions()) {
            question.setErrorVisible(!question.isSelected());
            if (isValid) {
                isValid = question.isSelected();
            }
        }
        return isValid;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case Constants.CAMERA_INTENT:
                    Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                    questionsAdapter.setImageView(selectedImage);
                    break;
                case Constants.GALLERY_INTENT:
                    Uri selectedImageUri = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    if (selectedImageUri != null) {
                        Cursor cursor = getActivity().getContentResolver().query(selectedImageUri,
                                filePathColumn, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String picturePath = cursor.getString(columnIndex);
                            questionsAdapter.setImageView(BitmapFactory.decodeFile(picturePath));
                            cursor.close();
                        }
                    }
                    break;
            }

        }
    }

}
