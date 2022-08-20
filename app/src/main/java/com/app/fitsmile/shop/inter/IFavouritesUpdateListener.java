package com.app.fitsmile.shop.inter;


import com.app.fitsmile.response.shop.ProductResult;

public interface IFavouritesUpdateListener {
    void onProductAddedToFavourite(ProductResult productResult, int position);
    void onProductRemovedFromFavourite(ProductResult productResult, int position);
}
