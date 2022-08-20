package com.app.fitsmile.common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppController;
import com.app.fitsmile.firebase_chat.ChatActivityFirebase;
import com.app.fitsmile.response.common.CommonResponse;
import com.app.fitsmile.splash.LoginActivity;
import com.app.fitsmile.splash.SliderActivity;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;
import com.wang.avi.AVLoadingIndicatorView;

import org.jetbrains.annotations.NotNull;

import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.fitsmile.app.AppConstants.FOUR_ZERO_ONE;
import static com.app.fitsmile.app.AppConstants.ONE;
import static com.app.fitsmile.firebase_chat.FirebaseConstants.OPPONENT_ID;
import static com.app.fitsmile.firebase_chat.FirebaseConstants.OPPONENT_NAME;
import static com.app.fitsmile.firebase_chat.FirebaseConstants.OPPONENT_PROFILE_PIC;

public class Utils {

    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static ImageView processbar = null;
    public static AppCompatDialog progressDialog = null;
    public static List<String> allergiesList = new ArrayList<>();
    public static List<String> otherAllergiesList = new ArrayList<>();
    public static List<String> medicationsList = new ArrayList<>();
    public static List<String> diagnosedList = new ArrayList<>();
    public static List<String> medicalProceduresList = new ArrayList<>();
    public static List<String> memberDiagnosedList = new ArrayList<>();

    public static String inputDate = "yyyy-MM-dd HH:mm:ss";
    public static String inputDateSmall = "yyyy-MM-dd hh:mm a";
    public static String inputOnlyDateSmall = "yyyy-MM-dd";
    public static String outputOnlyDateSmall = "MM-dd-yyyy";
    public static String outputDate = "MMM dd, yyyy hh:mm a";
    public static String outputDateAligner = "dd/MM/yyyy,hh:mm a";
    public static String outputDateChange = "MMM dd, yyyy";
    public static String outputOnlyDate = "MMM/dd/yyyy";
    public static String dateTimeDisplayFormat = "MMM dd, yyyy hh:mm a";
    public static String dateDisplayFormat = "MMM dd, yyyy";
    public static String dateDisplayFormatFull = "MMMM dd, yyyy";
    public static String dateDisplayMemberFormat = "MMM/dd/yyyy";
    public static String defaultCountrycode = "US";
    public static String inputTime = "hh:mm a";
    public static String outputTime = "HH:mm";
    public static String outputYear = "yyyy";
    public static String outputMonth = "MM";
    public static String outputDateAlone = "dd";
    private static AppCompatDialog sendingProgressDialog;

