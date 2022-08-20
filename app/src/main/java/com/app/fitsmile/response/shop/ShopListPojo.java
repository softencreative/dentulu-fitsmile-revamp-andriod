package com.app.fitsmile.response.shop;

import java.util.ArrayList;

public class ShopListPojo {
    private ArrayList<Datas> datas;
    private String message;
    private String base_url;
    private String status;
    private String total_amount;

    public ArrayList<Datas> getDatas () {
        return datas;
    }

    public void setDatas (ArrayList<Datas> datas) {
        this.datas = datas;
    }

    public String getMessage () {
        return message;
    }

    public void setMessage (String message) {
        this.message = message;
    }

    public String getBase_url() {
        return base_url;
    }

    public void setBase_url(String base_url) {
        this.base_url = base_url;
    }

    public String getStatus () {
        return status;
    }

    public void setStatus (String status) {
        this.status = status;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    @Override
    public String toString() {
        return "ClassPojo [datas = "+datas+", message = "+message+", status = "+status+"]";
    }


    public class Datas {
        private String image;
        private String is_deleted;
        private String thumb;
        private String price;
        private String in_cart;
        private String name;
        private String id;
        private String created_date;
        private String category;
        private String stock;
        private String info;
        private String status;
        private String quantity;

        public String getImage () {
            return image;
        }

        public void setImage (String image) {
            this.image = image;
        }

        public String getIs_deleted () {
            return is_deleted;
        }

        public void setIs_deleted (String is_deleted) {
            this.is_deleted = is_deleted;
        }

        public String getThumb () {
            return thumb;
        }

        public void setThumb (String thumb) {
            this.thumb = thumb;
        }

        public String getPrice () {
            return price;
        }

        public void setPrice (String price) {
            this.price = price;
        }

        public String getIn_cart () {
            return in_cart;
        }

        public void setIn_cart (String in_cart) {
            this.in_cart = in_cart;
        }

        public String getName () {
            return name;
        }

        public void setName (String name) {
            this.name = name;
        }

        public String getId () {
            return id;
        }

        public void setId (String id) {
            this.id = id;
        }

        public String getCreated_date () {
            return created_date;
        }

        public void setCreated_date (String created_date) {
            this.created_date = created_date;
        }

        public String getCategory () {
            return category;
        }

        public void setCategory (String category) {
            this.category = category;
        }

        public String getStock () {
            return stock;
        }

        public void setStock (String stock) {
            this.stock = stock;
        }

        public String getInfo () {
            return info;
        }

        public void setInfo (String info) {
            this.info = info;
        }

        public String getStatus () {
            return status;
        }

        public void setStatus (String status) {
            this.status = status;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return "ClassPojo [image = "+image+", is_deleted = "+is_deleted+", thumb = "+thumb+", price = "+price+", in_cart = "+in_cart+", name = "+name+", id = "+id+", created_date = "+created_date+", category = "+category+", stock = "+stock+", info = "+info+", status = "+status+"]";
        }
    }


}

