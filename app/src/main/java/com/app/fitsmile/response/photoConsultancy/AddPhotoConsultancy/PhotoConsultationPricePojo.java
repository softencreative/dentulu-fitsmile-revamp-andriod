package com.app.fitsmile.response.photoConsultancy.AddPhotoConsultancy;

import java.io.Serializable;

public class PhotoConsultationPricePojo implements Serializable {
    private int status;
    private String message;
    private Datass data;

    public PhotoConsultationPricePojo() {
    }

    public PhotoConsultationPricePojo(int status, Datass data) {
        this.status = status;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Datass getData() {
        return data;
    }

    public void setData(Datass data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PhotoConsultationPricePojo{" +
                "status=" + status +
                ", data=" + data +
                '}';
    }
    public class Datass {
        private String price;

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }
}