    public static String getNewDate(Context activity, String strDate, String format, String toFormat) {
        SimpleDateFormat oldFormatter = new SimpleDateFormat(format);
        oldFormatter.setTimeZone(TimeZone.getTimeZone(AppController.getInstance().getAppPreference().getSTR_TIME_ZONE()));
        Date value = null;
        String dueDateAsNormal = "";
        try {
            value = oldFormatter.parse(strDate);
            SimpleDateFormat newFormatter = new SimpleDateFormat(toFormat);
            newFormatter.setTimeZone(TimeZone.getDefault());
            dueDateAsNormal = newFormatter.format(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dueDateAsNormal;
    }


    public static String random(int len) {
        String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random RANDOM = new Random();
        StringBuilder sb = new StringBuilder(len);
        len = (len == 0) ? 10 : len;
        for (int i = 0; i < len; i++) {
            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }

        return sb.toString();
    }


    public static String displayPrice(String price) {
        try {
            double d = Double.parseDouble(price);
            if ((d - (int) d) != 0) {
                return String.format("$%.2f", Double.valueOf(d));
            } else {
                return String.format("$%.0f", Double.valueOf(d));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    public static String dateFormat(String date, String format, String toFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);//"yyyy-MM-dd HH:mm:ss"
        Date testDate = null;
        String newFormat = "";
        try {
            testDate = sdf.parse(date);
            java.text.SimpleDateFormat formatter = new SimpleDateFormat(toFormat);//"MMM dd, yyyy hh:mm a"
            newFormat = formatter.format(testDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return newFormat;
    }

    public static void hidekeyboard(View view, Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void showToast(Context context, String message) {
        if (getStr(message).isEmpty())
            return;
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    public static String convertTimestampToDate(long timeStamp) {
        Timestamp stamp = new Timestamp(timeStamp);
        Date date = new Date(stamp.getTime());
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(date);
        Calendar nowCalendar = Calendar.getInstance();
        if (nowCalendar.get(Calendar.DATE) == calendar.get(Calendar.DATE)) {
            return String.valueOf(DateFormat.format("hh:mm aa", date));
        } else if (nowCalendar.get(Calendar.DATE) - calendar.get(Calendar.DATE) == 1) {
            return DateFormat.format("hh:mm aa", calendar).toString() + "\n" + "Yesterday";
        } else {
            return DateFormat.format("hh:mm aa", calendar).toString() + "\n" + DateFormat.format("dd/MM/yyyy", calendar).toString();
        }
    }

    /*public static String getNewDate(Context activity, String strDate, String format, String toFormat) {
        SimpleDateFormat oldFormatter = new SimpleDateFormat(format);
        oldFormatter.setTimeZone(TimeZone.getTimeZone(AppController.getInstance().getAppPreference().getSTR_TIME_ZONE()));
        Date value = null;
        String dueDateAsNormal = "";
        try {
            value = oldFormatter.parse(strDate);
            SimpleDateFormat newFormatter = new SimpleDateFormat(toFormat);
            newFormatter.setTimeZone(TimeZone.getDefault());
            dueDateAsNormal = newFormatter.format(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dueDateAsNormal;
    }*/

    public static String convertTimestampToDateInList(long timeStamp) {
        Timestamp stamp = new Timestamp(timeStamp);
        Date date = new Date(stamp.getTime());
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(date);
        Calendar nowCalendar = Calendar.getInstance();
        if (nowCalendar.get(Calendar.DATE) == calendar.get(Calendar.DATE)) {
            return String.valueOf(DateFormat.format("hh:mm aa", date));
        } else if (nowCalendar.get(Calendar.DATE) - calendar.get(Calendar.DATE) == 1) {
            return "Yesterday";
        } else {
            return DateFormat.format("dd/MM/yyyy", calendar).toString();
        }
    }

    public static String getStr(String data) {
        if (data == null){
            return "";
        }
        else{
            return data.trim();
        }

    }


    public static String getDiscount(String total, String discount) {
        int discountAmount = (Integer.parseInt(total) - Integer.parseInt(discount));
        return String.valueOf(discountAmount).trim();
    }

   /* public static void moveWebView(Activity activity, String url) {
        Intent intent = new Intent(activity, Website.class);
        intent.putExtra("URL", url);
        activity.startActivity(intent);
    }

    public static void moveWebView(Context activity, String url) {
        Intent intent = new Intent(activity, Website.class);
        intent.putExtra("URL", url);
        activity.startActivity(intent);
    }*/

    public static Double getDoubleFromString(String str) {
        if (str == null || str.trim().isEmpty()) {
            return 0.0;
        } else {
            try {
                return Double.parseDouble(str);
            } catch (Exception e) {
                e.printStackTrace();
                return 0.0;
            }
        }
    }

    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidEmailId(String emailId) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches();
    }

    public static boolean checkSelfPermission(Activity activity, String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(activity, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }
        return true;
    }

    public static boolean isEmptyOrNull(String input) {
        return (input == null || input.isEmpty());
    }

    public static void signOut(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    public static void openProgressDialog(Context context) {
        progressDialog = new AppCompatDialog(context);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setContentView(R.layout.progress_loading);
        processbar = progressDialog.findViewById(R.id.processbar);
        progressDialog.setCancelable(false);
        Animation animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
        animation.setDuration(1000); //1 second duration for each animation cycle
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
        animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
        processbar.startAnimation(animation);
        progressDialog.show();
    }

    public static void closeProgressDialog() {
        if (progressDialog != null) {
            processbar.clearAnimation();
            progressDialog.dismiss();
        }
    }

    public static String getStringFromIntent(Intent intent, String key) {
        String value = "";
        if (intent == null || getStr(key).isEmpty())
            return value;
        if (intent.getExtras() == null)
            return value;
        if (intent.getExtras().getString(key) == null)
            return value;
        else
            return intent.getExtras().getString(key);

    }

    public static int getPixelFromDip(Context context, int dip) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        return (int) px;
    }

    public static void showSessionAlert(final Activity activity) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_common, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alert = dialogBuilder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();

        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvContent = dialogView.findViewById(R.id.tvContent);
        TextView tvNo = dialogView.findViewById(R.id.tvNo);
        TextView tvYes = dialogView.findViewById(R.id.tvYes);

        tvNo.setVisibility(View.GONE);

        tvTitle.setText(activity.getString(R.string.alert));
        tvContent.setText(activity.getString(R.string.you_are_already_logged));
        tvYes.setText(activity.getString(R.string.got_it));

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
                logOut(activity);
            }
        });
    }

    public static void logOut(final Activity activity) {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(activity).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        openProgressDialog(activity);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("notification_id", "");
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = AppController.getInstance().getClient().create(ApiInterface.class);
        Call<CommonResponse> mService = mApiService.logout(jsonObj);
        mService.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                closeProgressDialog();
                CommonResponse logout = response.body();
                if (getStr(logout.getStatus()).equals(ONE)) {
                    changeScreenAfterLogout(activity);
                    /*((AppController) activity.getApplication()).cleanUpVirgil(new VirgilCleanupListener() {
                        @Override
                        public void onCleanedUp() {

                        }

                        @Override
                        public void onError(String message) {
                            changeScreenAfterLogout(activity);
                            showToast(activity, message);
                        }
                    });*/
                } else if (Utils.getStr(logout.getStatus()).equals(FOUR_ZERO_ONE)) {
                    if(!activity.isFinishing()){
                        showSessionAlert(activity);
                    }

                } else {
                    showToast(activity, getStr(logout.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                if (!activity.isFinishing()) {
                    closeProgressDialog();
                }
                call.cancel();
            }
        });
    }

    private static void changeScreenAfterLogout(Activity activity) {
        ((AppController) activity.getApplication()).makeUserOffline();
        ((AppController) activity.getApplication()).stopFirebaseService();
        AppController.logoutFromFirebase();
//        ((AppController) activity.getApplication()).clearOutVirgil();
        AppController.getInstance().getAppPreference().clearPreference();
        LocaleManager.setLocale(activity);
        Intent intent = new Intent(activity, SliderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
        Log.e("Tag","logout done");
    }

    public static void showCommonPopup(Activity activity, String btnOne, String btnTwo, String title, String desp,
                                       boolean singleBtn, View.OnClickListener oneOnclick, View.OnClickListener twoOnClick) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
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

        tvTitle.setText(title);
        tvContent.setText(desp);
        tvNo.setText(btnTwo);
        tvYes.setText(btnOne);


        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneOnclick.onClick(view);
                alert.dismiss();
            }
        });

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twoOnClick.onClick(view);
                alert.dismiss();
            }
        });
    }

    public static String getDateFromTimestamp(long time) {
        try {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(time * 1000);
//            return DateFormat.format("MM-dd-yyyy", cal).toString();
            return DateFormat.format("E\ndd/MM\nyyyy", cal).toString();
        } catch (Exception e) {
            return "";
        }

    }

    public static Intent getChatIntent(Context context, String opponentId, String opponentName, @NotNull String opponentProfilePic) {
        Intent intent = new Intent(context, ChatActivityFirebase.class);
        intent.putExtra(OPPONENT_ID, opponentId);
        intent.putExtra(OPPONENT_NAME, opponentName);
        intent.putExtra(OPPONENT_PROFILE_PIC, opponentProfilePic);
        return intent;
    }

    public static void openSendingProgressDialog(Activity context) {
        sendingProgressDialog = new AppCompatDialog(context);
        sendingProgressDialog.setCancelable(false);
        sendingProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        sendingProgressDialog.setContentView(R.layout.progress_sending);
        sendingProgressDialog.show();
    }

    public static void startSendingAnimation() {
        if (sendingProgressDialog != null && sendingProgressDialog.isShowing()) {
            AVLoadingIndicatorView animatedCircleLoadingView = sendingProgressDialog.findViewById(R.id.circle_loading_view);
            animatedCircleLoadingView.setVisibility(View.VISIBLE);
            //animatedCircleLoadingView.startDeterminate();
        }
    }

    public static void stopSendingAnimation() {
        if (sendingProgressDialog != null && sendingProgressDialog.isShowing()) {
            AVLoadingIndicatorView animatedCircleLoadingView = sendingProgressDialog.findViewById(R.id.circle_loading_view);
            animatedCircleLoadingView.setVisibility(View.GONE);
            //animatedCircleLoadingView.stopOk();
        }
    }

    public static void setSendingMessage(String message) {
        if (!message.isEmpty()) {
            message = " (" + message + ")";
        }
        if (sendingProgressDialog != null && sendingProgressDialog.isShowing()) {
            ((TextView) sendingProgressDialog.findViewById(R.id.sendingMessageLabel)).setText("Sending" + message + " ...");
        }
    }

    public static void setSendingProgress(int progress) {
        if (sendingProgressDialog != null && sendingProgressDialog.isShowing()) {
            TextView textView = sendingProgressDialog.findViewById(R.id.parent);
            textView.setText(progress + " %");
        }
    }

    public static void closeSendingProgressDialog() {
        sendingProgressDialog.dismiss();
    }

    public static String getTimeFromTimestamp(long time) {
        try {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(time * 1000);
//            return DateFormat.format("MM-dd-yyyy", cal).toString();
            return DateFormat.format("HH:mm", cal).toString();
        } catch (Exception e) {
            return "";
        }

    }


    public static Date dateFromUTC(Date date) {
        return new Date(date.getTime() + Calendar.getInstance().getTimeZone().getOffset(date.getTime()));
    }

    public static Date dateToUTC(Date date) {
        return new Date(date.getTime() - Calendar.getInstance().getTimeZone().getOffset(date.getTime()));
    }

    public static void setSignatureText(Activity activity, TextView textView) {
        Typeface custom_font = Typeface.createFromAsset(activity.getAssets(), "fonts/DancingScript-Regular.ttf");
        textView.setTypeface(custom_font);
    }

    public static void setBoldText(Activity activity, TextView textView) {
        Typeface custom_font = Typeface.createFromAsset(activity.getAssets(), "fonts/Montserrat-Bold.otf");
        textView.setTypeface(custom_font);
    }
}
