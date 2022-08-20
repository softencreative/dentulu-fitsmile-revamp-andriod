package com.app.fitsmile.intra;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.app.fitsmile.MainActivity;
import com.app.fitsmile.R;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.intra.utils.StreamSelf;
import com.app.fitsmile.utils.LocaleManager;


public class IntraOralPicturesActivity extends BaseActivity {


    TextView img_camera, img_gallery, btn_connect;
    ImageView help;

    Activity activity;
    AlertDialog.Builder dialogBuilder;
    AlertDialog alert;
    private android.hardware.Camera mCameraDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intra_oral_picture);
        LocaleManager.setLocale(this);
        activity = this;
        initUI();
    }

    private void initUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if(getIntent()!=null && getIntent().getExtras()!=null && getIntent().getExtras().getString("MOUTH_CAM")!=null){
            getSupportActionBar().setTitle(R.string.mouth_cam);
        }else{
            getSupportActionBar().setTitle(R.string.intraoral_picture);
        }
        toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));


        help = findViewById(R.id.iv_help);
        btn_connect = findViewById(R.id.btn_connect);
        img_camera = findViewById(R.id.img_camera);
        img_gallery = findViewById(R.id.img_gallery);

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder = new AlertDialog.Builder(activity);
                LayoutInflater inflater = activity.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.popup_help_wifi, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(false);
                alert = dialogBuilder.create();
                alert.show();

                ImageView iv_close = (ImageView) dialogView.findViewById(R.id.iv_close);
                iv_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.cancel();
                    }
                });

            }
        });

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS), 0);
            }
        });

        img_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StreamSelf.isNeedTakePhoto) {
                    startActivity(new Intent(activity, MainActivity.class));
                }else{
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.popup_common, null);
                    dialogBuilder.setView(dialogView);
                    dialogBuilder.setCancelable(false);
                    AlertDialog alert = dialogBuilder.create();
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alert.show();

                    TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
                    TextView tvContent = dialogView.findViewById(R.id.tvContent);
                    TextView tvNo = dialogView.findViewById(R.id.tvNo);
                    TextView tvYes = dialogView.findViewById(R.id.tvYes);

                    tvTitle.setText(activity.getResources().getString(R.string.app_name));
                    tvContent.setText(R.string.connect_text);
                    tvNo.setVisibility(View.GONE);
                    tvYes.setText(R.string.got_it);

                    tvYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.cancel();
                        }
                    });
                }
            }
        });

        img_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, PlaybackActivity.class));
            }
        });
    }


	@Override
    public boolean
    onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

