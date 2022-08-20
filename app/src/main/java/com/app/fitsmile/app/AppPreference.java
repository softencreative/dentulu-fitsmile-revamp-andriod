package com.app.fitsmile.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppPreference {

    private SharedPreferences sharedPreferences;

    public AppPreference(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void clearPreference() {
        sharedPreferences.edit().clear().apply();
    }

    private final String USERNAME = "USERNAME";
    private final String FIRST_NAME = "FIRST_NAME";
    private final String LAST_NAME = "LAST_NAME";
    private final String PERSONID = "PERSONID";
    private final String PROFILE_URL = "PROFILE_URL";
    private final String TOKEN = "TOKEN";
    private final String REWARD_POINT = "reward_point";

    public static final String STR_TIME_ZONE = "time_zone_str";
    private static final String loginvalue = "loginvalue";
    private static final String profile = "profile";
    private static final String isAuthentication = "isAuthentication";
    private static final String image = "image";
    private static final String Email = "email";
    private static final String firebaseUID = "firebase_uid";
    private static final String firebaseCustomToken = "firebase_custom_token";
    private static final String dentalOfficeFirebase_uid = "dental_office_firebase_uid";
    private static final String byCode = "byCode";
    private static final String providerId = "providerId";
    private static final String providerUserLevel = "providerUserLevel";
    private static final String dentalOfficeLogo = "dentalOfficeLogo";
    private static String INVITE = "invitecode";
    private static String IS_LOGGED_IN = "is_logged_in";
    private static String SECONDS = "seconds";
    private static String SECONDS_OUT = "seconds_out";


    private static final String AGORA_TOKEN = "agora_token";
    private static final String AGORA_CHANNEL = "agora_channel";
    private static final String AGORA_SENDER = "agora_sender";
    private static final String AGORA_RECEIVER = "agora_receiver";
    private static final String AGORA_FNAME = "agora_fname";
    private static final String AGORA_LNAME = "agora_lname";
    private static final String AGORA_VIDEO_ID = "agora_videoid";
    private static final String AGORA_VIDEO_DURATION = "agora_duration";
    private static final String AGORA_LIMITED = "agora_limited";
    private static final String AGORA_EMERGENCY = "agora_emergency";
    public static final String PRODUCT_COUNT = "product_count";
    public static final String PRODUCT_ID = "product";
    public static final String IP_ADDRESS_ID = "ip_address";
    public static final String SET_TOUCH_ID = "touch_id";
    public static final String REMINDER_LIST = "reminder_list";
    public static final String ImageArray = "imageArray";
    public static final String FITS_REMINDER_ID = "fitsReminderId";
    public static final String LOGIN_COUNT = "loginCount";


    public void setAgoraCallDetails(String agora_token, String agora_channel, String agora_sender, String agora_receiver, String agora_fname,
                                              String agora_lname, String agora_videoid, String agora_emergency, String agora_limited) {
        sharedPreferences.edit().putString(AGORA_TOKEN, agora_token).apply();
        sharedPreferences.edit().putString(AGORA_CHANNEL, agora_channel).apply();
        sharedPreferences.edit().putString(AGORA_SENDER, agora_sender).apply();
        sharedPreferences.edit().putString(AGORA_RECEIVER, agora_receiver).apply();
        sharedPreferences.edit().putString(AGORA_FNAME, agora_fname).apply();
        sharedPreferences.edit().putString(AGORA_LNAME, agora_lname).apply();
        sharedPreferences.edit().putString(AGORA_VIDEO_ID, agora_videoid).apply();
        sharedPreferences.edit().putString(AGORA_EMERGENCY, agora_emergency).apply();
        sharedPreferences.edit().putString(AGORA_LIMITED, agora_limited).apply();
    }

    public String getAGORA_TOKEN() {
        return sharedPreferences.getString(AGORA_TOKEN, "");
    }

    public String getAGORA_CHANNEL() {
        return sharedPreferences.getString(AGORA_CHANNEL, "");
    }

    public String getAGORA_SENDER() {
        return sharedPreferences.getString(AGORA_SENDER, "");
    }

    public String getAGORA_RECEIVER() {
        return sharedPreferences.getString(AGORA_RECEIVER, "");
    }

    public String getAGORA_FNAME() {
        return sharedPreferences.getString(AGORA_FNAME, "");
    }

    public String getAGORA_LNAME() {
        return sharedPreferences.getString(AGORA_LNAME, "");
    }

    public String getAGORA_VIDEO_ID() {
        return sharedPreferences.getString(AGORA_VIDEO_ID, "");
    }

    public String getAGORA_VIDEO_DURATION() {
        return sharedPreferences.getString(AGORA_VIDEO_DURATION, "");
    }

    public String getAGORA_EMERGENCY() {
        return sharedPreferences.getString(AGORA_EMERGENCY, "");
    }

    public String getAGORA_LIMITED() {
        return sharedPreferences.getString(AGORA_LIMITED, "");
    }

    public void clearAgoraCallDetails() {
        sharedPreferences.edit().putString(AGORA_TOKEN, "");
        sharedPreferences.edit().putString(AGORA_CHANNEL, "");
        sharedPreferences.edit().putString(AGORA_SENDER, "");
        sharedPreferences.edit().putString(AGORA_RECEIVER, "");
        sharedPreferences.edit().putString(AGORA_FNAME, "");
        sharedPreferences.edit().putString(AGORA_LNAME, "");
        sharedPreferences.edit().putString(AGORA_VIDEO_ID, "");
        sharedPreferences.edit().putString(AGORA_VIDEO_DURATION, "");
        sharedPreferences.edit().putString(AGORA_EMERGENCY, "");
        sharedPreferences.edit().putString(AGORA_LIMITED, "");
        sharedPreferences.edit().commit();
    }

    public void setProductId(String product) {
        sharedPreferences.edit().putString(PRODUCT_ID, product).apply();
    }

    public void setIPAddress(String ip_address) {
        sharedPreferences.edit().putString(IP_ADDRESS_ID, ip_address).apply();
    }

    public String getProductId() {
        return sharedPreferences.getString(PRODUCT_ID, "");
    }

    public String getIPAddress() {
        return sharedPreferences.getString(IP_ADDRESS_ID, "");
    }


    public String getSTR_TIME_ZONE() {
        return sharedPreferences.getString(STR_TIME_ZONE, "");
    }

    public void setSTR_TIME_ZONE(String time_zone_str) {
        sharedPreferences.edit().putString(STR_TIME_ZONE, time_zone_str).apply();
    }

    public String getDentalOfficeLogo() {
        return sharedPreferences.getString(dentalOfficeLogo, "");
    }

    public void setDentalOfficeLogo(String logo) {
        sharedPreferences.edit().putString(dentalOfficeLogo, logo).apply();
    }

    public String getProviderLevel() { // if 1 then existing dentist flow or 0 then old patient flow
        return sharedPreferences.getString(providerUserLevel, "");
    }

    public void setProviderLevel(String providerLevel) {  // if 1 then existing dentist flow or 0 then old patient flow
        sharedPreferences.edit().putString(providerUserLevel, providerLevel).apply();
    }

    public String getProviderId() { // if 1 then existing dentist flow or 0 then old patient flow
        return sharedPreferences.getString(providerId, "");
    }

    public void setProviderId(String provider_Id) {  // if 1 then existing dentist flow or 0 then old patient flow
        sharedPreferences.edit().putString(providerId, provider_Id).apply();
    }

    public String getByCode() { // if 1 then existing dentist flow or 0 then old patient flow
        return sharedPreferences.getString(byCode, "");
    }

    public void setByCode(String by_code) {  // if 1 then existing dentist flow or 0 then old patient flow
        sharedPreferences.edit().putString(byCode, by_code).apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    public void setisLoggedIn(boolean bool) {
        sharedPreferences.edit().putBoolean(IS_LOGGED_IN, bool).apply();
    }

    public String getEmail() {
        return sharedPreferences.getString(Email, "");
    }

    public void setEmail(String email) {
        sharedPreferences.edit().putString(Email, email).apply();
    }


    public String getImage() {
        return sharedPreferences.getString(image, "");
    }

    public void setImage(String images) {
        sharedPreferences.edit().putString(image, images).apply();
    }

    public String getloginvalue() {
        return sharedPreferences.getString(loginvalue, "");
    }

    public void setloginvalue(String getloginvalue) {
        sharedPreferences.edit().putString(loginvalue, getloginvalue).apply();
    }

    public void setProfilePicture(String url) {
        sharedPreferences.edit().putString(PROFILE_URL, url).apply();
    }

    public String getPriflePicture() {
        return sharedPreferences.getString(PROFILE_URL, "");
    }

    public String getFirebaseUID() {
        return sharedPreferences.getString(firebaseUID, "");
    }

    public void setFirebaseUID(String firebase_UID) {
        sharedPreferences.edit().putString(firebaseUID, firebase_UID).apply();
    }

    public void setDentalOfficeFirebaseUID(String dental_office_firebase_uid) {
        sharedPreferences.edit().putString(dentalOfficeFirebase_uid, dental_office_firebase_uid).apply();
    }

    public String getDentalOfficeFirebase_uid() {
        return sharedPreferences.getString(dentalOfficeFirebase_uid, "");
    }

    public String getFirebaseCustomToken() {
        return sharedPreferences.getString(firebaseCustomToken, "");
    }

    public void setFirebaseCustomToken(String firebase_custom_token) {
        sharedPreferences.edit().putString(firebaseCustomToken, firebase_custom_token).apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(USERNAME, "");
    }

    public void setUserName(String name) {
        sharedPreferences.edit().putString(USERNAME, name).apply();
    }

    public String getFirstName() {
        return sharedPreferences.getString(FIRST_NAME, "");
    }

    public void setFirstName(String name) {
        sharedPreferences.edit().putString(FIRST_NAME, name).apply();
    }

    public String getLastName() {
        return sharedPreferences.getString(LAST_NAME, "");
    }

    public void setLastName(String name) {
        sharedPreferences.edit().putString(LAST_NAME, name).apply();
    }

    public String getToken() {
        return sharedPreferences.getString(TOKEN, "");
    }

    public void setToken(String token) {
        sharedPreferences.edit().putString(TOKEN, token).apply();
    }

    public String getUserId() {
        return sharedPreferences.getString(profile, "");
    }

    public void setUserId(String userId) {
        sharedPreferences.edit().putString(profile, userId).apply();
    }

    public int getPersonId() {
        return sharedPreferences.getInt(PERSONID, 0);
    }

    public void setPersonId(int personid) {
        sharedPreferences.edit().putInt(PERSONID, personid).apply();
    }

    public String getIsAuthentication() {
        return sharedPreferences.getString(isAuthentication, "");
    }

    public void setIsAuthentication(String isAuthentications) {
        sharedPreferences.edit().putString(isAuthentication, isAuthentications).apply();
    }

    public String getreferalcode() {

        return sharedPreferences.getString(INVITE, "");
    }

    public void setreferalcode(String userId) {
        sharedPreferences.edit().putString(INVITE, userId).apply();
    }

    public int getSeconds() {

        return sharedPreferences.getInt(SECONDS, 0);
    }

    public void setSeconds(int seconds) {
        sharedPreferences.edit().putInt(SECONDS, seconds).apply();
    }

    public int getOutSeconds() {

        return sharedPreferences.getInt(SECONDS_OUT, 0);
    }

    public void setOutSeconds(int seconds) {
        sharedPreferences.edit().putInt(SECONDS_OUT, seconds).apply();
    }


    public String getRewardPoint() {
        return sharedPreferences.getString(REWARD_POINT, "0");
    }

    public void setRewardPoint(String rewardPoint) {
        sharedPreferences.edit().putString(REWARD_POINT, rewardPoint).apply();
    }
    public void setTouchId(Boolean touch_id) {
        sharedPreferences.edit().putBoolean(SET_TOUCH_ID, touch_id).apply();
    }

    public Boolean getTouchId() {
        return sharedPreferences.getBoolean(SET_TOUCH_ID, false);
    }

    public void setReminderList(String reminderList) {
        sharedPreferences.edit().putString(REMINDER_LIST,reminderList).apply();
    }

    public String getReminderList() {
        return sharedPreferences.getString(REMINDER_LIST, "");
    }

    public void setImageArray(String reminderList) {
        sharedPreferences.edit().putString(ImageArray,reminderList).apply();
    }

    public String getImageArray() {
        return sharedPreferences.getString(ImageArray, "");
    }



    public void setFitsReminderId(String id) {
        sharedPreferences.edit().putString(FITS_REMINDER_ID,id).apply();
    }

    public String getFitsReminderId() {
        return sharedPreferences.getString(FITS_REMINDER_ID, "");
    }

    public void setLoginCount(int count) {
        sharedPreferences.edit().putInt(LOGIN_COUNT,count).apply();
    }

    public int getLoginCount() {
        return sharedPreferences.getInt(LOGIN_COUNT, 0);
    }

}
