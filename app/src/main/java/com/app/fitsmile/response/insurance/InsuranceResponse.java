package com.app.fitsmile.response.insurance;

import java.util.List;
public class InsuranceResponse
{
    private String status;

    private String insurance;

    private String driving_license;

    private String insurance_type;

    private String message;

    private String insurance_number;

    private String insurance_back;

    private String driving_license_back;

    private List<InsuranceType> insurance_type_master;

    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }
    public void setInsurance(String insurance){
        this.insurance = insurance;
    }
    public String getInsurance(){
        return this.insurance;
    }
    public void setDriving_license(String driving_license){
        this.driving_license = driving_license;
    }
    public String getDriving_license(){
        return this.driving_license;
    }
    public void setInsurance_type(String insurance_type){
        this.insurance_type = insurance_type;
    }
    public String getInsurance_type(){
        return this.insurance_type;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
    public void setInsurance_type_master(List<InsuranceType> insuranceTypeList){
        this.insurance_type_master = insuranceTypeList;
    }
    public List<InsuranceType> getInsurance_type_master(){
        return this.insurance_type_master;
    }

    public String getInsurance_number() {
        return insurance_number;
    }

    public void setInsurance_number(String insurance_number) {
        this.insurance_number = insurance_number;
    }

    public String getInsurance_back() {
        return insurance_back;
    }

    public void setInsurance_back(String insurance_back) {
        this.insurance_back = insurance_back;
    }

    public String getDriving_license_back() {
        return driving_license_back;
    }

    public void setDriving_license_back(String driving_license_back) {
        this.driving_license_back = driving_license_back;
    }
}
