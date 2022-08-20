package com.app.fitsmile.firebase_chat;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.base.BaseActivity;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.app.fitsmile.BuildConfig.BASE_IMAGEURL;
import static com.app.fitsmile.common.Utils.convertTimestampToDateInList;
import static com.app.fitsmile.firebase_chat.ChatConstant.CONTENT_TYPE_FILE;
import static com.app.fitsmile.firebase_chat.ChatConstant.CONTENT_TYPE_IMAGE;
import static com.app.fitsmile.firebase_chat.ChatConstant.CONTENT_TYPE_TEXT;
import static com.app.fitsmile.firebase_chat.ChatConstant.CONTENT_TYPE_VIDEO;
import static com.app.fitsmile.firebase_chat.ChatConstant.ONLINE;

public class ChatHistoryFirebaseAdapter extends RecyclerView.Adapter<ChatHistoryFirebaseAdapter.MyViewHolder> {

    BaseActivity activity;
    //    private Card myPublicKey;
    private final List<ChatDataFirebase> arrayListitem;
    private OnItemClicked onClick;
    private final Map<String, Integer> newMessages = new HashMap<>();
    private final Map<String, ChatMessage> lastMessages = new HashMap<>();
    private final Map<String, Boolean> onlineStatuses = new HashMap<>();

    public ChatHistoryFirebaseAdapter(BaseActivity activity, List<ChatDataFirebase> arrayListitem/*, Card myPublicKey*/) {
        this.arrayListitem = arrayListitem;
        this.activity = activity;
//        this.myPublicKey = myPublicKey;
    }

    public void showUnreadCount(String senderId, int unreadCount) {
        newMessages.put(senderId, unreadCount);
        notifyDataSetChanged();
    }

    public void setOnlineStatus(String opponentId, String status, int position) {
        onlineStatuses.put(opponentId, status.equals(ONLINE));
        notifyItemChanged(position);
    }

    public void showLastMessage(String opponentId, ChatMessage message) {
        lastMessages.put(opponentId, message);
        for (int i = 0; i < arrayListitem.size(); i++) {
            if (arrayListitem.get(i).getFirebase_uid().equals(opponentId)) {
                arrayListitem.get(i).lastMessageTime = message.getTimestamp();
            }
        }
        sortListBasedOnLastMessage();
        notifyDataSetChanged();
    }

    private void sortListBasedOnLastMessage() {
        Collections.sort(arrayListitem, (obj1, obj2) -> Long.compare(obj2.lastMessageTime, obj1.lastMessageTime));
    }

    public void clearAll() {
        newMessages.clear();
        lastMessages.clear();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_history, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        ChatDataFirebase chatDataFirebase = arrayListitem.get(position);
//        ChatMessage lastMessage = arrayListitem.get(position).getLatest_chat();
        if (chatDataFirebase.getFirst_name().equalsIgnoreCase("")) {
            holder.name.setText(R.string.dentulu_user);
        } else {
            holder.name.setText(chatDataFirebase.getFirst_name() + " " + chatDataFirebase.getLast_name());
        }

        if (onlineStatuses.containsKey(chatDataFirebase.getFirebase_uid())) {
            boolean isOnline = onlineStatuses.get(chatDataFirebase.getFirebase_uid());
            if (isOnline) {
                Drawable dr = activity.getResources().getDrawable(R.drawable.online_dot);
                Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
                Drawable d = new BitmapDrawable(activity.getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
                holder.name.setCompoundDrawablesWithIntrinsicBounds(
                        d, null, null, null);
            } else {
                holder.name.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, null, null);
            }
        } else {
            holder.name.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, null, null);
        }

        boolean isLastMessageRead = true;
        if (newMessages.containsKey(chatDataFirebase.getFirebase_uid())) {
            int count = newMessages.get(chatDataFirebase.getFirebase_uid());
            if (count > 0) {
                holder.countTV.setVisibility(View.VISIBLE);
                holder.countTV.setText(String.valueOf(count));
                isLastMessageRead = false;
            } else {
                holder.countTV.setVisibility(View.GONE);
            }
        } else {
            holder.countTV.setVisibility(View.GONE);
        }

