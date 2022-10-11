package com.app.fitsmile.app;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.app.fitsmile.MainActivity;
import com.app.fitsmile.appointment.AppointmentListActivity;
import com.app.fitsmile.appointment.VideoCallActivity;
import com.app.fitsmile.appointment.VideoCallStartActivity;
import com.app.fitsmile.firebase_chat.ChatActivityFirebase;
import com.app.fitsmile.firebase_chat.ChatDataFirebase;
import com.app.fitsmile.firebase_chat.ChatListFirebaseResponse;
import com.app.fitsmile.firebase_chat.ChatMessage;
import com.app.fitsmile.firebase_chat.FirebaseUtils;
import com.app.fitsmile.firebase_chat.IGetUnreadMessagesCount;
import com.app.fitsmile.firebase_chat.InitializeListener;
import com.app.fitsmile.firebase_chat.MyFirebaseMessagingService;
import com.app.fitsmile.intra.utils.CmdSocket;
import com.app.fitsmile.utils.LocaleManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.app.fitsmile.BuildConfig.BASE_URL;
import static com.app.fitsmile.common.Utils.showSessionAlert;
import static com.app.fitsmile.firebase_chat.ChatConstant.DEVICE_TOKENS;
import static com.app.fitsmile.firebase_chat.ChatConstant.MESSAGES;
import static com.app.fitsmile.firebase_chat.FirebaseConstants.OPPONENT_ID;
import static com.app.fitsmile.firebase_chat.FirebaseConstants.OPPONENT_NAME;
import static com.app.fitsmile.firebase_chat.FirebaseConstants.OPPONENT_PROFILE_PIC;
import static com.app.fitsmile.firebase_chat.FirebaseUtils.getRoomName;

public class AppController extends Application implements LifecycleObserver {

    public static final String TAG = AppController.class.getSimpleName();
    private static AppController appController;
    private static FirebaseUser user;
    private static DatabaseReference mFirebaseDatabaseReference;
    public static String Name = "";
    private static String currentDeviceTokenOnFirebase;
    private static String currentFCMDeviceToken;
    private final ArrayList<DatabaseReference> allChatsReferences = new ArrayList<>();
    private final ArrayList<ChildEventListener> allChatsChangeListeners = new ArrayList<>();
    int unreadMessagesCount = 0;
    private Activity activeActivity;
    private AppPreference appPreference;
    private Retrofit retrofit = null;
    private RequestQueue mRequestQueue;
    public static String defaultIpAddr = "192.168.10.123";
    public static CmdSocket cmdSocket;
    private FirebaseAuth mAuth;
    //    private HashMap<String, Card> publicKeys;
//    private Card myPublicKey;
//    private VirgilDevice senderDevice;
    private Intent firebaseService;
    private DatabaseReference deviceTokenChangeReference;
    private ChildEventListener deviceTokenChangeListener;
    public static boolean isVideoCallComing = false;

    public static DatabaseReference getDatabaseReference() {
        return mFirebaseDatabaseReference;
    }

    public static String getCurrentFCMDeviceToken() {
        if (currentFCMDeviceToken == null) {
            return "";
        }
        return currentFCMDeviceToken;
    }

    public static void setCurrentFCMDeviceToken(String token) {
        currentFCMDeviceToken = token;
    }

    public static AppController getInstance() {
        return appController;
    }

    public static void logoutFromFirebase() {
        FirebaseAuth.getInstance().signOut();
    }

    public void makeUserOnline() {
        FirebaseUtils.setOnline(appPreference);
    }

    public void makeUserOffline() {
        String myDeviceToken = getCurrentFCMDeviceToken();
        if (!appPreference.isLoggedIn()) {
            return;
        }
        if (myDeviceToken.isEmpty()) {
            return;
        }
        if (currentDeviceTokenOnFirebase == null || currentDeviceTokenOnFirebase.isEmpty()) {
            return;
        }
        if (currentDeviceTokenOnFirebase.equals(myDeviceToken)) {
            FirebaseUtils.setOffline(appPreference);
        }
    }

