package com.liossi.r.apps.sqlite_crud;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.liossi.r.apps.sqlite_crud.model.Product;
import com.liossi.r.apps.sqlite_crud.model.ProductsManager;
import com.liossi.r.apps.sqlite_crud.utils.PictureUtils;

import java.io.File;
import java.text.NumberFormat;
import java.util.UUID;

/**
 * Created by Rene on 21/06/2016.
 */
public class ProductDetailsFragment extends Fragment {
    private static String ARG_PRODUCT_ID = "productId";
    private static String TAG = "ProductDetailsFragment";
    private ProductsManager mProductsManager;
    private Product mProduct;
    private TextView mPriceTextView;
    private TextView mDescriptionTextView;
    private ImageView mPhotoImageView;
    private File mPhotoFile;
    private NumberFormat mNumberFormat;

    public static ProductDetailsFragment newInstance(UUID productId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT_ID, productId);

        ProductDetailsFragment fragment = new ProductDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID productId = (UUID) getArguments().get(ARG_PRODUCT_ID);
        mProductsManager = ProductsManager.get(getContext());
        mProduct = mProductsManager.getProduct(productId);
        mPhotoFile = mProductsManager.getPhotoFile(mProduct);
        mNumberFormat = NumberFormat.getCurrencyInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_fragment, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.product_toolbar);
        toolbar.setTitle(mProduct.getName());

        mPriceTextView = (TextView) view.findViewById(R.id.price_textview);
        mDescriptionTextView = (TextView) view.findViewById(R.id.description_textview);
        mPhotoImageView = (ImageView) view.findViewById(R.id.product_photo_imageview);

        updateUI(mProduct);

        return view;
    }

    private void updateUI(Product product){

        mPriceTextView.setText(mNumberFormat.format(Double.valueOf(product.getPrice())));
        mDescriptionTextView.setText(product.getDescription());

        if (mPhotoFile != null && mPhotoFile.exists()) {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoImageView.setImageBitmap(bitmap);
        }
    }


}