        if (lastMessages.containsKey(chatDataFirebase.getFirebase_uid())) {
            ChatMessage lastMessage = lastMessages.get(chatDataFirebase.getFirebase_uid());
            if (lastMessage != null) {
                if (lastMessage.getContentType() != null) {
                    switch (lastMessage.getContentType()) {
                        case CONTENT_TYPE_TEXT:
                            if (lastMessage.isMyMessage(activity.appPreference)) {
                                holder.description.setText(lastMessage.getMessage()/*((AppController) activity.getApplication()).decryptMessage(lastMessage.getMessage(), myPublicKey)*/);
                            } else {
                                holder.description.setText(lastMessage.getMessage()/*((AppController) activity.getApplication()).decryptMessage(lastMessage.getMessage(), opponentPublicKey)*/);
                            }
                            break;
                        case CONTENT_TYPE_IMAGE:
                            if (lastMessage.isMyMessage(activity.appPreference)) {
                                holder.description.setText(R.string.you_sent_photo);
                            } else {
                                holder.description.setText(R.string.sent_photo);
                            }
                            break;
                        case CONTENT_TYPE_VIDEO:
                            if (lastMessage.isMyMessage(activity.appPreference)) {
                                holder.description.setText(R.string.you_sent_video);
                            } else {
                                holder.description.setText(R.string.sent_video);
                            }
                            break;
                        case CONTENT_TYPE_FILE:
                            if (lastMessage.isMyMessage(activity.appPreference)) {
                                holder.description.setText(R.string.you_sent_file);
                            } else {
                                holder.description.setText(R.string.sent_file);
                            }
                            break;
                    }
                } else {
                    holder.description.setVisibility(View.GONE);
                    holder.time.setVisibility(View.GONE);
                }
                if (lastMessage.isMyMessage(activity.appPreference)) {
                    int drawableRes;
                    if (lastMessage.getIsRead() == 1) {
                        drawableRes = R.drawable.double_tick_read;
                    } else {
                        drawableRes = R.drawable.double_tick_unread;
                    }
                    Drawable dr = activity.getResources().getDrawable(drawableRes);
                    Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
                    Drawable d = new BitmapDrawable(activity.getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
                    holder.description.setCompoundDrawablesWithIntrinsicBounds(
                            d, null, null, null);
                } else {
                    holder.description.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, null, null);
                }
                /*((AppController) activity.getApplication()).getPublicKey(chatDataFirebase.getFirebase_uid(), new GetVirgilPublicKeyListener() {

                    @Override
                    public void onPublicKeyFound(Card opponentPublicKey) {

                    }

                    @Override
                    public void onError(String error) {
                        holder.description.setVisibility(View.GONE);
                        holder.time.setVisibility(View.GONE);
                    }
                });*/
                String formattedDate = convertTimestampToDateInList(lastMessage.getTimestamp());
                holder.time.setText(formattedDate);
                if (isLastMessageRead) {
                    holder.description.setTypeface(null, Typeface.NORMAL);
                } else {
                    holder.description.setTypeface(null, Typeface.BOLD);
                }
                holder.description.setVisibility(View.VISIBLE);
                holder.time.setVisibility(View.VISIBLE);
            } else {
                holder.description.setVisibility(View.GONE);
                holder.time.setVisibility(View.GONE);
            }
        }

        if(!chatDataFirebase.getImage_url().isEmpty()){
            Picasso.get().load(chatDataFirebase.getImage_url()).placeholder(R.drawable.man_checked).into(holder.image);
        }
        holder.itemView.setOnClickListener(v -> onClick.onItemClick(position, chatDataFirebase.getFirebase_uid(), chatDataFirebase.getFirst_name() + " " + chatDataFirebase.getLast_name(), chatDataFirebase.getImage_url()));
    }

    @Override
    public int getItemCount() {
        return arrayListitem.size();
    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }

    public interface OnItemClicked {
        void onItemClick(int position, String firebaseUId, String name, String image);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, description, time, countTV;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.title);
            countTV = view.findViewById(R.id.count);
            time = view.findViewById(R.id.time);
            description = view.findViewById(R.id.description);
            image = view.findViewById(R.id.image);
        }
    }

}
