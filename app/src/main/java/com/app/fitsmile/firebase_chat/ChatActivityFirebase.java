package com.app.fitsmile.firebase_chat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppController;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.utils.LocaleManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.ContentUriUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static com.app.fitsmile.common.Utils.closeSendingProgressDialog;
import static com.app.fitsmile.common.Utils.isOnline;
import static com.app.fitsmile.common.Utils.isVideoFile;
import static com.app.fitsmile.common.Utils.openSendingProgressDialog;
import static com.app.fitsmile.common.Utils.setSendingMessage;
import static com.app.fitsmile.common.Utils.showToast;
import static com.app.fitsmile.common.Utils.startSendingAnimation;
import static com.app.fitsmile.common.Utils.stopSendingAnimation;
import static com.app.fitsmile.firebase_chat.ChatConstant.LOCAL_DIRECTORY;
import static com.app.fitsmile.firebase_chat.ChatConstant.MESSAGES;
import static com.app.fitsmile.firebase_chat.ChatConstant.OFFLINE;
import static com.app.fitsmile.firebase_chat.ChatConstant.ONLINE;
import static com.app.fitsmile.firebase_chat.ChatConstant.STATUS;
import static com.app.fitsmile.firebase_chat.FirebaseConstants.CROPPED_VIDEOS;
import static com.app.fitsmile.firebase_chat.FirebaseConstants.OPPONENT_ID;
import static com.app.fitsmile.firebase_chat.FirebaseConstants.OPPONENT_NAME;
import static com.app.fitsmile.firebase_chat.FirebaseConstants.OPPONENT_PROFILE_PIC;
import static com.app.fitsmile.firebase_chat.FirebaseConstants.VIDEOS_TO_TRIM;
import static com.app.fitsmile.firebase_chat.FirebaseUtils.getRoomName;
import static com.app.fitsmile.firebase_chat.FirebaseUtils.sendFileMessage;
import static com.app.fitsmile.firebase_chat.FirebaseUtils.sendImageMessage;
import static com.app.fitsmile.firebase_chat.FirebaseUtils.sendTextMessage;
import static com.app.fitsmile.firebase_chat.FirebaseUtils.sendTypingStarted;
import static com.app.fitsmile.firebase_chat.FirebaseUtils.sendTypingStopped;
import static com.app.fitsmile.firebase_chat.FirebaseUtils.sendVideoMessage;

public class ChatActivityFirebase extends BaseActivity implements FirebaseUploadFilesListener {
    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_TRIMMED_VIDEO = 299;
    public static final int FILE_REQUEST_CODE = 399;
    public static String ZoomImage = "";
    private final int REQUEST_WRITE_PERMISSIONS = 10;
    public String id, token, doctorId = "";
    ImageView btnAttach, btnFile;
    LinearLayout form;
    RelativeLayout chat_lay;
    Toolbar toolbar;
    BaseActivity activity;
    String[] mimeTypes =
            {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                    "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                    "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                    "application/pdf"};
    int unreadCount = 0;
    Typing currentTyping = new Typing();
    int uploadMediaPosition = -1;
    int uploadFilePosition = -1;
    private ChatMessageFirebaseAdapter adapter;
    private RecyclerView chatRecyclerView;
    private EditText editText;
    private List<ChatMessage> chatMessages;
    private HashMap<String, Integer> chatMessagesIndexes;
    private DatabaseReference allChatsReference;
    private DatabaseReference isTypingReference;
    private DatabaseReference onlineStatusReference;
    private ChildEventListener allChatsChangeListener;
    private ChildEventListener isTypingChangeListener;
    private ChildEventListener userOnlineStatusChangeListener;
    private String opponentName;
    private String opponentId;
    private String opponentProfilePic;
    //    private Card opponentPublicKey;
    private ArrayList<String> mediaAttachments = new ArrayList<>();
    private ArrayList<String> fileAttachments = new ArrayList<>();
    private TextView opponentTypingLabel;
    private ShimmerFrameLayout shimmerFrameLayout;
    private RelativeLayout chatLayout;
    private boolean legacyChatsFetched = false;
    private ProgressBar legacyChatLoader;
    private TextView unreadCountLabel;

