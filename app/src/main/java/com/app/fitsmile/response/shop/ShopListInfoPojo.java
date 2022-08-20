package com.app.fitsmile.response.shop;

public class ShopListInfoPojo {

    private Datas datas;
    private String message;
    private String status;
    private Integer cartItemCount;

    public Datas getDatas () {
        return datas;
    }

    public void setDatas (Datas datas) {
        this.datas = datas;
    }

    public String getMessage () {
        return message;
    }

    public void setMessage (String message) {
        this.message = message;
    }

    public String getStatus () {
        return status;
    }

    public void setStatus (String status) {
        this.status = status;
    }

    public Integer getCartItemCount() {
        return cartItemCount;
    }

    public void setCartItemCount(Integer cartItemCount) {
        this.cartItemCount = cartItemCount;
    }

    @Override
    public String toString() {
        return "ClassPojo [datas = "+datas+", message = "+message+", status = "+status+"]";
    }

    public class Datas {
        private String imageS3;
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

        public String getImage () {
            return imageS3;
        }

        public void setImage (String image) {
            this.imageS3 = image;
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

        @Override
        public String toString() {
            return "ClassPojo [imageS3 = "+imageS3+", is_deleted = "+is_deleted+", thumb = "+thumb+", price = "+price+", in_cart = "+in_cart+", name = "+name+", id = "+id+", created_date = "+created_date+", category = "+category+", stock = "+stock+", info = "+info+", status = "+status+"]";
        }
    }



}
