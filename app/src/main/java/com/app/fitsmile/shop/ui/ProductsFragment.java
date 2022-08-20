package com.app.fitsmile.shop.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.base.BaseFragment;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.shop.CategoryListResponse;
import com.app.fitsmile.response.shop.CategoryListResult;
import com.app.fitsmile.response.shop.ProductListResponse;
import com.app.fitsmile.response.shop.ProductResult;
import com.app.fitsmile.shop.adapter.CategoryAdapter;
import com.app.fitsmile.shop.adapter.ProductAdapter;
import com.app.fitsmile.shop.inter.ICartUpdateListener;
import com.app.fitsmile.shop.inter.IFavouritesUpdateListener;
import com.app.fitsmile.shop.inter.IProductSelectionListener;
import com.app.fitsmile.shop.inter.RecyclerItemClickListener;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsFragment extends BaseFragment implements RecyclerItemClickListener, TextWatcher, IProductSelectionListener, ICartUpdateListener, IFavouritesUpdateListener {
    private RecyclerView mProductRecyclerView, mCategoryRecyclerView;
    Activity activity;
    TextView mNoData;
    SwipeRefreshLayout mSwipeRefreshLayout;
    int visibleItemCount = 0;
    int totalItemCount = 0;
    int pastVisiblesItems = 0;
    boolean loading = false;
    ArrayList<ProductResult> mListProduct = new ArrayList<>();
    ArrayList<CategoryListResult> mListCategory = new ArrayList<>();
    ProductAdapter mAdapter;
    CategoryAdapter mCategoryAdapter;
    EditText mEditSearch;
    ImageView mImageSearch, mImageClear;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        LocaleManager.setLocale(getActivity());
        initView(view);
        setToolBar(view);
        getCategoriesList();
        return view;
    }

    private void setToolBar(View view) {
        //setup toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((BaseActivity) getActivity()).setSupportActionBar(toolbar);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            Objects.requireNonNull(((BaseActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            Objects.requireNonNull(((BaseActivity) getActivity()).getSupportActionBar()).setDisplayShowHomeEnabled(true);
        }
        toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.white));
        Objects.requireNonNull(((BaseActivity) getActivity()).getSupportActionBar()).setTitle(getResources().getString(R.string.shop));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle arrow click
        if (item.getItemId() == android.R.id.home) {
            getActivity().finish(); // close this activity and return to preview activity (if there is any)
            return false;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initView(View view) {
        mNoData = view.findViewById(R.id.nodata);
        mEditSearch = view.findViewById(R.id.et_shop);
        mProductRecyclerView = view.findViewById(R.id.recyclerList);
        mCategoryRecyclerView = view.findViewById(R.id.recyclerCategoryList);
        mCategoryAdapter = new CategoryAdapter(getActivity(), mListCategory, this);
        mCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        mCategoryRecyclerView.setAdapter(mCategoryAdapter);
        mAdapter = new ProductAdapter(getActivity(), mListProduct, this);
        mProductRecyclerView.setPadding(10, 10, 10, 10);
        final GridLayoutManager mGrid = new GridLayoutManager(getActivity(), 2);
        mProductRecyclerView.setLayoutManager(mGrid);
        mProductRecyclerView.setHasFixedSize(true);
        mProductRecyclerView.setAdapter(mAdapter);
        mEditSearch = view.findViewById(R.id.et_shop);
        mEditSearch.addTextChangedListener(this);
        mImageSearch = view.findViewById(R.id.imageSearch);
        mImageClear = view.findViewById(R.id.imageClear);

        mSwipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mProductRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = mGrid.getChildCount();
                    totalItemCount = mGrid.getItemCount();
                    pastVisiblesItems = mGrid.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                        }
                    }
                }
            }
        });
        mImageClear.setOnClickListener(view1 -> mEditSearch.setText(null));
    }


    void getCategoriesList() {
        Utils.openProgressDialog(getContext());
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty(AppConstants.USER_ID, appPreference.getUserId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<CategoryListResponse> mService;
        mService = mApiService.getCategoryList(jsonObj);
        mService.enqueue(new Callback<CategoryListResponse>() {
            @Override
            public void onResponse(Call<CategoryListResponse> call, Response<CategoryListResponse> response) {
                Utils.closeProgressDialog();
                if (response.body() != null && response.body().getStatus().equals("401")) {
                    Utils.showSessionAlert(getActivity());
                    return;
                }
                assert response.body() != null;
                showCategories(response.body().getCategoryListResult());
            }

            @Override
            public void onFailure(Call<CategoryListResponse> call, Throwable t) {
                call.cancel();
                Utils.closeProgressDialog();
            }
        });
    }

    private void showCategories(List<CategoryListResult> mList) {
        mListCategory.clear();
        if (mList != null && mList.size() > 0) {
            mListCategory.addAll(mList);
        }
        mCategoryAdapter.notifyDataSetChanged();
        getProductList("");
    }

    private void showProducts(List<ProductResult> mList) {
        mListProduct.clear();
        if (mList != null && mList.size() > 0) {
            mNoData.setVisibility(View.GONE);
            mListProduct.addAll(mList);
        } else {
            mNoData.setVisibility(View.VISIBLE);
        }
        mAdapter.notifyDataSetChanged();
    }

    void getProductList(String category) {
        Utils.openProgressDialog(getContext());
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty(AppConstants.USER_ID, appPreference.getUserId());
        jsonObj.addProperty(AppConstants.CATEGORY_ID, category);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<ProductListResponse> mService;
        mService = mApiService.getProductList(jsonObj);
        mService.enqueue(new Callback<ProductListResponse>() {
            @Override
            public void onResponse(Call<ProductListResponse> call, Response<ProductListResponse> response) {
                Utils.closeProgressDialog();
                if (response.body() != null && response.body().getStatus().equals("401")) {
                    Utils.showSessionAlert(getActivity());
                    return;
                }
                assert response.body() != null;
                showProducts(response.body().getProductListResult());
            }

            @Override
            public void onFailure(Call<ProductListResponse> call, Throwable t) {
                call.cancel();
                Utils.closeProgressDialog();
            }
        });
    }

    @Override
    public void setClicks(int pos, int open) {
        getProductList(mListCategory.get(pos).getId());
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.length() != 0) {
            mImageClear.setVisibility(View.VISIBLE);
            mImageSearch.setVisibility(View.GONE);
        } else {
            mImageClear.setVisibility(View.GONE);
            mImageSearch.setVisibility(View.VISIBLE);
        }
        mAdapter.getFilter().filter(editable.toString());
    }

    @Override
    public void onProductSelected(ProductResult productResult) {
        if (getActivity() instanceof ShopActivity) {
            ((ShopActivity) getActivity()).setToolBar();
            ((ShopActivity) getActivity()).showToolBar();
            ((ShopActivity) getActivity()).setShowFrom(1);
            ((ShopActivity) getActivity()).loadFragment(ProductDetailFragment.newInstance(productResult));

        }

    }

    @Override
    public void onAddToCart(ProductResult productResult, int position) {
        if (getActivity() instanceof ShopActivity) {
            ((ShopActivity) getActivity()).addProductToCart(productResult, position, this);
        }
    }

    @Override
    public void onRemoveFromCart(ProductResult productResult, int position) {
        if (getActivity() instanceof ShopActivity) {
            ((ShopActivity) getActivity()).removeProductFromCart(productResult, position, this);
        }
    }

    @Override
    public void onAddToFavourite(ProductResult productResult, int position) {
        if (getActivity() instanceof ShopActivity) {
            ((ShopActivity) getActivity()).addToFavourite(productResult, position, this);
        }
    }

    @Override
    public void onRemoveFromFavourite(ProductResult productResult, int position) {
        if (getActivity() instanceof ShopActivity) {
            ((ShopActivity) getActivity()).removeFromFavourite(productResult, position, this);
        }
    }

    @Override
    public void onProductAddedToCart(ProductResult productResult, int position) {
        mAdapter.addProductToCart(position);
    }

    @Override
    public void onProductRemovedFromCart(ProductResult productResult, int position) {
        mAdapter.removeProductFromCart(position);
    }

    @Override
    public void onProductAddedToFavourite(ProductResult productResult, int position) {
        mAdapter.addProductToFavourite(position);
    }

    @Override
    public void onProductRemovedFromFavourite(ProductResult productResult, int position) {
        mAdapter.removeProductFromFavourite(position, false);
    }
}
