package com.app.fitsmile.firebase_chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.base.BaseActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.app.fitsmile.common.Utils.convertTimestampToDate;
import static com.app.fitsmile.common.Utils.showToast;
import static com.app.fitsmile.firebase_chat.ChatConstant.CONTENT_TYPE_FILE;
import static com.app.fitsmile.firebase_chat.ChatConstant.CONTENT_TYPE_IMAGE;
import static com.app.fitsmile.firebase_chat.ChatConstant.CONTENT_TYPE_TEXT;
import static com.app.fitsmile.firebase_chat.ChatConstant.CONTENT_TYPE_VIDEO;

public class ChatMessageFirebaseAdapter extends RecyclerView.Adapter<ChatMessageFirebaseAdapter.MyViewHolder> {

    private BaseActivity activity;
    private List<ChatMessage> chatMessages;
    private String myUserId;
    private String myPic;
    private String otherPicSuffix;
//    private Card myPublicKey, opponentPublicKey;
    private int VIEW_TYPE_MINE = 0;
    private int VIEW_TYPE_OTHERS = 1;

    public ChatMessageFirebaseAdapter(BaseActivity activity, @androidx.annotation.NonNull List<ChatMessage> chatMessages, String otherPicSuffix/*, Card myPublicKey, Card opponentPublicKey*/) {
        this.chatMessages = chatMessages;
        this.activity = activity;
        this.myPic = activity.appPreference.getImage();
        this.otherPicSuffix = otherPicSuffix;
//        this.myPublicKey = myPublicKey;
//        this.opponentPublicKey = opponentPublicKey;
        this.myUserId = activity.appPreference.getFirebaseUID();
    }

