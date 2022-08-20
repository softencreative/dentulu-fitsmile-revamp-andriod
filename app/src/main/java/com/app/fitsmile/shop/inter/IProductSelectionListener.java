package com.app.fitsmile.shop.inter;
import com.app.fitsmile.response.shop.ProductResult;

public interface IProductSelectionListener {
    void onProductSelected(ProductResult productResult);
    void onAddToCart(ProductResult productResult, int position);
    void onRemoveFromCart(ProductResult productResult, int position);
    void onAddToFavourite(ProductResult productResult, int position);
    void onRemoveFromFavourite(ProductResult productResult, int position);
}
