package com.app.fitsmile.response.shop;

public class MyOrderInfo
{
    private Datas datas;
    private String message;
    private String status;

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

    @Override
    public String toString() {
        return "ClassPojo [datas = "+datas+", message = "+message+", status = "+status+"]";
    }

    public class Datas {
        private String amount;
        private String coupon_code;
        private String shipping_city;
        private String payment_status;
        private String shipping_type;
        private String discount;
        private String order_status;
        private String order_status_name;
        private String order_ref_id;
        private String shipping_longitude;
        private String shipping_latitude;
        private String user_id;
        private String total_amount;
        private String shipping_pincode;
        private Details details;
        private String id;
        private String shipping_address;
        private String mode_of_payment;
        private String created_date;
        private String transaction_no;
        private String rating;
        private String comments;

        public String getAmount () {
            return amount;
        }

        public void setAmount (String amount) {
            this.amount = amount;
        }

        public String getCoupon_code () {
            return coupon_code;
        }

        public void setCoupon_code (String coupon_code) {
            this.coupon_code = coupon_code;
        }

        public String getShipping_city () {
            return shipping_city;
        }

        public void setShipping_city (String shipping_city) {
            this.shipping_city = shipping_city;
        }

        public String getPayment_status () {
            return payment_status;
        }

        public void setPayment_status (String payment_status) {
            this.payment_status = payment_status;
        }

        public String getShipping_type () {
            return shipping_type;
        }

        public void setShipping_type (String shipping_type) {
            this.shipping_type = shipping_type;
        }

        public String getDiscount () {
            return discount;
        }

        public void setDiscount (String discount) {
            this.discount = discount;
        }

        public String getOrder_status () {
            return order_status;
        }

        public void setOrder_status (String order_status) {
            this.order_status = order_status;
        }

        public String getOrder_status_name () {
            return order_status_name;
        }

        public void setOrder_status_name (String order_status_name) {
            this.order_status_name = order_status_name;
        }

        public String getOrder_ref_id () {
            return order_ref_id;
        }

        public void setOrder_ref_id (String order_ref_id) {
            this.order_ref_id = order_ref_id;
        }

        public String getShipping_longitude () {
            return shipping_longitude;
        }

        public void setShipping_longitude (String shipping_longitude) {
            this.shipping_longitude = shipping_longitude;
        }

        public String getShipping_latitude () {
            return shipping_latitude;
        }

        public void setShipping_latitude (String shipping_latitude) {
            this.shipping_latitude = shipping_latitude;
        }

        public String getUser_id () {
            return user_id;
        }

        public void setUser_id (String user_id) {
            this.user_id = user_id;
        }

        public String getTotal_amount () {
            return total_amount;
        }

        public void setTotal_amount (String total_amount) {
            this.total_amount = total_amount;
        }

        public String getShipping_pincode () {
            return shipping_pincode;
        }

        public void setShipping_pincode (String shipping_pincode) {
            this.shipping_pincode = shipping_pincode;
        }

        public Details getDetails () {
            return details;
        }

        public void setDetails (Details details) {
            this.details = details;
        }

        public String getId () {
            return id;
        }

        public void setId (String id) {
            this.id = id;
        }

        public String getShipping_address () {
            return shipping_address;
        }

        public void setShipping_address (String shipping_address) {
            this.shipping_address = shipping_address;
        }

        public String getMode_of_payment () {
            return mode_of_payment;
        }

        public void setMode_of_payment (String mode_of_payment) {
            this.mode_of_payment = mode_of_payment;
        }

        public String getCreated_date () {
            return created_date;
        }

        public void setCreated_date (String created_date) {
            this.created_date = created_date;
        }

        public String getTransaction_no () {
            return transaction_no;
        }

        public void setTransaction_no (String transaction_no) {
            this.transaction_no = transaction_no;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        @Override
        public String toString() {
            return "ClassPojo [amount = "+amount+", coupon_code = "+coupon_code+", shipping_city = "+shipping_city+", payment_status = "+payment_status+", shipping_type = "+shipping_type+", discount = "+discount+", order_status = "+order_status+", order_status_name = "+order_status_name+", order_ref_id = "+order_ref_id+", shipping_longitude = "+shipping_longitude+", shipping_latitude = "+shipping_latitude+", user_id = "+user_id+", total_amount = "+total_amount+", shipping_pincode = "+shipping_pincode+", details = "+details+", id = "+id+", shipping_address = "+shipping_address+", mode_of_payment = "+mode_of_payment+", created_date = "+created_date+", transaction_no = "+transaction_no+"]";
        }


        public class Details {
            private String image;
            private String quantity;
            private String thumb;
            private String is_deleted;
            private String total_amount;
            private String price;
            private String name;
            private String id;
            private String created_date;
            private String category;
            private String stock;
            private String info;
            private String status;

            public String getImage () {
                return image;
            }

            public void setImage (String image) {
                this.image = image;
            }

            public String getQuantity () {
                return quantity;
            }

            public void setQuantity (String quantity) {
                this.quantity = quantity;
            }

            public String getThumb () {
                return thumb;
            }

            public void setThumb (String thumb) {
                this.thumb = thumb;
            }

            public String getIs_deleted () {
                return is_deleted;
            }

            public void setIs_deleted (String is_deleted) {
                this.is_deleted = is_deleted;
            }

            public String getTotal_amount () {
                return total_amount;
            }

            public void setTotal_amount (String total_amount) {
                this.total_amount = total_amount;
            }

            public String getPrice () {
                return price;
            }

            public void setPrice (String price) {
                this.price = price;
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
                return "ClassPojo [image = "+image+", quantity = "+quantity+", thumb = "+thumb+", is_deleted = "+is_deleted+", total_amount = "+total_amount+", price = "+price+", name = "+name+", id = "+id+", created_date = "+created_date+", category = "+category+", stock = "+stock+", info = "+info+", status = "+status+"]";
            }
        }

    }
}

