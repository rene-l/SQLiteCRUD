package com.liossi.r.apps.sqlite_crud;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.liossi.r.apps.sqlite_crud.model.Product;
import com.liossi.r.apps.sqlite_crud.model.ProductsManager;
import com.liossi.r.apps.sqlite_crud.utils.PictureUtils;

import java.io.File;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rene on 23/06/2016.
 */
public class ProductEditFragment extends Fragment {
    private static String TAG = "ProductEditFragment";
    private static int REQUEST_PHOTO = 1;
    private static String ARG_PRODUCT_ID = "product_id";

    private ProductsManager mProductsManager;
    private Product mProduct;
    private FloatingActionButton mFabTakePhoto;
    private EditText mProductPriceEditText;
    private EditText mProductNameEditText;
    private EditText mProductDescEditText;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    public static ProductEditFragment newInstance(UUID productId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT_ID, productId);
        ProductEditFragment fragment = new ProductEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID productId = (UUID)getArguments().getSerializable(ARG_PRODUCT_ID);

        mProductsManager = ProductsManager.get(getActivity());
        mProduct = mProductsManager.getProduct(productId);
        mPhotoFile = mProductsManager.getPhotoFile(mProduct);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_edit_fragment, container, false);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbarLayout.setTitle(mProduct.getName());

        mProductDescEditText = (EditText) view.findViewById(R.id.product_description_edittext);
        mProductDescEditText.setText(mProduct.getDescription());
        mProductDescEditText.addTextChangedListener(new EditTextListener());

        mProductNameEditText = (EditText) view.findViewById(R.id.product_name_edittext);
        mProductNameEditText.setText(mProduct.getName());
        mProductNameEditText.addTextChangedListener(new EditTextListener());

        mProductPriceEditText = (EditText) view.findViewById(R.id.product_price_edittext);
        mProductPriceEditText.setText(mProduct.getPrice());
        mProductPriceEditText.setFilters(new InputFilter[]{new DecimalInputFilter()});
        mProductPriceEditText.addTextChangedListener(new EditTextPriceListener());

        mPhotoView = (ImageView) view.findViewById(R.id.product_pic_imageview);

        mFabTakePhoto = (FloatingActionButton) view.findViewById(R.id.fab_take_photo);

        PackageManager pm = getActivity().getPackageManager();

        updatePhotoView();

        final Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null && captureImageIntent.resolveActivity(pm) != null;

        mFabTakePhoto.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }


        mFabTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImageIntent, REQUEST_PHOTO);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_PHOTO) {
            updatePhotoView();
        }
    }

    private class EditTextPriceListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            updateProduct(mProduct);

        }
    }


    private class EditTextListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            updateProduct(mProduct);
        }
    }

    private void updatePhotoView(){
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private void updateProduct(Product product){
        product.setName(mProductNameEditText.getText().toString());
        product.setPrice(mProductPriceEditText.getText().toString());
        product.setDescription(mProductDescEditText.getText().toString());
        mCollapsingToolbarLayout.setTitle(product.getName());

        mProductsManager.updateProduct(product);
    }

    class DecimalInputFilter implements InputFilter {
        private Pattern mPattern;

        public DecimalInputFilter(){
            String regex = "\\d+(\\.[\\d]{0,2})?";
            mPattern = Pattern.compile(regex);
        }
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher matcher = mPattern.matcher(dest.subSequence(0, dstart) + "" + source.subSequence(start, end)+ "" + dest.subSequence(dend, dest.length()));

            if (!matcher.matches()) {
                return "";
            }

            return null;
        }
    }

}
