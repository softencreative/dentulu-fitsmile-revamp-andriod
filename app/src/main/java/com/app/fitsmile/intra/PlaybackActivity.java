package com.app.fitsmile.intra;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.app.fitsmile.R;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.intra.utils.PathConfig;
import com.app.fitsmile.utils.LocaleManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PlaybackActivity extends BaseActivity {

    private static final String TAG = "SecurityActivity";
    private static final int LOCK_REQUEST_CODE = 221;
    private static final int SECURITY_SETTING_REQUEST_CODE = 233;

    private static final int SCAN_OK = 0;
    private int screenWidth;

    public static enum TYPE {
        PHOTO, VIDEO
    };

    private ViewPager mPager;
    private List<View> listViews;
    private View viewPhotos,viewVideo;
    private TextView t1, t2;
    private int offset = 0;
    private int currIndex = 0;
    private ProgressDialog mProgressDialog;

    private ImageView iv_Actionbar_Back;
    private ImageView iv_Actionbar_More;
    private TextView txt_Playback_Actionbar;
    private PopupWindow pWindowMenu;
    private SimpleAdapter mAdapter;
    private RelativeLayout layout_Actionbar;
    private int layout_Actionbar_Height;

    private GridView gd_Photos;
    private PhotosAdapter mPhotosAdapter;

    private GridView gd_Videos;
    private VideosAdapter mVideosAdapter;

    private int currSdcardItem = 0;
    public static List<String> photoList = new ArrayList<String>();
    public static final String ACTION_DELETE_PHOTO = "com.action.send.ACTION_DELETE_PHOTO";
    private List<String> videoList = new ArrayList<String>();

    public TextView tvPhotoSelect, tvVideoSelect;
    ImageView ivPhotoDelete, ivPhotoShare;
    ImageView ivVideoDelete, ivVideoShare;
    Activity activity;
    boolean flagPhotos=false;
    boolean flagVideos=false;
    float calculatedWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);
        LocaleManager.setLocale(this);
        activity=this;
        authenticateApp();

        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        register();
        initImageView();
        initTextView();
        InitViewPager();
    }

    private void initTextView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(R.string.gallery);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));

        t1 = (TextView) findViewById(R.id.text1);
        t2 = (TextView) findViewById(R.id.text2);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        calculatedWidth = metrics.widthPixels * 0.5f;

        t1.setOnClickListener(new MyOnClickListener(0));
        t2.setOnClickListener(new MyOnClickListener(1));
    }

    private void InitViewPager() {
        mPager = (ViewPager) findViewById(R.id.vPager);
        listViews = new ArrayList<View>();
        LayoutInflater mInflater = getLayoutInflater();
        listViews.add(mInflater.inflate(R.layout.layout_photos, null));
        listViews.add(mInflater.inflate(R.layout.layout_videos, null));
        mPager.setAdapter(new MyPagerAdapter(listViews));
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());

        gd_Photos = (GridView) listViews.get(0).findViewById(R.id.gd_Photos);
        tvPhotoSelect  = (TextView) listViews.get(0).findViewById(R.id.tvPhotoSelect);
        ivPhotoDelete  = (ImageView) listViews.get(0).findViewById(R.id.ivPhotoDelete);
        ivPhotoShare   = (ImageView) listViews.get(0).findViewById(R.id.ivPhotoShare);
        viewVideo=findViewById(R.id.viewVideo);
        viewPhotos=findViewById(R.id.viewPhotos);

        mPhotosAdapter = new PhotosAdapter(PlaybackActivity.this, photoList, gd_Photos, flagPhotos);
        gd_Photos.setAdapter(mPhotosAdapter);

        tvPhotoSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagPhotos=true;
                mPhotosAdapter = new PhotosAdapter(PlaybackActivity.this, photoList, gd_Photos, flagPhotos);
                gd_Photos.setAdapter(mPhotosAdapter);
            }
        });

        ivPhotoDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flagPhotos) {
                    String head = getString(R.string.str_playback_warning);
                    String title = getString(R.string.str_playback_deletephoto);
                    AlertDialog.Builder builder = new AlertDialog.Builder(PlaybackActivity.this);
                    builder.setMessage(head);
                    builder.setTitle(title);
                    builder.setPositiveButton(R.string.str_playback_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for(int i=0;i< mPhotosAdapter.mCheckStates.size(); i++){
                                if(mPhotosAdapter.mCheckStates.get(i)) {
                                    File file = new File(photoList.get(i));
                                    file.delete();
                                    photoList.remove(i);
                                    mPhotosAdapter.notifyDataSetChanged();
                                    mPhotosAdapter.viewCheckBox();
                                    mPhotosAdapter.changeStatus=true;
                                    flagPhotos=false;
                                    mPhotosAdapter = new PhotosAdapter(PlaybackActivity.this, photoList, gd_Photos, flagPhotos);
                                    gd_Photos.setAdapter(mPhotosAdapter);
                                }
                            }
                        }
                    });
                    builder.setNegativeButton(R.string.str_playback_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                } else {
                    Utils.showToast(PlaybackActivity.this, "Atleast select one photo");
                }

            }
        });

        ivPhotoShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flagPhotos) {
                    for(int i=0;i< mPhotosAdapter.mCheckStates.size(); i++){
                        if(mPhotosAdapter.mCheckStates.get(i)) {
                            Log.d(">>>>>>>>>>>>>>>>>>>>>>>","Deleted photo items>>>>>>>>>>>>>>>>> "+i);
                            File file = new File(photoList.get(i));
                            String fleName = file.getPath()+"/"+file.getName();
                            shareImage(Uri.parse(fleName));
                        }
                    }
                } else {
                    Utils.showToast(PlaybackActivity.this, "Atleast select one photo");
                }
            }
        });

        gd_Photos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(PlaybackActivity.this, ImagePagerActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        gd_Photos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                dialogDelete(TYPE.PHOTO, position);
                return true;
            }
        });

        gd_Videos = (GridView) listViews.get(1).findViewById(R.id.gd_Videos);
        tvVideoSelect  = (TextView) listViews.get(1).findViewById(R.id.tvVideoSelect);
        ivVideoDelete  = (ImageView) listViews.get(1).findViewById(R.id.ivVideoDelete);
        ivVideoShare   = (ImageView) listViews.get(1).findViewById(R.id.ivVideoShare);

        mVideosAdapter = new VideosAdapter(PlaybackActivity.this, videoList, gd_Videos, flagVideos);
        gd_Videos.setAdapter(mVideosAdapter);

        tvVideoSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagVideos=true;
                mVideosAdapter = new VideosAdapter(PlaybackActivity.this, videoList, gd_Videos, flagVideos);
                gd_Videos.setAdapter(mVideosAdapter);
            }
        });

        ivVideoDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flagVideos) {
                    String head = getString(R.string.str_playback_warning);
                    String title = getString(R.string.str_playback_deletephoto);
                    AlertDialog.Builder builder = new AlertDialog.Builder(PlaybackActivity.this);
                    builder.setMessage(head);
                    builder.setTitle(title);
                    builder.setPositiveButton(R.string.str_playback_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for(int i=0;i< mVideosAdapter.mVideoCheckStates.size(); i++){
                                if(mVideosAdapter.mVideoCheckStates.get(i)) {
                                    Log.d(">>>>>>>>>>>>>>>>>>>>>>>","Deleted photo items>>>>>>>>>>>>>>>>> "+i);
                                    File file = new File(videoList.get(i));
                                    File parentFile = file.getParentFile();
                                    PathConfig.deleteFiles(parentFile);
                                    videoList.remove(i);
                                    mVideosAdapter.notifyDataSetChanged();
                                    flagVideos=false;
                                    mVideosAdapter = new VideosAdapter(PlaybackActivity.this, videoList, gd_Videos, flagVideos);
                                    gd_Videos.setAdapter(mVideosAdapter);
                                }
                            }
                        }
                    });
                    builder.setNegativeButton(R.string.str_playback_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                } else {
                    Utils.showToast(PlaybackActivity.this, "Atleast select one video");
                }
            }
        });
        gd_Videos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(PlaybackActivity.this, VideoPlayerActivity.class);
                intent.putExtra("videoPath", videoList.get(position));
                startActivity(intent);
            }
        });

        gd_Videos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                Log.d(">>>>>>>>>>>>>>>>>>>>>>>","Deleted video items>>>>>>>>>>>>>>>>> "+position);
                //dialogDelete(TYPE.VIDEO, position);
                return true;
            }
        });
    }

    private void initImageView() {
        /*cursor = (ImageView) findViewById(R.id.cursor);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;

        offset = 0;
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.qrcode_scan_line);
        Bitmap newBmp = zoomImg(bmp, screenW / 2, bmp.getHeight() / 2);
        cursor.setImageBitmap(newBmp);

        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        cursor.setImageMatrix(matrix);*/

        txt_Playback_Actionbar = (TextView)findViewById(R.id.txt_Playback_Actionbar);
        txt_Playback_Actionbar.post(new Runnable()
        {

            @Override
            public void run()
            {
                // TODO Auto-generated method stub
                loadImage();
            }
        });
        layout_Actionbar = (RelativeLayout) findViewById(R.id.layout_actionbar);
        iv_Actionbar_Back = (ImageView) findViewById(R.id.iv_playback_actionbar_back);
        iv_Actionbar_Back.setOnClickListener(clickListener);
        iv_Actionbar_More = (ImageView) findViewById(R.id.iv_playback_actionbar_more);
        iv_Actionbar_More.setOnClickListener(clickListener);
        layout_Actionbar.post(new Runnable()
        {

            @Override
            public void run()
            {
                // TODO Auto-generated method stub
                layout_Actionbar_Height = layout_Actionbar.getHeight();
            }
        });
    }

    private void initPopupWndMenu() {
        LayoutInflater mInflater = LayoutInflater.from(PlaybackActivity.this.getApplicationContext());
        View view = mInflater.inflate(R.layout.popup_playback_actionbar_more, null);
        pWindowMenu = new PopupWindow(view, screenWidth / 3, RelativeLayout.LayoutParams.WRAP_CONTENT);
        ListView mListView = (ListView) view.findViewById(R.id.listview_Popup_actionbar_more);
        mAdapter = new SimpleAdapter(PlaybackActivity.this, getData(), R.layout.list_item_playback_menu, new String[] { "icon", "info" }, new int[] { R.id.icon, R.id.info });
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int item, long arg3)
            {
                // TODO Auto-generated method stub
                switch (item) {
                    case 0:
                        if (currSdcardItem != 0) {
                            PathConfig.sdcardItem = PathConfig.SdcardSelector.BUILT_IN;
                            loadImage();
                        }
                        pWindowMenu.dismiss();
                        break;
                    case 1:
                        if (currSdcardItem != 1) {
                            PathConfig.sdcardItem = PathConfig.SdcardSelector.EXTERNAL;
                            loadImage();
                        }
                        pWindowMenu.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void showPopupWndMenu() {
        pWindowMenu.setFocusable(true);
        pWindowMenu.setBackgroundDrawable(new BitmapDrawable(PlaybackActivity.this.getResources(), BitmapFactory.decodeResource(PlaybackActivity.this.getResources(), R.color.transparent)));
        pWindowMenu.setOutsideTouchable(true);
        pWindowMenu.showAtLocation(layout_Actionbar, Gravity.TOP | Gravity.RIGHT, 0, layout_Actionbar_Height);
    }

    private View.OnClickListener clickListener = new View.OnClickListener()
    {

        @Override
        public void onClick(View v)
        {
            // TODO Auto-generated method stub
            switch (v.getId())
            {
                case R.id.iv_playback_actionbar_back:
                    onBackPressed();
                    break;
                case R.id.iv_playback_actionbar_more:
                    initPopupWndMenu();
                    showPopupWndMenu();
                    break;
                default:
                    break;
            }
        }
    };

    private void loadImage() {
        mProgressDialog = ProgressDialog.show(PlaybackActivity.this, null, "Loading...");
        if (PathConfig.sdcardItem == PathConfig.SdcardSelector.EXTERNAL)
        {
            if (PathConfig.getRootPath() == null)
            {
                Utils.showToast(PlaybackActivity.this, getString(R.string.str_playback_notfindexcard));
                PathConfig.sdcardItem = PathConfig.SdcardSelector.BUILT_IN;
                currSdcardItem = 0;
                txt_Playback_Actionbar.setText(R.string.tv_Playback_BuiltinSdcard);
            } else
            {
                PathConfig.sdcardItem = PathConfig.SdcardSelector.EXTERNAL;
                currSdcardItem = 1;
                txt_Playback_Actionbar.setText(R.string.tv_Playback_ExternalSdcard);
            }
        } else
        {
            currSdcardItem = 0;
            txt_Playback_Actionbar.setText(R.string.tv_Playback_BuiltinSdcard);
        }
        getPhotoVideoList(new File(PathConfig.getRootPath() + PathConfig.PHOTOS_PATH), new File(PathConfig.getRootPath() + PathConfig.VIDEOS_PATH));
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> photo = new HashMap<String, Object>();
        photo.put("icon", R.drawable.playback_menu_phone);
        photo.put("info", this.getString(R.string.tv_Playback_BuiltinSdcard));
        list.add(photo);

        photo = new HashMap<String, Object>();
        photo.put("icon", R.drawable.playback_menu_sdcard);
        photo.put("info", this.getString(R.string.tv_Playback_ExternalSdcard));
        list.add(photo);

        return list;
    }

    private void shareImage(Uri imagePath) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, imagePath);
        startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case SCAN_OK:
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    mPhotosAdapter = new PhotosAdapter(PlaybackActivity.this, photoList, gd_Photos, flagPhotos);
                    gd_Photos.setAdapter(mPhotosAdapter);

                    mVideosAdapter = new VideosAdapter(PlaybackActivity.this, videoList, gd_Videos, flagVideos);
                    gd_Videos.setAdapter(mVideosAdapter);
                    break;

                default:
                    break;
            }
            return true;
        }
    });

    private void getPhotoVideoList(final File photoPath, final File videoPath) {
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                // TODO Auto-generated method stub
                // photo list
                photoList.clear();
                photoList = PathConfig.getImagesList(photoPath);

                // video list
                videoList.clear();
                videoList = PathConfig.getVideosList(videoPath);
                Log.e(TAG, "images size:" + photoList.size() + " videos size:" + videoList.size());
                handler.sendEmptyMessage(SCAN_OK);
            }
        }).start();
    }

    private Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    public class MyPagerAdapter extends PagerAdapter {
        public List<View> mListViews;

        public MyPagerAdapter(List<View> mListViews)
        {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mListViews.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount()
        {
            return mListViews.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
            return mListViews.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1)
        {
            return arg0 == (arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState()
        {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }

    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;
        public MyOnClickListener(int i)
        {
            index = i;
        }
        @Override
        public void onClick(View v) {
            if (index==0){
                viewPhotos.setVisibility(View.VISIBLE);
                viewVideo.setVisibility(View.INVISIBLE);
            }if (index==1){
                viewPhotos.setVisibility(View.INVISIBLE);
                viewVideo.setVisibility(View.VISIBLE);
            }
            mPager.setCurrentItem(index);
        }
    };

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        int one = offset * 2 + screenWidth / 2;
        @Override
        public void onPageSelected(int arg0)
        {
            Animation animation = null;
            switch (arg0)
            {
                case 0:

                    if (currIndex == 1)
                    {
                        animation = new TranslateAnimation(one, 0, 0, 0);
                        viewPhotos.setVisibility(View.VISIBLE);
                        viewVideo.setVisibility(View.INVISIBLE);
                    }
                    break;
                case 1:
                    viewPhotos.setVisibility(View.INVISIBLE);
                    viewVideo.setVisibility(View.VISIBLE);
                    if (currIndex == 0)
                    {
                        animation = new TranslateAnimation(offset, one, 0, 0);
                        viewPhotos.setVisibility(View.INVISIBLE);
                        viewVideo.setVisibility(View.VISIBLE);
                    }
                    break;
            }
            currIndex = arg0;
            animation.setFillAfter(true);
            animation.setDuration(300);
            viewVideo.clearAnimation();
            viewPhotos.clearAnimation();
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    private void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DELETE_PHOTO);
        registerReceiver(broadcastReceivers, intentFilter);
    }

    private void unregister()
    {
        unregisterReceiver(broadcastReceivers);
    }

    private BroadcastReceiver broadcastReceivers = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            String action = arg1.getAction();
            if (action.equals(ACTION_DELETE_PHOTO)) {
                Log.e(TAG, "get receiver");
                mPhotosAdapter.notifyDataSetChanged();
            }
        }
    };

    public void onPause() {
        super.onPause();
        //unregisterReceiver(broadcastReceivers);
    }

    private void dialogPhotoDelete(final int position) {
        File file = new File(photoList.get(position));
        file.delete();
        photoList.remove(position);
        mPhotosAdapter.notifyDataSetChanged();
    }

    private void dialogDelete(final TYPE mType, final int position) {
        String msg = "";
        String title = this.getString(R.string.str_playback_warning);
        if (mType == TYPE.PHOTO) {
            msg = this.getString(R.string.str_playback_deletephoto);
        } else if (mType == TYPE.VIDEO) {
            msg = this.getString(R.string.str_playback_deletevideo);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(PlaybackActivity.this);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton(R.string.str_playback_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if (mType == TYPE.PHOTO) {
                    File file = new File(photoList.get(position));
                    file.delete();
                    photoList.remove(position);
                    mPhotosAdapter.notifyDataSetChanged();
                } else if (mType == TYPE.VIDEO)
                {
                    File file = new File(videoList.get(position));
                    File parentFile = file.getParentFile();
                    PathConfig.deleteFiles(parentFile);
                    videoList.remove(position);
                    mVideosAdapter.notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton(R.string.str_playback_cancel, new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });

        builder.create().show();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        //unregisterReceiver(broadcastReceivers);
        Log.d(TAG, "on stop.. unregister");
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        unregister();
        finish();
        //startActivity(new Intent( SecurityActivity.this, MainActivity.class));
        //overridePendingTransition(R.anim.left_out, R.anim.left_out);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            initPopupWndMenu();
            showPopupWndMenu();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void authenticateApp() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent i = keyguardManager.createConfirmDeviceCredentialIntent(getResources().getString(R.string.unlock), getResources().getString(R.string.confirm_pattern));
            try {
                startActivityForResult(i, LOCK_REQUEST_CODE);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                try {
                    startActivityForResult(intent, SECURITY_SETTING_REQUEST_CODE);
                } catch (Exception ex) {
                    //textView.setText(getResources().getString(R.string.setting_label));
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOCK_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    //textView.setText(getResources().getString(R.string.unlock_success));
                } else {
                    finish();
                    //textView.setText(getResources().getString(R.string.unlock_failed));
                }
                break;
            case SECURITY_SETTING_REQUEST_CODE:
                if (isDeviceSecure()) {
                    Utils.showToast(PlaybackActivity.this, getResources().getString(R.string.device_is_secure));
                    authenticateApp();
                } else {
                    finish();
                    //textView.setText(getResources().getString(R.string.security_device_cancelled));
                }

                break;
        }
    }

    private boolean isDeviceSecure() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && keyguardManager.isKeyguardSecure();
    }

}
