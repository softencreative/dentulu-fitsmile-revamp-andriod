package com.app.fitsmile.app;


import com.app.fitsmile.book.BookingSuccessPojo;
import com.app.fitsmile.firebase_chat.ChatListFirebaseResponse;
import com.app.fitsmile.firebase_chat.GetDocumentResponse;
import com.app.fitsmile.firebase_chat.GetMediaResponse;
import com.app.fitsmile.firebase_chat.LegacyChatsResponse;
import com.app.fitsmile.firebase_chat.UploadDocumentResponse;
import com.app.fitsmile.firebase_chat.UploadMediaResponse;
import com.app.fitsmile.my_address.DeleteAddressResponse;
import com.app.fitsmile.noti.NotificationPojo;
import com.app.fitsmile.response.CheckDoctorAvailablePojo;
import com.app.fitsmile.response.SettingsResponse;
import com.app.fitsmile.response.VideoListPojo;
import com.app.fitsmile.response.appointmentdetails.VideoDetailPojo;
import com.app.fitsmile.response.appointmentlist.DoctorAppointmentListPojo;
import com.app.fitsmile.response.appointmentlist.HealthHistoryQuestion;
import com.app.fitsmile.response.appointmentlist.ResponseCheckPromoCode;
import com.app.fitsmile.response.dentisdetails.ResponseDentistDetails;
import com.app.fitsmile.my_address.MyAddressListResponse;
import com.app.fitsmile.my_address.Update_addressuser;
import com.app.fitsmile.response.dentistlist.DoctorListPojo;
import com.app.fitsmile.response.doctime.DocAvailableTimeResponse;
import com.app.fitsmile.response.healthrec.ResponseViewHealthRecord;
import com.app.fitsmile.response.insurance.InsuranceResponse;
import com.app.fitsmile.response.login.DentalOfficeResponse;
import com.app.fitsmile.response.myaccount.FAQResponse;
import com.app.fitsmile.response.myaccount.Updateprofile;
import com.app.fitsmile.response.photoConsultancy.AddPhotoConsultancy.DynamicViewResponse;
import com.app.fitsmile.response.photoConsultancy.AddPhotoConsultancy.InsertWidgetQuestionDataRequest;
import com.app.fitsmile.response.photoConsultancy.AddPhotoConsultancy.InsertWidgetQuestionDataResponse;
import com.app.fitsmile.response.photoConsultancy.AddPhotoConsultancy.InsertWidgetRequest;
import com.app.fitsmile.response.photoConsultancy.AddPhotoConsultancy.InsertWidgetResponse;
import com.app.fitsmile.response.photoConsultancy.AddPhotoConsultancy.PhotoConsultationPricePojo;
import com.app.fitsmile.book.stripe.StripeSaveCustomerId;
import com.app.fitsmile.response.addpatient.AddPatientResponse;
import com.app.fitsmile.response.common.CommonResponse;
import com.app.fitsmile.response.date.AppointmentDateResponse;
import com.app.fitsmile.response.getstripe.StripeCustomerResponse;
import com.app.fitsmile.response.login.LoginResponse;
import com.app.fitsmile.response.myaccount.MyAccountResponse;
import com.app.fitsmile.response.patientlist.PatientListResponse;
import com.app.fitsmile.response.photoConsultancy.PhotoConsultancyDetail.PhotoConsultancyDetailResponse;
import com.app.fitsmile.response.photoConsultancy.appointmentList.PhotoConsultancyListResponse;
import com.app.fitsmile.response.profile.ProfileResponse;
import com.app.fitsmile.response.profile.UpdateProfile;
import com.app.fitsmile.response.register.RegistrationResponse;
import com.app.fitsmile.response.rewards.EarningResponse;
import com.app.fitsmile.response.rewards.SettingResponse;
import com.app.fitsmile.response.rewards.TransactionResponse;
import com.app.fitsmile.response.shop.AddToFavouritesResponse;
import com.app.fitsmile.response.shop.CategoryListResponse;
import com.app.fitsmile.response.shop.FavouriteProductsResponse;
import com.app.fitsmile.response.shop.GetCartProductsResponse;
import com.app.fitsmile.response.shop.MyOrderInfo;
import com.app.fitsmile.response.shop.OrderListResponse;
import com.app.fitsmile.response.shop.ProductListResponse;
import com.app.fitsmile.response.shop.ShopListInfoPojo;
import com.app.fitsmile.response.shop.ShopListPojo;
import com.app.fitsmile.response.tempschedule.ReScheduleBookingpojoo;
import com.app.fitsmile.response.trayMinder.AlignerListResponse;
import com.app.fitsmile.response.trayMinder.SmileProgressImageResponse;
import com.app.fitsmile.response.trayMinder.TimeLineResponse;
import com.app.fitsmile.response.trayMinder.TrayMinderDataResponse;
import com.app.fitsmile.response.trayMinder.TrayMinderResponse;
import com.app.fitsmile.response.videocall.VideoCallPojo;
import com.app.fitsmile.response.videofee.VideoCallFeeResponse;
import com.google.gson.JsonObject;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @POST("Register")
    Call<RegistrationResponse> register(@Body JsonObject body);

    @POST("Login")
    Call<LoginResponse> login(@Body JsonObject body);


    @POST("SessionLogin")
    Call<LoginResponse> sessionLogin(@Body JsonObject body);


    @POST("logout")
    Call<CommonResponse> logout(@Body JsonObject body);

    @POST("Profile")
    Call<ProfileResponse> profile(@Body JsonObject body);

    @POST("My_Account")
    Call<MyAccountResponse> myAccount(@Body JsonObject body);

    @POST("myAddresses")
    Call<MyAddressListResponse> getMyAddresses(@Body JsonObject body);

    @POST("patientsList")
    Call<PatientListResponse> getPatientList(@Body JsonObject body);

    @POST("update_favourite")
    Call<CommonResponse> updateDentistGenderPreference(@Body JsonObject body);

    @POST("deletePatient")
    Call<CommonResponse> deletePatient(@Body JsonObject body);

    @Multipart
    @POST("addPatient")
    Call<AddPatientResponse> addPatientImage(@Part MultipartBody.Part selected1ImagePart,
                                             @Part("user_id") RequestBody user_id,
                                             @Part("firstname") RequestBody firstname,
                                             @Part("lastname") RequestBody lastname,
                                             @Part("gender") RequestBody gender,
                                             @Part("email") RequestBody email,
                                             @Part("phone") RequestBody phone,
                                             @Part("mobile_code") RequestBody mobile_code,
                                             @Part("dob") RequestBody birthday_date,
                                             @Part("language") RequestBody language,
                                             @Part("relationship_type") RequestBody relationship_type);

    @Multipart
    @POST("editPatient")
    Call<AddPatientResponse> editPatientImage(@Part MultipartBody.Part selected1ImagePart,
                                              @Part("patient_id") RequestBody user_id,
                                              @Part("firstname") RequestBody firstname,
                                              @Part("lastname") RequestBody lastname,
                                              @Part("gender") RequestBody gender,
                                              @Part("email") RequestBody email,
                                              @Part("phone") RequestBody phone,
                                              @Part("mobile_code") RequestBody mobile_code,
                                              @Part("dob") RequestBody birthday_date,
                                              @Part("language") RequestBody language,
                                              @Part("relationship_type") RequestBody relationship_type);


    @POST("getOnlineDoctorsByCode")
    Call<DoctorListPojo> getOnlineDoctorListByCode(@Body JsonObject body);

    @POST("fetch_patient_dentist")
    Call<DoctorListPojo> fetchPatientDentistList(@Body JsonObject body);

    @POST("doctorAvailableDays")
    Call<AppointmentDateResponse> getDentistAvailableDate(@Body JsonObject body);

    @POST("availableVideoCallDoctorTiming")
    Call<DocAvailableTimeResponse> availableVideoCallDoctorTiming(@Body JsonObject body);

    @POST("get_form_data")
    Call<DynamicViewResponse> getFormData(@Body JsonObject getFormData);

    @POST("photo_consultation_payment")
    Call<DynamicViewResponse> photoConsultationPayment(@Body JsonObject getFormData);

    @GET("fetch_photo_consultation_price")
    Call<PhotoConsultationPricePojo> fetchPhotoConsultationPrice();

    @POST("insert_widget")
    Call<InsertWidgetResponse> insertWidget(@Body InsertWidgetRequest insertWidgetRequest);

    @POST("insert_widget_question_data")
    Call<InsertWidgetQuestionDataResponse> insertWidgetQuestionData(@Body InsertWidgetQuestionDataRequest insertWidgetQuestionDataRequest);

    @POST("fetch_patient_consultations_list")
    Call<PhotoConsultancyListResponse> getPhotoConsultationList(@Body JsonObject getJsonObject);

    @POST("fetch_consultations_detail")
    Call<PhotoConsultancyDetailResponse> getPhotoConsultancyDetail(@Body JsonObject getJsonObject);

    @POST("getStripeCustomerId")
    Call<StripeCustomerResponse> getStripeCustomerId(@Body JsonObject body);

    @POST("saveStripeCustomerId")
    Call<StripeSaveCustomerId> getSaveStripeId(@Body JsonObject body);

    @POST("getVideoCallFees")
    Call<VideoCallFeeResponse> getVideoCallFees(@Body JsonObject body);

    @POST("temp_schedule")
    Call<ReScheduleBookingpojoo> scheduleDateTime(@Body JsonObject body);

    @POST("videoCallAppointments")
    Call<DoctorAppointmentListPojo> getDoctorAppointmentList(@Body JsonObject body);

    @POST("videoCallAppointmentDetail")
    Call<VideoDetailPojo> getVideoCallDetail(@Body JsonObject body);

    @POST("doctor_information")
    Call<ResponseDentistDetails> getDoctorDetails(@Body JsonObject body);

    @POST("update_address")
    Call<Update_addressuser> updateAddress(@Body JsonObject body);

    @POST("getNotifications")
    Call<NotificationPojo> getNotificationList(@Body JsonObject body);

    @POST("Forgot")
    Call<CommonResponse> forgotPassword(@Body JsonObject body);

    @Multipart
    @POST("UpdateProfile")
    Call<UpdateProfile> profileUpdateWithImage(@Part MultipartBody.Part selected1ImagePart,
                                        @Part("user_id") RequestBody user_id,
                                        @Part("username") RequestBody usernmae,
                                        @Part("lastname") RequestBody lastname,
                                        @Part("gender") RequestBody gender,
                                        @Part("email") RequestBody email,
                                        @Part("phone") RequestBody phone,
                                        @Part("mobile_code") RequestBody mobile_code,
                                        @Part("language") RequestBody language,
                                        @Part("birthday_date") RequestBody birthday_date);

    @Multipart
    @POST("UpdateProfile")
    Call<UpdateProfile> profileUpdateWithoutImage(
                                        @Part("user_id") RequestBody user_id,
                                        @Part("username") RequestBody usernmae,
                                        @Part("lastname") RequestBody lastname,
                                        @Part("gender") RequestBody gender,
                                        @Part("email") RequestBody email,
                                        @Part("phone") RequestBody phone,
                                        @Part("mobile_code") RequestBody mobile_code,
                                        @Part("language") RequestBody language,
                                        @Part("birthday_date") RequestBody birthday_date);



    @POST("transactionDetails")
    Call<TransactionResponse> getTransactionList(@Body JsonObject body);

    @POST("earningDetails")
    Call<EarningResponse> getEarning(@Body JsonObject body);

    @POST("appConfig")
    Call<SettingResponse> getShare(@Body JsonObject jsonObject);


    @POST("ChangePassword")
    Call<CommonResponse> ChangePassword(@Body JsonObject body);

    @Multipart
    @POST("makeVideoCallAppointment")
    Call<BookingSuccessPojo> makeVideoCallAppointmentExistDentistFlow(
            @Part MultipartBody.Part selected1ImagePart,
            @Part MultipartBody.Part selected1ImagePart1,
            @Part MultipartBody.Part selected1ImagePart2,
            @Part MultipartBody.Part selected1ImagePart3,
            @Part MultipartBody.Part selected1ImagePart4,
            @Part("user_id") RequestBody user_id,
            @Part("doctor_id") RequestBody doctor_id,
            @Part("cardId") RequestBody cardId,
            @Part("customerId") RequestBody customerId,
            @Part("is_sandbox") RequestBody is_sandbox,
            @Part("patient_id") RequestBody patient_id,
            @Part("with_insurance") RequestBody with_insurance,
            @Part("specialist_category") RequestBody specialist_category,
            @Part("coupon_code") RequestBody coupon_code,
            @Part("total_amount") RequestBody total_amount,
            @Part("paid_amount") RequestBody paid_amount,
            @Part("category") RequestBody category,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("insurance_provider") RequestBody insurance_provider,
            @Part("insurance_number") RequestBody insurance_number,
            @Part("booking_date") RequestBody booking_date,
            @Part("booking_time") RequestBody booking_time,
            @Part("allergies") RequestBody allergies,
            @Part("indicate_other_allergies") RequestBody indicate_other_allergies,
            @Part("medications") RequestBody medications,
            @Part("diagnosed") RequestBody diagnosed,
            @Part("medical_procedures") RequestBody medical_procedures,
            @Part("family_member_diagnosed") RequestBody family_member_diagnosed,
            @Part("debug") RequestBody debug,
            @Part("by_code") RequestBody bycode,
            @Part("isHealthRecordUpdated") RequestBody isHealthRecordUpdated,
            @Part("language") RequestBody language,
            @Part("booking_source") RequestBody platform);

    @POST("FirebaseChatList")
    Call<ChatListFirebaseResponse> getFirebaseChatList(@Body JsonObject body);

    @POST("getLegacyChat")
    Call<LegacyChatsResponse> getLegacyChats(@Body JsonObject body);

    @POST("sendOneSignalNotificationForChat")
    Call<LegacyChatsResponse> sendOneSignalNotificationForChat(@Body JsonObject body);

    @Multipart
    @POST("uploadFirebaseMedia")
    Call<UploadMediaResponse> uploadFirebaseMedia(@Part MultipartBody.Part file);

    @Multipart
    @POST("uploadFirebaseDocs")
    Call<UploadDocumentResponse> uploadFirebaseDocs(@Part MultipartBody.Part file);

    @POST("getFirebaseMedia")
    Call<GetMediaResponse> getFirebaseMedia(@Body JsonObject body);

    @POST("getFirebaseDocs")
    Call<GetDocumentResponse> getFirebaseDocs(@Body JsonObject body);

    @POST("saas_product_category")
    Call<CategoryListResponse> getCategoryList(@Body JsonObject body);
    @POST("saas_product")
    Call<ProductListResponse> getProductList(@Body JsonObject body);
    @POST("add_user_favourite_product")
    Call<AddToFavouritesResponse> addProductToFavourites(@Body JsonObject body);

    @POST("remove_user_favourite_product")
    Call<AddToFavouritesResponse> removeProductFromFavourites(@Body JsonObject body);

    @POST("cartItemsCount")
    Call<ShopListInfoPojo> cartItemsCount(@Body JsonObject body);

    @POST("updateItemToCart")
    Call<ShopListInfoPojo> updateItemToCart(@Body JsonObject body);
    @POST("productDetails")
    Call<ShopListInfoPojo> getProductDetails(@Body JsonObject body);

    @POST("cartItems")
    Call<GetCartProductsResponse> cartItems(@Body JsonObject body);

    @POST("checkZipCode")
    Call<CommonResponse> getZipCode(@Body JsonObject body);

    @POST("get_user_favourite_product")
    Call<FavouriteProductsResponse> getFavouriteProducts(@Body JsonObject body);

    @POST("checkDoctorAvailable")
    Call<CheckDoctorAvailablePojo> getCheckDoctorPojo(@Body JsonObject body);


    @POST("sendVideoCallInvitation")
    Call<VideoListPojo> sendVideoCallInvitation(@Body JsonObject body);

    @POST("rejectVideoCall")
    Call<VideoCallPojo> rejectVideoCall(@Body JsonObject body);

    @POST("getVideoCallInfo")
    Call<VideoCallPojo> getVideoCallInfo(@Body JsonObject body);

    @POST("videoCallFeedback")
    Call<CommonResponse> getVideoCallFeedBACK(@Body JsonObject body);

    @POST("fetch_health_history_detail")
    Call<ResponseViewHealthRecord> getMedicalRecordView(@Body JsonObject body);

    @Multipart
    @POST("saveSignature")
    Call<CommonResponse> getSignature(@Part MultipartBody.Part selected1ImagePart, @Part("user_id") RequestBody user_id, @Part("booking_id") RequestBody booking_id,@Part("language") RequestBody language);

    @POST("orderCheckout")
    Call<ShopListPojo> orderCheckout(@Body JsonObject body);

    @POST("myProductOrder")
    Call<OrderListResponse> myProductOrder(@Body JsonObject body);

    @POST("myProductOrderDetail")
    Call<MyOrderInfo> myProductOrderDetail(@Body JsonObject body);

    @POST("cancelOrder")
    Call<MyOrderInfo> cancelOrder(@Body JsonObject body);

    @POST("orderFeedback")
    Call<MyOrderInfo> orderFeedback(@Body JsonObject body);
    @Multipart
    @POST("insuranceUpload")
    Call<Updateprofile> updateSignaturea(@Part MultipartBody.Part selected1ImagePart,
                                         @Part MultipartBody.Part selected1ImagePart2,
                                         @Part MultipartBody.Part selected1ImagePart3,
                                         @Part MultipartBody.Part selected1ImagePart4,
                                         @Part("user_id") RequestBody user_id,
                                         @Part("insurance_provider") RequestBody insurance_provider,
                                         @Part("insurance_number") RequestBody insurance_number,
                                         @Part("language") RequestBody language);

    @POST("My_Insurance")
    Call<InsuranceResponse> getInsurance(@Body JsonObject body);

    @POST("checkCouponcode")
    Call<ResponseCheckPromoCode> checkCoupon(@Body JsonObject body);

    @POST("deleteAddress")
    Call<DeleteAddressResponse> deleteAddress(@Body JsonObject body);

    @POST("fetch_notification_setting")
    Call<SettingsResponse> fetchNotificationSettings(@Body JsonObject jsonObject);

    @POST("update_notification_setting")
    Call<SettingsResponse> updateNotification(@Body JsonObject jsonObject);


    @POST("book_minder_appointment")
    Call<SettingsResponse> bookFitsReminder(@Body JsonObject jsonObject);

    @POST("fetch_minder_list")
    Call<TrayMinderResponse> fetchFitsReminder(@Body JsonObject jsonObject);

    @POST("fetch_minder_detail")
    Call<TrayMinderDataResponse> fetchFitsReminderDetails(@Body JsonObject jsonObject);

    @POST("update_minder_detail")
    Call<TrayMinderDataResponse> updateFitsReminder(@Body JsonObject jsonObject);

    @POST("upload_aligner_image")
    Call<SettingsResponse> uploadAlignerImage(@Body JsonObject jsonObject);

    @Multipart
    @POST("upload_aligner_images_data")
    Call<SettingsResponse> uploadAlignerImageData(@Part MultipartBody.Part imagefront,
                                               @Part MultipartBody.Part imageleft,
                                               @Part MultipartBody.Part imageright,
                                               @Part MultipartBody.Part imageup,
                                               @Part MultipartBody.Part imagedown,
                                               @Part("patient_id") RequestBody patient_id,
                                               @Part("id") RequestBody fitssmile_id,
                                               @Part("aligner_no") RequestBody aligner_no,
                                               @Part("language") RequestBody language);

    @POST("fetch_aligner_images")
    Call<SmileProgressImageResponse> fetchAlignerImage(@Body JsonObject jsonObject);


    @POST("update_aligner_image")
    Call<SettingsResponse> updateAlignerImage(@Body JsonObject jsonObject);

    @POST("delete_aligner_image")
    Call<SettingsResponse> deleteAlignerImage(@Body JsonObject jsonObject);

    @POST("update_image_aligner_no")
    Call<SettingsResponse> updateAlignerNumber(@Body JsonObject jsonObject);

    @POST("fetch_aligner_list")
    Call<AlignerListResponse> fetchAlignerList(@Body JsonObject jsonObject);

    @POST("update_remind_time")
    Call<SettingsResponse> updateRemindTime(@Body JsonObject jsonObject);

    @POST("aligner_reminder")
    Call<SettingResponse> updateAlignerReminder(@Body JsonObject jsonObject);

    @POST("getDentistListByCode")
    Call<DoctorListPojo> getExistingDocList(@Body JsonObject body);

    @POST("delete_minder_appointment")
    Call<SettingsResponse> deleteReminder(@Body JsonObject jsonObject);

    @POST("getOfficeFAQ")
    Call<FAQResponse> getFAQ(@Body JsonObject jsonObject);

    @POST("term_and_policy")
    Call<FAQResponse> getTerms(@Body JsonObject jsonObject);

    @POST("update_default_minder_appointment")
    Call<SettingsResponse> updateDefaultAppointment(@Body JsonObject jsonObject);

    @POST("update_aligner_days")
    Call<SettingsResponse> updateAlignerDays(@Body JsonObject jsonObject);

    @POST("update_aligners")
    Call<SettingsResponse>updateAlignerTotalNumber(@Body JsonObject jsonObject);

    @POST("switch_aligner")
    Call<SettingsResponse> changeAligner(@Body JsonObject jsonObject);

    @POST("update_daily_goal")
    Call<SettingsResponse> updateDailyGoal(@Body JsonObject jsonObject);

    @POST("getHealthRecord")
    Call<HealthHistoryQuestion> getHealthRecordQuestions(@Body JsonObject body);

    @POST("updateHealthRecord")
    Call<ReScheduleBookingpojoo> getUpdateHealthHistory(@Body JsonObject body);

    @POST("update_alarm_data")
    Call<SettingsResponse> updateAlarmData(@Body JsonObject jsonObject);

    @POST("fetch_minder_timeline")
    Call<TimeLineResponse> fetchTimeLineData(@Body JsonObject jsonObject);

    @GET("fetch_admin_detail")
    Call<DentalOfficeResponse> fetchDentalOffice();

    @POST("update_appointment_status")
    Call<SettingsResponse> updateAppointmentStatus(@Body JsonObject jsonObject);

    @POST("updateNotificationReadStatus")
    Call<CommonResponse> getCheckNotification(@Body JsonObject body);
}