    public static String getMimeType(String url) {
        try {
            String type;
            String extension = url.substring(url.lastIndexOf(".") + 1);
            Log.i("extension", "ext : " + extension);
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            return type;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getOpponentId() {
        return opponentId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_firebase);
        LocaleManager.setLocale(this);
        activity = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        chatLayout = findViewById(R.id.chatLayout);
        unreadCountLabel = findViewById(R.id.unreadCountLabel);
        legacyChatLoader = findViewById(R.id.legacyChatLoader);
        shimmerFrameLayout = findViewById(R.id.shimmer_layout);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmerAnimation();
        chatLayout.setVisibility(View.GONE);
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, RECORD_AUDIO, CALL_PHONE, READ_EXTERNAL_STORAGE}, 0);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            opponentId = getIntent().getStringExtra(OPPONENT_ID);
            opponentName = getIntent().getStringExtra(OPPONENT_NAME);
            opponentProfilePic = getIntent().getStringExtra(OPPONENT_PROFILE_PIC);
            if (opponentProfilePic != null && opponentProfilePic.isEmpty()) {
                opponentProfilePic = null;
            }
            setStatusOfOpponent(OFFLINE);
        }
        if (opponentId == null || opponentId.isEmpty()) {
            showToast(this, getResources().getString(R.string.chat_not_available));
            finish();
            return;
        }

        if (opponentId.equals(appPreference.getFirebaseUID())) {
            showToast(this, getResources().getString(R.string.cannot_chat_with_himself));
            finish();
            return;
        }

        allChatsChangeListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("Firebase", "Added " + s);
                parseMessagesAndShow(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("Firebase", "Changed " + s);
                modifyMessagesAndShow(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d("Firebase", "Removed");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        isTypingChangeListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                parseTypingEvent(snapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                parseTypingEvent(snapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                parseTypingEvent(snapshot);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                parseTypingEvent(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        userOnlineStatusChangeListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("Firebase", "Added " + s);
                if (!appPreference.getFirebaseUID().isEmpty()) {
                    if (dataSnapshot.getKey().equals("state")) {
                        String onlineStatus = (String) dataSnapshot.getValue();
                        setStatusOfOpponent(onlineStatus);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("Firebase", "Changed " + s);
                if (!appPreference.getFirebaseUID().isEmpty()) {
                    if (dataSnapshot.getKey().equals("state")) {
                        String onlineStatus = (String) dataSnapshot.getValue();
                        setStatusOfOpponent(onlineStatus);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d("Firebase", "Removed");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        shimmerFrameLayout.setVisibility(View.GONE);
        shimmerFrameLayout.stopShimmerAnimation();
        chatLayout.setVisibility(View.VISIBLE);

        chatRecyclerView = findViewById(R.id.list_msg);
        form = findViewById(R.id.form);
        chat_lay = findViewById(R.id.chat_lay);
        opponentTypingLabel = findViewById(R.id.opponentTypingLabel);
        doctorId = appPreference.getUserId();
        btnAttach = findViewById(R.id.btn_attach);
        btnFile = findViewById(R.id.btn_file);
        editText = findViewById(R.id.msg_type);
        ImageView imageSend = findViewById(R.id.btn_chat_send);
        editText.requestFocus();
        allChatsReference = AppController.getDatabaseReference().child(MESSAGES).child(getRoomName(appPreference, opponentId));
        isTypingReference = FirebaseUtils.listenToOpponentTyping(activity, opponentId);
        onlineStatusReference = AppController.getDatabaseReference().child(STATUS).child(opponentId);

        unreadCountLabel.setOnClickListener(view -> {
            int lastPosition = chatMessages.size() - 1;
            chatRecyclerView.smoothScrollToPosition(lastPosition);
            unreadCount = 0;
            unreadCountLabel.setVisibility(View.GONE);
        });

        btnAttach.setOnClickListener(v -> Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Options options = Options.init()
                                    .setRequestCode(100)                                           //Request code for activity results
                                    .setCount(5)                                                   //Number of images to restrict selection count
                                    .setFrontfacing(false)                                         //Front Facing camera on start
                                    .setExcludeVideos(false)                                       //Option to exclude videos
                                    .setVideoDurationLimitinSeconds(30)                            //Duration for video recording
                                    .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                                    .setPath(LOCAL_DIRECTORY);                                       //Custom Path For media Storage

                            Pix.start(activity, options);
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check());

        btnFile.setOnClickListener(view -> {
            if (hasExternalStorageReadPermission()) {
                getFilePicker();
            } else {
                askExternalReadPermission();
            }
        });

        imageSend.setOnClickListener(view -> {
            String message = editText.getText().toString().trim();
            if (!message.isEmpty()) {
                if (isOnline(getApplicationContext())) {
                    sendTextMessage(activity, message, opponentId /*,opponentPublicKey*/);
                    editText.setText(null);
                } else {
                    showToast(activity, getResources().getString(R.string.please_check_your_network));
                }
            } else {
                showToast(activity, getResources().getString(R.string.type_some_message));
            }
        });
        chatMessages = new ArrayList<>();
        chatMessagesIndexes = new HashMap<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        adapter = new ChatMessageFirebaseAdapter(activity, chatMessages, opponentProfilePic/*, myPublicKey, opponentPublicKey*/);
        chatRecyclerView.setAdapter(adapter);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if ((layoutManager.findFirstCompletelyVisibleItemPosition() == 0) && !legacyChatsFetched) {
                    // getLegacyChats();
                }
                int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();

                for (int i = 0; i <= lastVisiblePosition; i++) {
                    markMessageAsRead(chatMessages.get(i));
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    sendTypingStarted(activity, opponentId);
                } else {
                    sendTypingStopped(activity);
                }
            }
        });

        startListeningToMessages();
        startListeningToTypingEvent();
        startListeningToOnlineStatus();
        /*((AppController) getApplication()).getMyPublicKey(new GetVirgilPublicKeyListener() {

            @Override
            public void onPublicKeyFound(Card myPublicKey) {

            }

            @Override
            public void onError(String error) {
                showToast(activity, error);
            }
        });*/

        /*((AppController) getApplication()).initialiseVirgil(new VirgilInitializeListener() {
            @Override
            public void onInitialized() {
                ((AppController) getApplication()).getPublicKey(opponentId, new GetVirgilPublicKeyListener() {

                    @Override
                    public void onPublicKeyFound(Card opponentCard) {
                        opponentPublicKey = opponentCard;


                    }

                    @Override
                    public void onError(String error) {
                        shimmerFrameLayout.setVisibility(View.GONE);
                        shimmerFrameLayout.stopShimmerAnimation();
                        showToast(activity, error);
                        activity.finish();
                    }
                });
            }

            @Override
            public void onError(String message) {
                shimmerFrameLayout.setVisibility(View.GONE);
                shimmerFrameLayout.stopShimmerAnimation();
                showToast(activity, message);
            }
        });*/
    }

    private void setStatusOfOpponent(String onlineStatus) {
        TextView actionBarTitle = toolbar.findViewById(R.id.action_bar_title);
        if (onlineStatus.equals(ONLINE)) {
            Drawable dr = activity.getResources().getDrawable(R.drawable.online_dot);
            Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
            Drawable d = new BitmapDrawable(activity.getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
            actionBarTitle.setCompoundDrawablesWithIntrinsicBounds(
                    d, null, null, null);
            toolbar.findViewById(R.id.onlineStatus).setVisibility(View.GONE);
        } else {
            toolbar.findViewById(R.id.onlineStatus).setVisibility(View.VISIBLE);
            ((TextView) toolbar.findViewById(R.id.onlineStatus)).setText(getResources().getString(R.string.offline));
            actionBarTitle.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, null, null);
        }
        actionBarTitle.setText(opponentName);
        Picasso.get().load(opponentProfilePic).placeholder(R.drawable.man_checked).into((CircleImageView) toolbar.findViewById(R.id.opponentPic));
    }

    @Override
    protected void onStop() {
        super.onStop();
        sendTypingStopped(activity);
    }


    private void getLegacyChats() {
        legacyChatLoader.setVisibility(View.VISIBLE);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("receiver_firebase_uid", opponentId);
        jsonObj.addProperty("sender_firebase_uid", appPreference.getFirebaseUID());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<LegacyChatsResponse> mService;
        mService = mApiService.getLegacyChats(jsonObj);
        mService.enqueue(new Callback<LegacyChatsResponse>() {
            @Override
            public void onResponse(Call<LegacyChatsResponse> call, Response<LegacyChatsResponse> response) {
                LegacyChatsResponse res = response.body();
                if (res != null) {
                    if (res.getStatus().equals("1")) {
                        legacyChatsFetched = true;
                        List<ChatMessage> legacyChatMessages = res.getChat_history();
                        for (int i = 0; i < legacyChatMessages.size(); i++) {
                            legacyChatMessages.get(i).isLegacyChatMessage = true;
                        }
                        adapter.addLegacyChats(legacyChatMessages);
                    } else if (res.getStatus().equals("0")) {
                        //No old chats
                        legacyChatsFetched = true;
                    } else if (res.getStatus().equals("401")) {
                        Utils.showSessionAlert(activity);
                    } else {
                        showToast(activity, "" + res.getMessage());
                    }
                }
                legacyChatLoader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<LegacyChatsResponse> call, Throwable t) {
                legacyChatLoader.setVisibility(View.GONE);
                call.cancel();
            }
        });
    }

    private Boolean hasExternalStorageReadPermission() {
        boolean hasPermission = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false;
            }
        }
        return hasPermission;
    }

    private void getFilePicker() {
        FilePickerBuilder.getInstance()
                .setActivityTheme(R.style.LibAppTheme) //optional
                .setMaxCount(5)
                .enableSelectAll(false)
                .pickFile(activity);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getFilePicker();
            } else {
                Toast.makeText(this, "No permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void askExternalReadPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSIONS);
        } else {
            Toast.makeText(this, "No permission", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendTypingStopped(activity);
        stopListeningToMessages();
        stopListeningToTypingEvents();
        stopListeningToOnlineStatusEvents();
    }

    private void stopListeningToOnlineStatusEvents() {
        if (onlineStatusReference != null && userOnlineStatusChangeListener != null) {
            onlineStatusReference.removeEventListener(userOnlineStatusChangeListener);
        }
    }

    private void stopListeningToMessages() {
        if (allChatsReference != null && allChatsChangeListener != null) {
            allChatsReference.removeEventListener(allChatsChangeListener);
        }
    }

    private void stopListeningToTypingEvents() {
        if (isTypingReference != null && isTypingChangeListener != null) {
            isTypingReference.removeEventListener(isTypingChangeListener);
        }
    }

    private void startListeningToMessages() {
        if (!activity.appPreference.getFirebaseUID().isEmpty() && allChatsReference != null && allChatsChangeListener != null) {
            allChatsReference.addChildEventListener(allChatsChangeListener);
        }
    }

    private void startListeningToOnlineStatus() {
        if (!activity.appPreference.getFirebaseUID().isEmpty() && onlineStatusReference != null && userOnlineStatusChangeListener != null) {
            onlineStatusReference.addChildEventListener(userOnlineStatusChangeListener);
        }
    }

    private void startListeningToTypingEvent() {
        if (!activity.appPreference.getFirebaseUID().isEmpty() && isTypingReference != null && isTypingChangeListener != null) {
            isTypingReference.addChildEventListener(isTypingChangeListener);
        }
    }

    public void parseMessagesAndShow(DataSnapshot dataSnapshot) {
        ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
        if (message != null) {
            message.uniqueKey = dataSnapshot.getKey();
            if(!message.getContentType().isEmpty()){
                chatMessages.add(message);
                Log.i("Message: ", message.getMessage());
                Log.i("type: ", message.getContentType());
                chatMessagesIndexes.put(message.uniqueKey, chatMessages.size() - 1);
            }

        }
        adapter.notifyItemInserted(chatMessages.size() - 1);
        if (!message.isMyMessage(appPreference)) {
            if (message.getIsRead() == 1) {
                chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
            } else {
                unreadCount++;
                showUnreadCountLabel();
            }
        } else {
            chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
        }
    }

    private void showUnreadCountLabel() {
        if (unreadCount == 1) {
            unreadCountLabel.setText(unreadCount + " new message");
        } else if (unreadCount > 99) {
            unreadCountLabel.setText("99+ new message");
        } else {
            unreadCountLabel.setText(unreadCount + " new messages");
        }
        unreadCountLabel.setVisibility(View.VISIBLE);
    }

    private void markMessageAsRead(ChatMessage message) {
        if (!message.isMyMessage(appPreference) && message.getIsRead() == 0) {
            FirebaseUtils.setMessageRead(appPreference, message.uniqueKey, opponentId);
            if (unreadCount > 0) {
                unreadCount--;
            }
            if (unreadCount > 0) {
                showUnreadCountLabel();
            } else {
                unreadCountLabel.setVisibility(View.GONE);
            }
        }
    }

    public void modifyMessagesAndShow(DataSnapshot dataSnapshot) {
        ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
        if (chatMessagesIndexes.containsKey(dataSnapshot.getKey())) {
            int index = chatMessagesIndexes.get(dataSnapshot.getKey());
            chatMessages.set(index, message);
            adapter.notifyItemChanged(index);
        }
    }

    public void parseTypingEvent(DataSnapshot snapshot) {
        if (snapshot.getKey().equals("typing")) {
            currentTyping.setTyping((Boolean) snapshot.getValue());
        } else if (snapshot.getKey().equals("typingTo")) {
            currentTyping.setTypingTo((String) snapshot.getValue());
        }
        if (currentTyping.isTyping() && currentTyping.getTypingTo() != null && currentTyping.getTypingTo().equals(activity.appPreference.getFirebaseUID())) {
            opponentTypingLabel.setVisibility(View.VISIBLE);
            opponentTypingLabel.setText(opponentName + " is typing...");
        } else {
            opponentTypingLabel.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));

        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });

        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE) {
                if (data != null) {
                    ArrayList<String> selectedMedia = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                    if (selectedMedia != null) {
                        ArrayList<String> selectedOriginalVideos = new ArrayList<>();
                        for (String mediaPath : selectedMedia) {
                            if (isVideoFile(mediaPath)) {
                                if (mediaPath.contains(".mp4")) {
//                                    selectedOriginalVideos.add(mediaPath);
                                    File file = new File(mediaPath);
                                    long length = file.length();
                                    long sizeInkb  = length/1024;
//                                    Toast.makeText(ChatActivityFirebase.this, "Video size:"+length+"KB",
//                                            Toast.LENGTH_LONG).show();
                                    long sizeInMb=sizeInkb/1024;
                                    if(sizeInMb<100){
                                        selectedOriginalVideos.add(mediaPath);
                                    }
                                    else {
                                        Toast.makeText(ChatActivityFirebase.this,R.string.video_limit_alert, Toast.LENGTH_SHORT).show();
                                        return;
                                    }

//                                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//                                    retriever.setDataSource(mediaPath);
//                                    long duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
//                                    int width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
//                                    int height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
//                                    retriever.release();

                                } else {
                                    Toast.makeText(ChatActivityFirebase.this, R.string.only_mp4_video_allowd, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } else {
                                mediaAttachments.add(mediaPath);
                            }
                        }
                        if (selectedOriginalVideos.size() > 0) {
//                            Intent intent = new Intent(this, VideoTrimActivity.class);
//                            Bundle bundle = new Bundle();
//                            bundle.putStringArrayList(VIDEOS_TO_TRIM, selectedOriginalVideos);
//                            intent.putExtras(bundle);
//                            startActivityForResult(intent, REQUEST_TRIMMED_VIDEO);

                            mediaAttachments.addAll(selectedOriginalVideos);
                            sendNextAttachmentsToAWS();


                        } else {
                            sendNextAttachmentsToAWS();
                        }
                    }
                }
            } else if (requestCode == REQUEST_TRIMMED_VIDEO) {
                // Trimmed Videos fetched
                if (data != null) {
                    ArrayList<String> croppedVideos = data.getStringArrayListExtra(CROPPED_VIDEOS);
                    if (croppedVideos != null && croppedVideos.size() > 0) {
                         mediaAttachments.addAll(croppedVideos);
                            sendNextAttachmentsToAWS();
                    }
                }
            } else if (requestCode == FilePickerConst.REQUEST_CODE_DOC) {
                if (data != null) {
                    ArrayList<Uri> fileUris = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
                    if (fileUris != null && fileUris.size() > 0) {
                        boolean isNonFile = false;
                        for (Uri fileUri : fileUris) {
                            try {
                                String path = ContentUriUtils.INSTANCE.getFilePath(activity, fileUri);
                                if (Arrays.asList(mimeTypes).contains(getMimeType(path)) && path != null) {
                                    fileAttachments.add(path);
                                } else {
                                    isNonFile = true;
                                }
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                        }
                        if (isNonFile) {
                            showToast(activity, getResources().getString(R.string.file_not_document));
                        } else {
                            sendNextFileToAWS();
                        }
                    }
                }
            }
        }
    }

    private void sendFileToAWS(String filename) {
        openSendingProgressDialog(this);
        if (fileAttachments.size() > 1) {
            setSendingMessage((uploadFilePosition + 1) + "/" + fileAttachments.size());
        } else {
            setSendingMessage("");
        }
        AWSUtils utils = new AWSUtils();
        utils.upload(activity, filename, true, this);
        startSendingAnimation();
    }

    public void sendMediaToAWS(String filename) {
        openSendingProgressDialog(this);
        if (mediaAttachments.size() > 1) {
            setSendingMessage((uploadMediaPosition + 1) + "/" + mediaAttachments.size());
        } else {
            setSendingMessage("");
        }
        AWSUtils utils = new AWSUtils();
        utils.upload(activity, filename, false, this);
        startSendingAnimation();
    }

    private void sendNextAttachmentsToAWS() {
        uploadMediaPosition++;
        sendMediaToAWS(mediaAttachments.get(uploadMediaPosition));
    }

    private void sendNextFileToAWS() {
        uploadFilePosition++;
        sendFileToAWS(fileAttachments.get(uploadFilePosition));
    }

    /*@Override
    public void onProgressUpdate(int percentage) {
        CommonClass.setSendingProgress(percentage);
        Log.d("Upload", percentage + "% uploaded");
    }*/

    @Override
    public void onUploadSuccess(String filename, boolean isFile) {
        stopSendingAnimation();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            closeSendingProgressDialog();
            if (isFile) {
                if (uploadFilePosition >= 0) {
                    sendFileMessage(activity, opponentId, filename/*, opponentPublicKey*/);
                    if (uploadFilePosition < fileAttachments.size() - 1) {
                        sendNextFileToAWS();
                    } else {
                        uploadFilePosition = -1;
                        fileAttachments.clear();
                    }
                } else {
                    fileAttachments.clear();
                }
            } else {
                if (uploadMediaPosition >= 0) {
                    if (!isVideoFile(mediaAttachments.get(uploadMediaPosition))) {
                        sendImageMessage(activity, opponentId, filename/*, opponentPublicKey*/);
                    } else {
                        sendVideoMessage(activity, opponentId, filename/*, opponentPublicKey*/);
                    }
                    if (uploadMediaPosition < mediaAttachments.size() - 1) {
                        sendNextAttachmentsToAWS();
                    } else {
                        uploadMediaPosition = -1;
                        mediaAttachments.clear();
                    }
                } else {
                    mediaAttachments.clear();
                }
            }
        }, 2000);

    }

    @Override
    public void onUploadError(String errorMessage, boolean isFile) {
        showToast(this, errorMessage);
        if (isFile) {
            if (uploadFilePosition < fileAttachments.size() - 1) {
                sendNextFileToAWS();
            } else {
                uploadFilePosition = -1;
                fileAttachments.clear();
                closeSendingProgressDialog();
            }
        } else {
            if (uploadMediaPosition < mediaAttachments.size() - 1) {
                sendNextAttachmentsToAWS();
            } else {
                uploadMediaPosition = -1;
                mediaAttachments.clear();
                closeSendingProgressDialog();
            }
        }
    }
}

