package com.app.fitsmile.shop.inter;

import com.app.fitsmile.response.shop.OrderListResponse;

public interface IOrderListener {
    void onViewOrder(OrderListResponse.OrderData order, String url);
}
