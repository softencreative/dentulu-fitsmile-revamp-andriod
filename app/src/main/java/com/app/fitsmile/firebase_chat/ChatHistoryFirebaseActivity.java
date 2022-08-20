package com.app.fitsmile.firebase_chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppController;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.utils.LocaleManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.fitsmile.BuildConfig.BASE_IMAGEURL;
import static com.app.fitsmile.common.Utils.getChatIntent;
import static com.app.fitsmile.common.Utils.isOnline;
import static com.app.fitsmile.common.Utils.showToast;
import static com.app.fitsmile.firebase_chat.ChatConstant.MESSAGES;
import static com.app.fitsmile.firebase_chat.ChatConstant.STATUS;
import static com.app.fitsmile.firebase_chat.FirebaseUtils.getRoomName;

public class ChatHistoryFirebaseActivity extends BaseActivity implements ChatHistoryFirebaseAdapter.OnItemClicked {
    ChatHistoryFirebaseAdapter chatListAdapter;
    RecyclerView recyclerView;
    BaseActivity activity;
    ShimmerFrameLayout shimmerLayout;
    TextView nodata;
    private ArrayList<DatabaseReference> allChatsReferences = new ArrayList<>();
    private ArrayList<DatabaseReference> allUsersOnlineStatusReferences = new ArrayList<>();
    private ArrayList<ChildEventListener> allChatsChangeListeners = new ArrayList<>();
    private ArrayList<ChildEventListener> allUsersOnlineStatusChangeListeners = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);
        LocaleManager.setLocale(this);
        activity = this;
        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadAllChatHistory();
    }

    private void loadAllChatHistory() {
        nodata.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmerAnimation();

        if (isOnline(activity)) {
            getChatList(/*card*/);
        } else {
            shimmerLayout.setVisibility(View.GONE);
            shimmerLayout.stopShimmerAnimation();
            showToast(activity, getResources().getString(R.string.please_check_your_network));
        }

        /*((AppController) getApplication()).initialiseVirgil(new VirgilInitializeListener() {
            @Override
            public void onInitialized() {
                ((AppController) getApplication()).getMyPublicKey(new GetVirgilPublicKeyListener() {
                    @Override
                    public void onPublicKeyFound(Card card) {

                    }

                    @Override
                    public void onError(String error) {
                        shimmerLayout.setVisibility(View.GONE);
                        shimmerLayout.stopShimmerAnimation();
                        showToast(activity, error);
                    }
                });
            }

            @Override
            public void onError(String message) {
                shimmerLayout.setVisibility(View.GONE);
                shimmerLayout.stopShimmerAnimation();
                showToast(activity, message);
            }
        });*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopListeningToAllMessages();
        clearChatListAdapter();
    }

    private void clearChatListAdapter() {
        if (chatListAdapter != null) {
            chatListAdapter.clearAll();
        }
    }

    private void initUI() {
        shimmerLayout = findViewById(R.id.shimmer_layout);
        shimmerLayout.startShimmerAnimation();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(R.string.chat_history);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));

        recyclerView = findViewById(R.id.recyclerList);

        nodata = findViewById(R.id.nodata);
    }

    void getChatList(/*Card myPublicKey*/) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<ChatListFirebaseResponse> mService = mApiService.getFirebaseChatList(jsonObj);
        mService.enqueue(new Callback<ChatListFirebaseResponse>() {
            @Override
            public void onResponse(Call<ChatListFirebaseResponse> call, Response<ChatListFirebaseResponse> response) {
                shimmerLayout.setVisibility(View.GONE);
                shimmerLayout.stopShimmerAnimation();
                ChatListFirebaseResponse chatListResponse = response.body();
                if (chatListResponse == null) {
                    nodata.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    showToast(ChatHistoryFirebaseActivity.this, "No response from server");
                    return;
                }
                if (chatListResponse.getStatus().equals("1")) {
                    if (chatListResponse.getData() == null || chatListResponse.getData().size() == 0) {
                        nodata.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        getFilteredList(chatListResponse.getData(), filteredData -> {
                            if (filteredData.size() > 0) {
                                nodata.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);

                                LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
                                chatListAdapter = new ChatHistoryFirebaseAdapter(activity, filteredData/*, myPublicKey*/);
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setAdapter(chatListAdapter);
                                chatListAdapter.setOnClick(ChatHistoryFirebaseActivity.this);
                                stopListeningToAllMessages();
                                for (int i = 0; i < filteredData.size(); i++) {
                                    ChatDataFirebase chatDataFirebase = filteredData.get(i);
                                    final int[] unreadCount = {0};
                                    String opponentId = chatDataFirebase.getFirebase_uid();
                                    String roomName = getRoomName(appPreference, opponentId);
                                    DatabaseReference userOnlineStatusReference = AppController.getDatabaseReference().child(STATUS).child(opponentId);
                                    allUsersOnlineStatusReferences.add(userOnlineStatusReference);

                                    DatabaseReference allChatReference = AppController.getDatabaseReference().child(MESSAGES).child(roomName);
                                    allChatsReferences.add(allChatReference);
                                    int finalPosition = i;
                                    ChildEventListener userOnlineStatusChangeListener = new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                            Log.d("Firebase", "Added " + s);
                                            if (!appPreference.getFirebaseUID().isEmpty()) {
                                                if (dataSnapshot.getKey().equals("state")) {
                                                    String onlineStatus = (String) dataSnapshot.getValue();
                                                    chatListAdapter.setOnlineStatus(opponentId, onlineStatus, finalPosition);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                            Log.d("Firebase", "Changed " + s);
                                            if (!appPreference.getFirebaseUID().isEmpty()) {
                                                if (dataSnapshot.getKey().equals("state")) {
                                                    String onlineStatus = (String) dataSnapshot.getValue();
                                                    chatListAdapter.setOnlineStatus(opponentId, onlineStatus, finalPosition);
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

                                    ChildEventListener allChatsChangeListener = new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                            Log.d("Firebase", "Added " + s);
                                            if (!appPreference.getFirebaseUID().isEmpty()) {
                                                ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                                                if (message != null) {
                                                    if (message.isOpponentMessage(appPreference)) {
                                                        if (message.getIsRead() == 0) {
                                                            unreadCount[0]++;
                                                        } else {
                                                            if (unreadCount[0] > 0) {
                                                                unreadCount[0]--;
                                                            }
                                                        }
                                                    }
                                                    chatListAdapter.showUnreadCount(message.getSenderId(), unreadCount[0]);
                                                    chatListAdapter.showLastMessage(opponentId, message);
                                                }
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
                                                            unreadCount[0]++;
                                                        } else {
                                                            if (unreadCount[0] > 0) {
                                                                unreadCount[0]--;
                                                            }
                                                        }
                                                    }
                                                    chatListAdapter.showUnreadCount(message.getSenderId(), unreadCount[0]);
                                                    chatListAdapter.showLastMessage(opponentId, message);
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
                                    allChatsChangeListeners.add(allChatsChangeListener);
                                    allUsersOnlineStatusChangeListeners.add(userOnlineStatusChangeListener);
                                    if (!appPreference.getFirebaseUID().isEmpty()) {
                                        userOnlineStatusReference.addChildEventListener(userOnlineStatusChangeListener);
                                        allChatReference.addChildEventListener(allChatsChangeListener);
                                    }
                                }
                            } else {
//                                if (chatListResponse.getData() != null || chatListResponse.getData().size() > 0) {
//                                    nodata.setVisibility(View.GONE);
//                                    recyclerView.setVisibility(View.VISIBLE);
//                                    LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
//                                    chatListAdapter = new ChatHistoryFirebaseAdapter(activity, chatListResponse.getData());
//                                    recyclerView.setLayoutManager(layoutManager);
//                                    recyclerView.setAdapter(chatListAdapter);
//                                    chatListAdapter.setOnClick(ChatHistoryFirebaseActivity.this);
//                                } else {
                                    nodata.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                               // }
                            }
                        });
                    }
                } else if (chatListResponse.getStatus().equals("401")) {
                    nodata.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    Utils.showSessionAlert(activity);
                } else {
                    nodata.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    //showToast(ChatHistoryFirebaseActivity.this, chatListResponse.getMessage());
                    showToast(ChatHistoryFirebaseActivity.this, getResources().getString(R.string.not_data_found));
                }
            }

            @Override
            public void onFailure(Call<ChatListFirebaseResponse> call, Throwable t) {
                shimmerLayout.setVisibility(View.GONE);
                shimmerLayout.stopShimmerAnimation();
                nodata.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                call.cancel();
            }
        });
    }

    private void getFilteredList(List<ChatDataFirebase> data, @NotNull GetFilteredUsersListener getFilteredUsersListener) {
        DatabaseReference allMessagesReference = AppController.getDatabaseReference().child(MESSAGES);
        allMessagesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<ChatDataFirebase> filteredList = new ArrayList<>();
                for (ChatDataFirebase chatDataFirebase : data) {
                    String opponentId = chatDataFirebase.getFirebase_uid();
                    if (!getRoomName(appPreference, opponentId).isEmpty()){
                        filteredList.add(chatDataFirebase);
                    }
//                    if (snapshot.hasChild(getRoomName(appPreference, opponentId))) {
//                        filteredList.add(chatDataFirebase);
//                    }
                }
                getFilteredUsersListener.onFiltered(filteredList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void stopListeningToAllMessages() {
        if (allChatsReferences.size() > 0) {
            for (int i = 0; i < allChatsReferences.size(); i++) {
                DatabaseReference reference = allChatsReferences.get(i);
                ChildEventListener listener = allChatsChangeListeners.get(i);
                reference.removeEventListener(listener);
                allChatsReferences.clear();
                allChatsChangeListeners.clear();
            }
        }
        if (allUsersOnlineStatusReferences.size() > 0) {
            for (int i = 0; i < allUsersOnlineStatusReferences.size(); i++) {
                DatabaseReference reference = allUsersOnlineStatusReferences.get(i);
                ChildEventListener listener = allUsersOnlineStatusChangeListeners.get(i);
                reference.removeEventListener(listener);
                allUsersOnlineStatusReferences.clear();
                allUsersOnlineStatusChangeListeners.clear();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onItemClick(int position, String firebaseUId, String name, String image) {
        Intent intent = getChatIntent(activity, firebaseUId, name, image);
        startActivity(intent);
    }
}