    @androidx.annotation.NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        int layoutRes;
        layoutRes = viewType == VIEW_TYPE_MINE ? R.layout.item_chat_right_firebase : R.layout.item_chat_left_firebase;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull final MyViewHolder holder, final int position) {
        ChatMessage message = chatMessages.get(position);
        String contentType = message.getContentType();
        if (contentType != null) {
            switch (contentType) {
                case CONTENT_TYPE_TEXT:
                    showText(message, holder);
                    break;
                case CONTENT_TYPE_IMAGE:
                    showImage(message, holder);
                    break;
                case CONTENT_TYPE_VIDEO:
                    showVideo(message, holder);
                    break;
                case CONTENT_TYPE_FILE:
                    showFile(message, holder);
                    break;
            }
        }

        if (message.isMyMessage(activity.appPreference)) {
            int drawableRes;
            if (message.getIsRead() == 1) {
                drawableRes = R.drawable.double_tick_read;
            } else {
                drawableRes = R.drawable.double_tick_unread;
            }
            Drawable dr = activity.getResources().getDrawable(drawableRes);
            Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
            Drawable d = new BitmapDrawable(activity.getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
            holder.date.setCompoundDrawablesWithIntrinsicBounds(
                    d, null, null, null);
        } else {
            holder.date.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, null, null);
        }

        String formattedDate;
        if (message.isLegacyChatMessage) {
            formattedDate = convertTimestampToDate(message.getTimestamp() * 1000);
        } else {
            formattedDate = convertTimestampToDate(message.getTimestamp());
        }
        holder.date.setText(formattedDate);

        holder.profilePic.setOnClickListener(v -> {
            /*if (message.isMyMessage(activity.appPreference)) {
                if (SessionManager.getInstance(activity).getAccess_Lavel().equals(ACCESS_LEVEL_HYGIENIST)) {
                    Intent i = new Intent(activity, HygienistMyAccountActivity.class);
                    i.putExtra("type", "H");
                    activity.startActivity(i);
                } else if (SessionManager.getInstance(activity).getAccess_Lavel().equals(ACCESS_LEVEL_DENTIST)) {
                    Intent intent = new Intent(activity, DentistAccountActivity.class);
                    intent.putExtra("type", "D");
                    activity.startActivity(intent);
                } else {
                    Intent intent = new Intent(activity, OfficeProfileActivity.class);
                    activity.startActivity(intent);
                }
            } else {
                String id = message.getSenderId();
                Intent i = new Intent(activity, PatientInfoActivity.class);
                i.putExtra("patient_id", id);
                i.putExtra("patient_sub_id", "");
                i.putExtra("patient_name", "");
                activity.startActivity(i);
            }*/
        });

        String url = "";
        if (getItemViewType(position) == VIEW_TYPE_MINE) {
            url = myPic;
        } else {
            url = otherPicSuffix;
        }

        if (url != null && !url.isEmpty()) {
            Picasso.get().load(url).placeholder(R.drawable.man_checked).into(holder.profilePic);
        } else {
            holder.profilePic.setImageResource(R.drawable.man_checked);
        }
    }

    /*private Card getRequiredKeyToDecryptMessage(ChatMessage message) {
        if (message.isMyMessage(activity.appPreference)) {
            return myPublicKey;
        } else {
            return opponentPublicKey;
        }
    }*/

    private void showText(ChatMessage message, MyViewHolder holder) {
        holder.contentLayout.setVisibility(VISIBLE);
        if (message.isLegacyChatMessage) {
            holder.name.setText(message.getMessage());
        } else {
            holder.name.setText(message.getMessage()/*((AppController) activity.getApplication()).decryptMessage(message.getMessage(), getRequiredKeyToDecryptMessage(message))*/);
        }
        holder.name.setVisibility(VISIBLE);
        holder.imageMessageIV.setVisibility(View.GONE);
        holder.playVideoIV.setVisibility(GONE);
        holder.mediaLayout.setVisibility(GONE);
        holder.docLayout.setVisibility(GONE);
    }

    private void showImage(final ChatMessage message, final MyViewHolder holder) {
        holder.contentLayout.setVisibility(VISIBLE);
        holder.mediaLayout.setVisibility(VISIBLE);
        holder.imageMessageIV.setVisibility(VISIBLE);
        holder.name.setVisibility(GONE);
        holder.playVideoIV.setVisibility(GONE);
        holder.docLayout.setVisibility(GONE);
        if (message.getMessage() != null && !message.getMessage().isEmpty()) {
            String url = null;
            if (message.isLegacyChatMessage) {
                holder.name.setText(message.getMessage());
                url = message.getMessage();
            } else {
                new AWSUtils().getMediaUrlFromServer(activity, message.getMessage(), new FirebaseGetFilesUrlListener() {
                    @Override
                    public void onGetUrl(String url) {
                        Log.d("Virgil", "Image URL - " + url);
                        Glide.with(activity).load(url).placeholder(R.drawable.gallery_thumb).into(holder.imageMessageIV);
                        holder.imageMessageIV.setOnClickListener(v -> openImage(url));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.d("Virgil", "Image URL - Could not load url, Error is " + errorMessage);
                        Glide.with(activity).load("").placeholder(R.drawable.gallery_thumb).into(holder.imageMessageIV);
                        holder.imageMessageIV.setOnClickListener(v -> openImage(""));
                    }
                });
                /*String decryptedMessage = ((AppController) activity.getApplication()).decryptMessage(message.getMessage(), getRequiredKeyToDecryptMessage(message));
                if (decryptedMessage != null && !decryptedMessage.isEmpty() && !decryptedMessage.equals(activity.getResources().getString(R.string.unable_to_decrypt))) {

                } else {
                    showText(message, holder);
                }*/
            }
            if (url != null) {
                Log.d("url", url);
                Glide.with(activity).load(url).placeholder(R.drawable.gallery_thumb).into(holder.imageMessageIV);
                String finalUrl = url;
                holder.imageMessageIV.setOnClickListener(v -> openImage(finalUrl));
            }
        } else {
            showNoImage(holder);
        }
    }

    private void showVideo(final ChatMessage message, final MyViewHolder holder) {
        holder.contentLayout.setVisibility(VISIBLE);
         holder.mediaLayout.setVisibility(VISIBLE);
         holder.imageMessageIV.setVisibility(VISIBLE);
        holder.name.setVisibility(GONE);
        holder.docLayout.setVisibility(GONE);

        if (message.getMessage() != null && !message.getMessage().isEmpty()) {
            holder.playVideoIV.setVisibility(VISIBLE);
             String url = null;
            if (message.isLegacyChatMessage) {
                holder.name.setText(message.getMessage());
                url = message.getMessage();
            } else {
                new AWSUtils().getMediaUrlFromServer(activity, message.getMessage(), new FirebaseGetFilesUrlListener() {
                    @Override
                    public void onGetUrl(String url) {
                        Log.d("Virgil", "Video URL - " + url);
                        Glide.with(activity).load(url).placeholder(R.drawable.gallery_thumb).into(holder.imageMessageIV);
                        holder.imageMessageIV.setOnClickListener(v -> openVideo(url));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.d("Virgil", "Video URL - Could not load url, Error is " + errorMessage);
                        Glide.with(activity).load("").placeholder(R.drawable.gallery_thumb).into(holder.imageMessageIV);
                        holder.imageMessageIV.setOnClickListener(v -> openImage(""));
                    }
                });
                /*String decryptedMessage = ((AppController) activity.getApplication()).decryptMessage(message.getMessage(), getRequiredKeyToDecryptMessage(message));
                if (decryptedMessage != null && !decryptedMessage.isEmpty() && !decryptedMessage.equals(activity.getResources().getString(R.string.unable_to_decrypt))) {

                } else {
                    showText(message, holder);
                }*/
            }
            if (url != null) {
                String finalUrl = url;
                holder.playVideoIV.setOnClickListener(v -> openVideo(finalUrl));
                Log.d("url", url);
                long thumb = 1000;
                RequestOptions options = new RequestOptions().frame(thumb);
                Glide.with(activity).load(url).apply(options).into(holder.imageMessageIV);
            }
        } else {
            holder.playVideoIV.setVisibility(VISIBLE);
            holder.imageMessageIV.setVisibility(GONE);
        }
    }

    private void showFile(final ChatMessage message, final MyViewHolder holder) {
        holder.contentLayout.setVisibility(GONE);
        holder.mediaLayout.setVisibility(GONE);
        holder.imageMessageIV.setVisibility(GONE);
        holder.name.setVisibility(GONE);
        holder.playVideoIV.setVisibility(GONE);
        holder.docLayout.setVisibility(VISIBLE);

        if (message.getMessage() != null && !message.getMessage().isEmpty()) {
            String url = null;
            if (message.isLegacyChatMessage) {
                holder.name.setText(message.getMessage());
                url = message.getMessage();
            } else {
                new AWSUtils().getDocumentUrlFromServer(activity, message.getMessage(), new FirebaseGetFilesUrlListener() {
                    @Override
                    public void onGetUrl(String url) {
                        holder.docLayout.setOnClickListener(v -> openFile(url));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        holder.docLayout.setOnClickListener(v -> {
                            showToast(activity, "Unable to load this file");
                        });
                    }
                });
                /*String decryptedMessage = ((AppController) activity.getApplication()).decryptMessage(message.getMessage(), getRequiredKeyToDecryptMessage(message));
                if (decryptedMessage != null && !decryptedMessage.isEmpty() && !decryptedMessage.equals(activity.getResources().getString(R.string.unable_to_decrypt))) {

                } else {
                    showText(message, holder);
                }*/
            }
            if (url != null) {
                String finalUrl = url;
                holder.docLayout.setOnClickListener(v -> openFile(finalUrl));
            }
        }
    }

    private void showNoImage(MyViewHolder holder) {
        Glide.with(activity).load("").placeholder(R.drawable.gallery_thumb).into(holder.imageMessageIV);
    }

    private void openFile(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(browserIntent);
    }

    private void openVideo(String url) {
        Intent intent = new Intent(activity, VideoPlayerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("videoUrl", url);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    private void openImage(String url) {
        Intent intent = new Intent(activity, ImageViewerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("imageUrl", url);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).getSenderId().equals(myUserId)) {
            return VIEW_TYPE_MINE;
        } else {
            return VIEW_TYPE_OTHERS;
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public void addLegacyChats(List<ChatMessage> legacyChatMessages) {
        chatMessages.addAll(0, legacyChatMessages);
        notifyItemRangeInserted(0, legacyChatMessages.size());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView date;
        ImageView imageMessageIV, playVideoIV;
        CircleImageView profilePic;
        RelativeLayout mediaLayout, docLayout;
        LinearLayout contentLayout;

        public MyViewHolder(View view) {
            super(view);
            contentLayout = view.findViewById(R.id.contentLayout);
            name = view.findViewById(R.id.txt_msg);
            date = view.findViewById(R.id.date);
            imageMessageIV = view.findViewById(R.id.imageMessageIV);
            playVideoIV = view.findViewById(R.id.playVideoIV);
            profilePic = view.findViewById(R.id.profilePic);
            mediaLayout = view.findViewById(R.id.mediaLayout);
            docLayout = view.findViewById(R.id.docLayout);

        }
    }
}
