package com.app.fitsmile.shop.inter;

import com.app.fitsmile.response.shop.GetCartProductsResponse;

public interface IGetCartProductsListener {
    void onCartProductsFetched(GetCartProductsResponse cartProductsResponse);
}