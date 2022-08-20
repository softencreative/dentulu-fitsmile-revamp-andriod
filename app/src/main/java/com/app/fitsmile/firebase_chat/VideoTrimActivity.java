package com.app.fitsmile.firebase_chat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.fitsmile.R;
import com.app.fitsmile.utils.LocaleManager;

import java.io.File;
import java.util.ArrayList;

import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;

import static com.app.fitsmile.firebase_chat.ChatConstant.LOCAL_DIRECTORY;
import static com.app.fitsmile.firebase_chat.FirebaseConstants.CROPPED_VIDEOS;
import static com.app.fitsmile.firebase_chat.FirebaseConstants.VIDEOS_TO_TRIM;

public class VideoTrimActivity extends AppCompatActivity {
    K4LVideoTrimmer videoTrimmer;
    ArrayList<String> originalVideos;
    ArrayList<String> croppedVideos = new ArrayList<>();
    int currentVideoIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_trim);
        LocaleManager.setLocale(this);
        videoTrimmer = findViewById(R.id.videoTrimmer);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            originalVideos = bundle.getStringArrayList(VIDEOS_TO_TRIM);
        }

        if (originalVideos != null && originalVideos.size() > 0) {
            loadVideoToCrop();
        }
    }

    private void loadVideoToCrop() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (currentVideoIndex < originalVideos.size()) {
                    videoTrimmer.setVideoURI(Uri.parse(originalVideos.get(currentVideoIndex)));
                    videoTrimmer.setMaxDuration(20);
                    videoTrimmer.setDestinationPath(Environment.getExternalStorageDirectory() + File.separator + LOCAL_DIRECTORY + File.separator);
                    videoTrimmer.setOnTrimVideoListener(new OnTrimVideoListener() {
                        @Override
                        public void getResult(Uri uri) {
                            croppedVideos.add(uri.getPath());
                            if (currentVideoIndex < originalVideos.size() - 1) {
                                currentVideoIndex++;
                                loadVideoToCrop();
                            } else {
                                Intent intent = new Intent();
                                intent.putStringArrayListExtra(CROPPED_VIDEOS, croppedVideos);
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            }
                        }

                        @Override
                        public void cancelAction() {
                            finish();
                        }
                    });
                }
            }
        });
    }
}