    public void clearDeviceToken() {
        FirebaseUtils.clearToken(appPreference);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appController = this;
        LocaleManager.setLocale(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        appPreference = new AppPreference(this);
        retrofit = getClient();
        cmdSocket = new CmdSocket(getApplicationContext());
        firebaseService = new Intent(this, MyFirebaseMessagingService.class);
        FirebaseApp.initializeApp(this);
        LocaleManager.setLocale(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        setupActivityListener();
        notification();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    @OnLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_STOP)
    void onAppBackgrounded() {
        //App in background
        makeUserOffline();
        Log.e("Raman", "************* backgrounded");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onAppForegrounded() {
        makeUserOnline();
        Log.e("Raman", "************* foregrounded");
        // App in foreground
    }


    public void notification() {
        OneSignal.startInit(appController)
                .setNotificationReceivedHandler(new ExampleNotificationReceiverHandlers(appController))
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandlers(appController))
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        cmdSocket.stopSocket();
        cmdSocket = null;
    }

    public Activity getCurrentActivity() {
        return activeActivity;
    }

    private void setupActivityListener() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                activeActivity = activity;
                startListeningToSessionChanges(activity);
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                //  stopListeningToSessionChanges(activity);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    public AppPreference getAppPreference() {
        return appPreference;
    }

    public Retrofit getClient() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("Authorization", appPreference.getToken())
                                .header("Dentulu-Userid",appPreference.getUserId())
                                .header("Sessiontoken", appPreference.getIsAuthentication())
                                .header("Content-Type", "application/json")
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    }
                })
                .build();

        Gson gson = new GsonBuilder().setLenient().create();
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient).build();
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(com.android.volley.Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(com.android.volley.Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    private class ExampleNotificationReceiverHandlers implements OneSignal.NotificationReceivedHandler {

        private final Context context;

        public ExampleNotificationReceiverHandlers(Context context) {
            this.context = context;
        }

        @Override
        public void notificationReceived(OSNotification notification) {
            try {
                Log.d("Notification", "Patient App " + notification.payload);
                JSONObject data = notification.payload.additionalData;
                String type = data.optString("type");
                if (type.equals("videoCallRequest")) {
                    Log.d(">>>Test>>>", ">>>>>>>>>notificationReceived: videoCallRequest>>");
                    String strToken = null;
                    try {
                        strToken = notification.payload.additionalData.getString("token");
                        String strChannel = notification.payload.additionalData.getString("channel_id");
                        String strSender = notification.payload.additionalData.getString("caller_id");
                        String strReceiver = notification.payload.additionalData.getString("uidstr");
                        String strFname = notification.payload.additionalData.getString("first_name");
                        String strLname = notification.payload.additionalData.getString("last_name");
                        String is_emergency = notification.payload.additionalData.getString("is_emergency");
                        String limitedDuration = notification.payload.additionalData.getString("limitedDuration");
                        String strVideoId = notification.payload.additionalData.getString("appointment_id");
                        Name = strFname + " " + strLname;

                        appPreference.setAgoraCallDetails(strToken, strChannel, strSender, strReceiver, strFname, strLname, strVideoId, is_emergency, limitedDuration);
                    } catch (JSONException e) {
                        Log.e("Raman....2", "ntoficatation_catch");

                        e.printStackTrace();
                    }
                    NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    //  notifManager.cancelAll();
                    Log.e("Raman....2", "receive");
                    Log.e("Raman....2", "receive" + context);
                    isVideoCallComing = true;
                    Intent intent = new Intent(context, VideoCallStartActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(AppConstants.OPEN_FROM, 4);
                    context.startActivity(intent);
                    Log.e("Raman....3", "receive");
                    //  OneSignal.cancelNotification(notification.androidNotificationId);
                } else if (type.equals("videoCallReject")) {
                    if (VideoCallStartActivity.activity != null) {
                        Log.d(">>>Raman>>>", ">>>>>>>>>>>>>>>>>>>>>>>>videoCallReject_if>>");
                        VideoCallStartActivity.activity.finish();
                    }
                    if (VideoCallActivity.activity != null) {
                        Log.d(">>>Raman>>>", ">>>>>>>>>>>>>>>>>>>>>>>>videoCallReject_if_>>");
                        VideoCallActivity.activity.finish();
                        VideoCallActivity.decline = true;
                        VideoCallActivity.testing = false;
                    }
                    NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notifManager.cancelAll();
                    OneSignal.cancelNotification(notification.androidNotificationId);
                } else if (type.equals("fitsMinder")) {
                    String id = notification.payload.additionalData.getString("fits_smile_id");
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(AppConstants.OPEN_FROM, 2);
                    intent.putExtra(AppConstants.APPOINTMENT_ID, Integer.parseInt(id));
                    context.startActivity(intent);
                } else if (type.equals("fitsMinderAlignerImages")) {
                    String id = notification.payload.additionalData.getString("fits_smile_id");
                    String alignerNo = notification.payload.additionalData.getString("aligner_no");
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(AppConstants.OPEN_FROM, 3);
                    intent.putExtra(AppConstants.APPOINTMENT_ID, Integer.parseInt(id));
                    intent.putExtra(AppConstants.ALIGNER_NO, alignerNo);
                    context.startActivity(intent);
                }
//                else if (type.equals("chat_notification")) {
////                            sendNotification(data.get("title"), data.get("body"), data.get("image"), data.get("senderName"), data.get("senderId"));
//                    Activity currentActivity = getCurrentActivity();
//                    boolean isOurAppInBackground = (currentActivity == null);
//                    boolean isChatPageOpened;
//                    boolean isOpponentChatOpened = false;
//                    if (currentActivity != null) {
//                        isChatPageOpened = currentActivity.getClass().getName().equals(ChatActivityFirebase.class.getName());
//                        if (isChatPageOpened) {
//                            isOpponentChatOpened = ((ChatActivityFirebase) currentActivity).getOpponentId().equals(data.get("senderId"));
//                        }
//                    }
//                    if (isOurAppInBackground || !isOpponentChatOpened) {
//                        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                        Intent intent = new Intent(context, ChatActivityFirebase.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.putExtra(OPPONENT_ID, data.optString(("senderId")));
//                        intent.putExtra(OPPONENT_NAME, data.optString(("senderName")));
//                        intent.putExtra(OPPONENT_PROFILE_PIC, data.optString(("image")));
//                        context.startActivity(intent);
//                    }
//                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Raman", "ntoficatation_catch");
            }
        }

    }

    private class ExampleNotificationOpenedHandlers implements OneSignal.NotificationOpenedHandler {

        private final Context context;

        public ExampleNotificationOpenedHandlers(Context context) {
            this.context = context;
        }

        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            try {

                String booking_id = data.optString("booking_id");
                String page = data.optString("page");
                /*if (data.get("type").equals("fitsMinder")){
                    String id=data.get("fits_smile_id").toString();
                    Intent intent=new Intent(context, ReminderDetailsActivity.class);
                    intent.putExtra("id",Integer.parseInt(id));
                    startActivity(intent);
                }else {*/

                if (actionType == OSNotificationAction.ActionType.Opened) {
                    String type = data.optString("type");
                    if (type.equals("videoCallRequest")) {
                        //   isVideoCallComing=true;
//                            Log.d(">>>Test>>>", ">>>>>>>>>>>>>>>>>>>>>>>>videoCallRequest  opened>>");
//                            Log.e("Raman....2","receive");
//                            Log.e("Raman....2","receive"+context);
//                            Intent intent = new Intent(context, MainActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            intent.putExtra(AppConstants.OPEN_FROM,4);
//                            startActivity(intent);
                        Log.e("Raman....3", "receive");
                        //  OneSignal.cancelNotification(notification.androidNotificationId);
                    }
                    else if (type.equals("chat_notification")) {
//                            sendNotification(data.get("title"), data.get("body"), data.get("image"), data.get("senderName"), data.get("senderId"));
                        Activity currentActivity = getCurrentActivity();
                        boolean isOurAppInBackground = (currentActivity == null);
                        boolean isChatPageOpened;
                        boolean isOpponentChatOpened = false;
                        if (currentActivity != null) {
                            isChatPageOpened = currentActivity.getClass().getName().equals(ChatActivityFirebase.class.getName());
                            if (isChatPageOpened) {
                                isOpponentChatOpened = ((ChatActivityFirebase) currentActivity).getOpponentId().equals(data.get("senderId"));
                            }
                        }
                        if (isOurAppInBackground || !isOpponentChatOpened) {
                            NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            Intent intent = new Intent(context, ChatActivityFirebase.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(OPPONENT_ID, data.optString(("senderId")));
                            intent.putExtra(OPPONENT_NAME, data.optString(("senderName")));
                            intent.putExtra(OPPONENT_PROFILE_PIC, data.optString(("image")));
                            context.startActivity(intent);
                        }
                    }
                    else {
                        isVideoCallComing = false;
                        if (page.equals("booking")) {
                            Intent bookingIntent = new Intent(context, MainActivity.class);
                            bookingIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(bookingIntent);
                        } else if (page.equals("fits_reminder")) {
                            Intent intent = new Intent(context, AppointmentListActivity.class);
                            intent.putExtra(AppConstants.OPEN_FROM, 2);
                            startActivity(intent);
                        }
                    }

                }
                // }

                NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                // notifManager.cancelAll();

            } catch (Exception e) {
            }
        }
    }

    public void stopListeningToAllMessages() {
        if (allChatsReferences.size() > 0) {
            for (int i = 0; i < allChatsReferences.size(); i++) {
                DatabaseReference reference = allChatsReferences.get(i);
                ChildEventListener listener = allChatsChangeListeners.get(i);
                reference.removeEventListener(listener);
                allChatsReferences.clear();
                allChatsChangeListeners.clear();
            }
        }

        if (deviceTokenChangeReference != null && deviceTokenChangeListener != null) {
            deviceTokenChangeReference.removeEventListener(deviceTokenChangeListener);
            deviceTokenChangeListener = null;
            deviceTokenChangeReference = null;
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleManager.setLocale(this);
    }

    public void stopListeningToSessionChanges(Activity activity) {
        log("Stopped listening to session changes on " + activity.getClass().getSimpleName());
        if (deviceTokenChangeReference != null && deviceTokenChangeListener != null) {
            deviceTokenChangeReference.removeEventListener(deviceTokenChangeListener);
            deviceTokenChangeListener = null;
            deviceTokenChangeReference = null;
        }
    }

    public void startListeningToSessionChanges(Activity activity) {
        log("Started listening to session changes on " + activity.getClass().getSimpleName());
        deviceTokenChangeReference = getDatabaseReference().child(DEVICE_TOKENS).child(appPreference.getFirebaseUID());
        deviceTokenChangeListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                deviceTokenModifiedOnFirebase(activity, dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                deviceTokenModifiedOnFirebase(activity, dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        if (!appPreference.getFirebaseUID().isEmpty()) {
            deviceTokenChangeReference.addChildEventListener(deviceTokenChangeListener);
        }
    }

    public void getUnreadMessagesCount(IGetUnreadMessagesCount getUnreadMessagesCount) {
        startFirebaseService();
        makeUserOnline();
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        Call<ChatListFirebaseResponse> mService = mApiService.getFirebaseChatList(jsonObj);
        mService.enqueue(new Callback<ChatListFirebaseResponse>() {
            @Override
            public void onResponse(Call<ChatListFirebaseResponse> call, retrofit2.Response<ChatListFirebaseResponse> response) {
                ChatListFirebaseResponse chatListResponse = response.body();
                if (chatListResponse != null && chatListResponse.getStatus().equals("1")) {
                    if (chatListResponse.getData() != null && chatListResponse.getData().size() > 0) {
                        for (int i = 0; i < chatListResponse.getData().size(); i++) {
                            ChatDataFirebase chatDataFirebase = chatListResponse.getData().get(i);
                            String roomName = getRoomName(appPreference, chatDataFirebase.getFirebase_uid());

                            DatabaseReference allChatReference = AppController.getDatabaseReference().child(MESSAGES).child(roomName);
                            allChatsReferences.add(allChatReference);
                            ChildEventListener allChatsChangeListener = new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    Log.d("Firebase", "Added " + s);
                                    if (!appPreference.getFirebaseUID().isEmpty()) {
                                        ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                                        if (message != null) {
                                            if (message.isOpponentMessage(appPreference)) {
                                                if (message.getIsRead() == 0) {
                                                    unreadMessagesCount++;
                                                } else {
                                                    if (unreadMessagesCount > 0) {
                                                        unreadMessagesCount--;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (getUnreadMessagesCount != null) {
                                        getUnreadMessagesCount.onUnreadMessageCountFetched(unreadMessagesCount);
                                    }
                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    Log.d("Firebase", "Changed " + s);
                                    if (!appPreference.getFirebaseUID().isEmpty()) {
                                        ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                                        if (message != null) {
                                            if (message.isOpponentMessage(appPreference)) {
                                                if (message.getIsRead() == 0) {
                                                    unreadMessagesCount++;
                                                } else {
                                                    if (unreadMessagesCount > 0) {
                                                        unreadMessagesCount--;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (getUnreadMessagesCount != null) {
                                        getUnreadMessagesCount.onUnreadMessageCountFetched(unreadMessagesCount);
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
                            allChatsChangeListeners.add(allChatsChangeListener);
                            if (!appPreference.getFirebaseUID().isEmpty()) {
                                allChatReference.addChildEventListener(allChatsChangeListener);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ChatListFirebaseResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void deviceTokenModifiedOnFirebase(Activity activity, DataSnapshot dataSnapshot) {
        String deviceTokenOnFirebase = (String) dataSnapshot.getValue();
        currentDeviceTokenOnFirebase = deviceTokenOnFirebase;
        String myDeviceToken = getCurrentFCMDeviceToken();
        log("Device token changed on firebase. New token - " + deviceTokenOnFirebase + "\n Your token - " + myDeviceToken);
        if (!appPreference.isLoggedIn()) {
            log("You are not logged in. No need to logout");
            return;
        }
        if (myDeviceToken.isEmpty()) {
            log("You device token is not yet available. No need to logout");
            return;
        }
        if (deviceTokenOnFirebase == null || deviceTokenOnFirebase.isEmpty()) {
            log("No device logged in for this user yet. No need to logout");
            return;
        }
        if (deviceTokenOnFirebase.equals(myDeviceToken)) {
            log("The device token on firebase is same as your device token. No need to logout");
            return;
        }
        log("You have logged in from different platform/device. Logging you out from this device");
        if(!activity.isFinishing()){
            showSessionAlert(activity);
        }

    }

    /*public void initialiseVirgil(@NotNull VirgilInitializeListener initializeListener) {
        if (senderDevice == null) {
            senderDevice = new VirgilDevice(this);
        }
        new InitializeVirgilAsyncTask(appPreference, senderDevice, initializeListener).execute();
    }

    private void unregisterUserVirgil(@NotNull VirgilResetUserListener virgilResetUserListener) {
        senderDevice.unregister().addCallback(new OnCompleteListener() {
            @Override
            public void onSuccess() {
                log("Unregistered this user from Virgil");
                virgilResetUserListener.onResetCompleted();
            }

            @Override
            public void onError(@NotNull Throwable throwable) {
                String errorMessage = "Unregister from Virgil failed " + throwable.getMessage();
                log(errorMessage);
                new Handler(Looper.getMainLooper()).post(virgilResetUserListener::onResetCompleted);
            }
        });
    }

    public void getMyPublicKey(GetVirgilPublicKeyListener virgilPublicKeyListener) {
        if (myPublicKey == null) {
            fetchMyPublicKey(virgilPublicKeyListener);
        } else {
            if (virgilPublicKeyListener != null) {
                new Handler(Looper.getMainLooper()).post(() -> virgilPublicKeyListener.onPublicKeyFound(myPublicKey));
            }
        }
    }

    public void verifyKeys(@NotNull GetVirgilPrivateKeyListener virgilPrivateKeyListener) {
        if (privateKeyPresentOnDevice()) {
            log("user has local private key :)");
            new Handler(Looper.getMainLooper()).post(virgilPrivateKeyListener::onPrivateKeyFound);
        } else {
            registerUser(new VirgilRegisterUserListener() {
                @Override
                public void onRegistered() {
                    backupPrivateKey(new VirgilBackupPrivateKeyListener() {
                        @Override
                        public void onBackupSuccess() {
                            new Handler(Looper.getMainLooper()).post(virgilPrivateKeyListener::onPrivateKeyFound);
                        }

                        @Override
                        public void onBackupAlreadyExists() {
                            new Handler(Looper.getMainLooper()).post(() -> virgilPrivateKeyListener.onError(getResources().getString(R.string.failed_private_key_backup_error) + " - " + getResources().getString(R.string.private_key_backup_already_exists)));
                        }

                        @Override
                        public void onPrivateKeyMissing() {
                            new Handler(Looper.getMainLooper()).post(() -> virgilPrivateKeyListener.onError(getResources().getString(R.string.failed_private_key_backup_error) + " - " + getResources().getString(R.string.private_key_not_found_on_device)));
                        }

                        @Override
                        public void onError(String message) {
                            new Handler(Looper.getMainLooper()).post(() -> virgilPrivateKeyListener.onError(getResources().getString(R.string.failed_private_key_backup_error) + " - " + message));
                        }
                    });
                }

                @Override
                public void onUserAlreadyRegistered() {
                    restorePrivateKey(new VirgilRestorePrivateKeyListener() {
                        @Override
                        public void onRestoreSuccess() {
                            new Handler(Looper.getMainLooper()).post(virgilPrivateKeyListener::onPrivateKeyFound);
                        }

                        @Override
                        public void onBackupNotPresentOnVirgil() {
                            resetUser(new VirgilResetUserListener() {
                                @Override
                                public void onResetCompleted() {
                                    new Handler(Looper.getMainLooper()).post(() -> verifyKeys(virgilPrivateKeyListener));
                                }

                                @Override
                                public void onError(String message) {
                                    new Handler(Looper.getMainLooper()).post(() -> virgilPrivateKeyListener.onError(getResources().getString(R.string.no_private_key_backup_virgil)));
                                }
                            });
                        }

                        @Override
                        public void onPrivateKeyExists() {
                            new Handler(Looper.getMainLooper()).post(virgilPrivateKeyListener::onPrivateKeyFound);
                        }

                        @Override
                        public void onWrongPassword() {
                            new Handler(Looper.getMainLooper()).post(() -> virgilPrivateKeyListener.onError(getResources().getString(R.string.incorrect_password_to_restore)));
                        }

                        @Override
                        public void onError(String message) {
                            new Handler(Looper.getMainLooper()).post(() -> virgilPrivateKeyListener.onError(message));
                        }
                    });
                }

                @Override
                public void onPrivateKeyExists() {
                    new Handler(Looper.getMainLooper()).post(virgilPrivateKeyListener::onPrivateKeyFound);
                }

                @Override
                public void onError(String message) {
                    new Handler(Looper.getMainLooper()).post(() -> virgilPrivateKeyListener.onError(message));
                }
            });
        }
    }

    public void resetUser(@NotNull VirgilResetUserListener virgilResetUserListener) {
        log("Resetting user");
        log("Resetting private key backup on Virgil cloud");
        senderDevice.resetPrivateKeyBackup().addCallback(new OnCompleteListener() {
            @Override
            public void onSuccess() {
                log("Reset private key from Virgil cloud successfully");
                unregisterUserVirgil(virgilResetUserListener);
            }

            @Override
            public void onError(@NotNull Throwable throwable) {
                String errorMessage = "Reset private key failed " + throwable.getMessage();
                log(errorMessage);
                unregisterUserVirgil(virgilResetUserListener);
            }
        });
    }

    private void backupPrivateKey(@NotNull VirgilBackupPrivateKeyListener virgilBackupPrivateKeyListener) {
        log("Backing up private key on Virgil cloud");
        senderDevice.backupPrivateKey(appPreference).addCallback(new OnCompleteListener() {
            @Override
            public void onSuccess() {
                log("Backed up private key successfully");
                virgilBackupPrivateKeyListener.onBackupSuccess();
            }

            @Override
            public void onError(@NotNull Throwable throwable) {
                if (throwable instanceof EThreeException) {
                    EThreeException backupException = ((EThreeException) throwable);
                    switch (backupException.getDescription()) {
                        case MISSING_PRIVATE_KEY:
                            log("Could not back up private key - Private Key missing");
                            virgilBackupPrivateKeyListener.onPrivateKeyMissing();
                            break;
                        case PRIVATE_KEY_BACKUP_EXISTS:
                            log("Could not back up private key - Backup already present on Virgil cloud");
                            virgilBackupPrivateKeyListener.onBackupAlreadyExists();
                            break;
                        default:
                            log("Could not back up private key - " + backupException.getMessage());
                            virgilBackupPrivateKeyListener.onError(throwable.getMessage());
                            break;
                    }
                }
            }
        });
    }

    private void restorePrivateKey(@NotNull VirgilRestorePrivateKeyListener virgilRestorePrivateKeyListener) {
        log("Restoring the private key from cloud");
        senderDevice.restorePrivateKey(appPreference).addCallback(new OnCompleteListener() {
            @Override
            public void onSuccess() {
                log("Restored private key successfully");
                virgilRestorePrivateKeyListener.onRestoreSuccess();
            }

            @Override
            public void onError(@NotNull Throwable throwable) {
                if (throwable instanceof EThreeException) {
                    EThreeException restoreException = ((EThreeException) throwable);
                    switch (restoreException.getDescription()) {
                        case NO_PRIVATE_KEY_BACKUP:
                            log("Could not restore as the private key backup not present for this user on Virgil cloud");
                            virgilRestorePrivateKeyListener.onBackupNotPresentOnVirgil();
                            break;
                        case WRONG_PASSWORD:
                            log("The password used to restore the private key backup is incorrect");
                            virgilRestorePrivateKeyListener.onWrongPassword();
                            break;
                        case PRIVATE_KEY_EXISTS:
                            log("Private Key already exists on device");
                            virgilRestorePrivateKeyListener.onPrivateKeyExists();
                            break;
                        default:
                            String errorMessage = "Could not restore private key - " + restoreException.getMessage();
                            log(errorMessage);
                            virgilRestorePrivateKeyListener.onError(errorMessage);
                            break;
                    }
                }
            }
        });
    }

    private boolean privateKeyPresentOnDevice() {
        return senderDevice.hasLocalPrivateKey();
    }

    private void registerUser(@NotNull VirgilRegisterUserListener virgilRegisterUserListener) {
        log("Registering user on virgil");
        senderDevice.register().addCallback(new OnCompleteListener() {
            @Override
            public void onSuccess() {
                log("Registered user successfully");
                virgilRegisterUserListener.onRegistered();
            }

            @Override
            public void onError(@NotNull Throwable throwable) {
                if (throwable instanceof EThreeException) {
                    EThreeException registerException = ((EThreeException) throwable);
                    switch (registerException.getDescription()) {
                        case PRIVATE_KEY_EXISTS:
                            log("Private key already exists on the device");
                            virgilRegisterUserListener.onPrivateKeyExists();
                            break;
                        case USER_IS_ALREADY_REGISTERED:
                            log("This user is already registered on virgil");
                            virgilRegisterUserListener.onUserAlreadyRegistered();
                            break;
                        default:
                            String errorMessage = "Could not register user - " + registerException.getMessage();
                            log(errorMessage);
                            virgilRegisterUserListener.onError(errorMessage);
                            break;
                    }
                }
            }
        });
    }

    public void clearOutVirgil() {
        if (senderDevice != null) {
            senderDevice.deInitialiseVirgil();
        }
        senderDevice = null;
    }

    public void initVirgilAfterLogin(@NotNull VirgilInitializeListener virgilInitializeListener) {
        if (appPreference.isLoggedIn()) {
            initialiseVirgil(new VirgilInitializeListener() {
                @Override
                public void onInitialized() {
                    verifyKeys(new GetVirgilPrivateKeyListener() {
                        @Override
                        public void onPrivateKeyFound() {
                            log("KEYS VERIFIED SUCCESSFULLY");
                            if (user == null) {
                                String mCustomToken = appPreference.getFirebaseCustomToken();
                                log("Authenticating user on firebase using custom_firebase_token provided by API in login - " + mCustomToken);
                                if (!mCustomToken.isEmpty()) {
                                    log("Firebase Auth details - " + "App id -" + mAuth.getApp().getOptions().getApplicationId() + "\nDB URL - " + mAuth.getApp().getOptions().getDatabaseUrl() + "\nAPI Key - " + mAuth.getApp().getOptions().getApiKey() + "\nStorage Bucket - " + mAuth.getApp().getOptions().getStorageBucket());
                                    mAuth.signInWithCustomToken(mCustomToken)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    // Sign in success, update UI with the signed-in user's information
                                                    log("Logged in on firebase successfully");
                                                    user = mAuth.getCurrentUser();
                                                    startFirebaseService();
                                                    makeUserOnline();
                                                    virgilInitializeListener.onInitialized();
                                                } else {
                                                    // If sign in fails, display a message to the user.
                                                    String error = "Firebase authentication : " + task.getException().getMessage();
                                                    log(error);
                                                    virgilInitializeListener.onError(error);
                                                }
                                            });
                                } else {
                                    user = null;
                                    String error = "Firebase authentication : No authentication data found";
                                    log(error);
                                    virgilInitializeListener.onError(error);
                                }
                            } else {
                                startFirebaseService();
                                makeUserOnline();
                                virgilInitializeListener.onInitialized();
                            }
                        }

                        @Override
                        public void onError(String error) {
                            log("COULD NOT VERIFY KEYS");
                            virgilInitializeListener.onError(error);
                        }
                    });
                }

                @Override
                public void onError(String message) {
                    virgilInitializeListener.onError(message);
                }
            });
        } else {
            virgilInitializeListener.onError("You are not logged in");
        }
    }

    public void fetchMyPublicKey(GetVirgilPublicKeyListener virgilPublicKeyListener) {
        getPublicKey(appPreference.getFirebaseUID(), new GetVirgilPublicKeyListener() {
            @Override
            public void onPublicKeyFound(Card card) {
                myPublicKey = card;
                if (virgilPublicKeyListener != null) {
                    new Handler(Looper.getMainLooper()).post(() -> virgilPublicKeyListener.onPublicKeyFound(card));
                }
            }

            @Override
            public void onError(String error) {
                if (virgilPublicKeyListener != null) {
                    new Handler(Looper.getMainLooper()).post(() -> virgilPublicKeyListener.onError(error));
                }
            }
        });
    }

    public void getPublicKey(String firebaseUID, GetVirgilPublicKeyListener virgilPublicKeyListener) {
        if (publicKeys == null) {
            publicKeys = new HashMap<>();
        }
        if (firebaseUID != null && !firebaseUID.isEmpty()) {
            if (!publicKeys.containsKey(firebaseUID)) {
                senderDevice.findUser(firebaseUID).addCallback(new OnResultListener<Card>() {
                    @Override
                    public void onSuccess(Card card) {
                        log("Looked up " + firebaseUID + "'s public key");
                        if (virgilPublicKeyListener != null) {
                            if (card != null) {
                                new Handler(Looper.getMainLooper()).post(() -> virgilPublicKeyListener.onPublicKeyFound(card));
                                publicKeys.put(firebaseUID, card);
                            } else {
                                new Handler(Looper.getMainLooper()).post(() -> virgilPublicKeyListener.onError("Public key not found for this user. Ask them to login again on their device."));
                            }
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable throwable) {
                        log("Failure looking up public key :" + throwable.getMessage());
                        String errorMessage = "";
                        if (throwable instanceof FindUsersException) {
                            FindUsersException exception = (FindUsersException) throwable;
                            switch (exception.getDescription()) {
                                case DUPLICATE_CARDS:
                                    errorMessage = getResources().getString(R.string.one_or_more_signatures_opponent);
                                    break;
                                case CARD_WAS_NOT_FOUND:
                                    errorMessage = getResources().getString(R.string.opponent_not_registered_on_virgil);
                                    break;
                                default:
                                    errorMessage = exception.getMessage();
                                    break;
                            }
                        }
                        String finalErrorMessage = errorMessage;
                        new Handler(Looper.getMainLooper()).post(() -> virgilPublicKeyListener.onError(finalErrorMessage));
                    }
                });
            } else {
                new Handler(Looper.getMainLooper()).post(() -> virgilPublicKeyListener.onPublicKeyFound(publicKeys.get(firebaseUID)));
            }
        }
    }

    public void cleanUpVirgil(VirgilCleanupListener cleanupListener) {
        new CleanUpVirgilAsyncTask(senderDevice, cleanupListener).execute();
    }

    public void encryptMessage(String message, String opponentId, EncryptionListener encryptionListener) {
        senderDevice.findUser(opponentId).addCallback(new OnResultListener<Card>() {
            @Override
            public void onSuccess(Card card) {
                try {
                    String encryptedMessage = senderDevice.encrypt(message, card);
                    if (encryptionListener != null) {
                        encryptionListener.onEncryptionComplete(encryptedMessage);
                    }
                } catch (Throwable throwable) {
                    if (throwable instanceof EThreeException) {
                        EThreeException exception = (EThreeException) throwable;
                        switch (exception.getDescription()) {
                            case MISSING_PRIVATE_KEY:
                                if (encryptionListener != null) {
                                    encryptionListener.onError(getResources().getString(R.string.private_key_not_found_on_device));
                                }
                                break;
                            case MISSING_PUBLIC_KEY:
                                if (encryptionListener != null) {
                                    encryptionListener.onError(getResources().getString(R.string.public_key_not_found_for_opponent));
                                }
                                break;
                            case STR_TO_DATA_FAILED:
                                if (encryptionListener != null) {
                                    encryptionListener.onError(getResources().getString(R.string.unable_to_decrypt));
                                }
                                break;
                        }
                    } else {
                        if (encryptionListener != null) {
                            encryptionListener.onError(getResources().getString(R.string.unable_to_decrypt));
                        }
                    }
                }
            }

            @Override
            public void onError(@NotNull Throwable throwable) {
                if (encryptionListener != null) {
                    encryptionListener.onError(throwable.getMessage());
                }
            }
        });
    }

    public String decryptMessage(String message, Card senderCard) {
        if (message == null || message.trim().isEmpty()) {
            return "";
        }
        try {
            return senderDevice.decrypt(message, senderCard);
        } catch (Throwable throwable) {
            return getResources().getString(R.string.unable_to_decrypt);
        }
    }*/

    public void initAfterLogin(@NotNull InitializeListener initializeListener) {
        if (appPreference.isLoggedIn()) {
            if (user == null) {
                String mCustomToken = appPreference.getFirebaseCustomToken();
                log("Authenticating user on firebase using custom_firebase_token provided by API in login - " + mCustomToken);
                if (!mCustomToken.isEmpty()) {
                    log("Firebase Auth details - " + "App id -" + mAuth.getApp().getOptions().getApplicationId() + "\nDB URL - " + mAuth.getApp().getOptions().getDatabaseUrl() + "\nAPI Key - " + mAuth.getApp().getOptions().getApiKey() + "\nStorage Bucket - " + mAuth.getApp().getOptions().getStorageBucket());
                    mAuth.signInWithCustomToken(mCustomToken)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    log("Logged in on firebase successfully");
                                    user = mAuth.getCurrentUser();
                                    startFirebaseService();
                                    makeUserOnline();
                                    initializeListener.onInitialized();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    String error = "Firebase authentication : " + task.getException().getMessage();
                                    log(error);
                                    initializeListener.onError(error);
                                }
                            });
                } else {
                    user = null;
                    String error = "Firebase authentication : No authentication data found";
                    log(error);
                    initializeListener.onError(error);
                }
            } else {
                startFirebaseService();
                makeUserOnline();
                initializeListener.onInitialized();
            }
        } else {
            initializeListener.onError("You are not logged in");
        }
    }

    public void startFirebaseService() {
        try {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        FirebaseUtils.sendToken(appPreference, token);
                        startService(firebaseService);
                    });
        } catch (Exception e) {

        }

    }

    public void stopFirebaseService() {
        stopService(firebaseService);
    }

    private void log(String e) {
        Log.d("Virgil", e);
    }


}
