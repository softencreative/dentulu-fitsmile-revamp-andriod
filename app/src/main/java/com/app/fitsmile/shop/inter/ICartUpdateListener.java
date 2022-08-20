package com.app.fitsmile.shop.inter;

import com.app.fitsmile.response.shop.ProductResult;

public interface ICartUpdateListener {
    void onProductAddedToCart(ProductResult productResult, int position);
    void onProductRemovedFromCart(ProductResult productResult, int position);
}