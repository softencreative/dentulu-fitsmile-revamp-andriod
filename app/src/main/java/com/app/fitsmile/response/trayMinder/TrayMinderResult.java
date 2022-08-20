package com.app.fitsmile.response.trayMinder;

import java.io.Serializable;
import java.util.List;

public class TrayMinderResult implements Serializable {
    private String id;

    private String ref_id;

    private String patient_id;

    private String type;

    private String no_of_aligners;

    private String no_of_days;

    private String aligner_number;

    private String wear_current_aligner;

    private String remind_time;

    private String total_in_time;

    private String total_out_time;

    private String updated_date;

    private String created_date;

    private String goal_time;

    private List<Timming_log> timming_log;

    private List<Logs> logs;

    private Today_timming_log today_timming_log;

    private List<Today_logs> today_logs;

    private int remaining_days;

    private String family_member_name;

    private String doctor_name;

    private String image;

    private String timer_on;

    private String fits_minder_percentage;

    private String next_aligner_no;

    private String total_wear_time;

    private String breakfast;

    private String lunch;

    private String dinner;

    private String cleaner;
    private String current_aligner_no;

    private String status;

    private String assigned_id;
    private String video_link;
    private String video_type;

    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public void setRef_id(String ref_id){
        this.ref_id = ref_id;
    }
    public String getRef_id(){
        return this.ref_id;
    }
    public void setPatient_id(String patient_id){
        this.patient_id = patient_id;
    }
    public String getPatient_id(){
        return this.patient_id;
    }
    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }
    public void setNo_of_aligners(String no_of_aligners){
        this.no_of_aligners = no_of_aligners;
    }
    public String getNo_of_aligners(){
        return this.no_of_aligners;
    }
    public void setNo_of_days(String no_of_days){
        this.no_of_days = no_of_days;
    }
    public String getNo_of_days(){
        return this.no_of_days;
    }
    public void setAligner_number(String aligner_number){
        this.aligner_number = aligner_number;
    }
    public String getAligner_number(){
        return this.aligner_number;
    }
    public void setWear_current_aligner(String wear_current_aligner){
        this.wear_current_aligner = wear_current_aligner;
    }
    public String getWear_current_aligner(){
        return this.wear_current_aligner;
    }
    public void setRemind_time(String remind_time){
        this.remind_time = remind_time;
    }
    public String getRemind_time(){
        return this.remind_time;
    }
    public void setTotal_in_time(String total_in_time){
        this.total_in_time = total_in_time;
    }
    public String getTotal_in_time(){
        return this.total_in_time;
    }
    public void setTotal_out_time(String total_out_time){
        this.total_out_time = total_out_time;
    }
    public String getTotal_out_time(){
        return this.total_out_time;
    }
    public void setUpdated_date(String updated_date){
        this.updated_date = updated_date;
    }
    public String getUpdated_date(){
        return this.updated_date;
    }
    public void setCreated_date(String created_date){
        this.created_date = created_date;
    }
    public String getCreated_date(){
        return this.created_date;
    }
    public void setTimming_log(List<Timming_log> timming_log){
        this.timming_log = timming_log;
    }
    public List<Timming_log> getTimming_log(){
        return this.timming_log;
    }
    public void setLogs(List<Logs> logs){
        this.logs = logs;
    }
    public List<Logs> getLogs(){
        return this.logs;
    }
    public void setToday_timming_log(Today_timming_log today_timming_log){
        this.today_timming_log = today_timming_log;
    }
    public Today_timming_log getToday_timming_log(){
        return this.today_timming_log;
    }
    public void setToday_logs(List<Today_logs> today_logs){
        this.today_logs = today_logs;
    }
    public List<Today_logs> getToday_logs(){
        return this.today_logs;
    }
    public void setRemaining_days(int remaining_days){
        this.remaining_days = remaining_days;
    }
    public int getRemaining_days(){
        return this.remaining_days;
    }

    public String getTimer_on() {
        return timer_on;
    }

    public void setTimer_on(String timer_on) {
        this.timer_on = timer_on;
    }

    public String getGoal_time() {
        return goal_time;
    }

    public void setGoal_time(String goal_time) {
        this.goal_time = goal_time;
    }

    public void setFamily_member_name(String family_member_name) {
        this.family_member_name = family_member_name;
    }

    public String getFamily_member_name() {
        return family_member_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public String getFits_minder_percentage() {
        return fits_minder_percentage;
    }

    public void setFits_minder_percentage(String fits_minder_percentage) {
        this.fits_minder_percentage = fits_minder_percentage;
    }

    public String getNext_aligner_no() {
        return next_aligner_no;
    }

    public void setNext_aligner_no(String next_aligner_no) {
        this.next_aligner_no = next_aligner_no;
    }

    public String getTotal_wear_time() {
        return total_wear_time;
    }

    public void setTotal_wear_time(String total_wear_time) {
        this.total_wear_time = total_wear_time;
    }

    public String getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(String breakfast) {
        this.breakfast = breakfast;
    }

    public String getLunch() {
        return lunch;
    }

    public void setLunch(String lunch) {
        this.lunch = lunch;
    }

    public String getDinner() {
        return dinner;
    }

    public void setDinner(String dinner) {
        this.dinner = dinner;
    }

    public String getCleaner() {
        return cleaner;
    }

    public void setCleaner(String cleaner) {
        this.cleaner = cleaner;
    }

    public String getCurrent_aligner_no() {
        return current_aligner_no;
    }

    public void setCurrent_aligner_no(String current_aligner_no) {
        this.current_aligner_no = current_aligner_no;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssigned_id() {
        return assigned_id;
    }

    public void setAssigned_id(String assigned_id) {
        this.assigned_id = assigned_id;
    }

    public String getVideo_link() {
        return video_link;
    }

    public void setVideo_link(String video_link) {
        this.video_link = video_link;
    }

    public String getVideo_type() {
        return video_type;
    }

    public void setVideo_type(String video_type) {
        this.video_type = video_type;
    }
}


