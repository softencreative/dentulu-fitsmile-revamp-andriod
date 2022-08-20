package com.app.fitsmile.shop.ui;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.app.fitsmile.R;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.utils.LocaleManager;
import com.squareup.picasso.Picasso;

public class ZoomImageActivity extends BaseActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);
        LocaleManager.setLocale(this);
        imageView = findViewById(R.id.imageZoom);

        if (getIntent().getStringExtra("internal").equals("1")) {
            imageView.setImageURI(Uri.parse(getIntent().getStringExtra("ImageName")));
        } else if (getIntent().getStringExtra("internal").equals("2")) {
            Picasso.get().load(getIntent().getStringExtra("ImageName")).placeholder(R.drawable.friends_sends_pictures_no).into(imageView);
        }
    }

}
