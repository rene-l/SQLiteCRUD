package com.liossi.r.apps.sqlite_crud;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

/**
 * Created by Rene on 21/06/2016.
 */
public class ProductDetailsActivity extends SingleFragmentActivity{
    private static String EXTRA_PRODUCT_ID = "com.liossi.r.apps.shopdemo.product_id";


    @Override
    public Fragment createFragment() {
        UUID productId = (UUID) getIntent().getSerializableExtra(EXTRA_PRODUCT_ID);
        return ProductDetailsFragment.newInstance(productId);
    }

    public static Intent newIntent(Context context, UUID productId){
        Intent intent = new Intent(context, ProductDetailsActivity.class);
        intent.putExtra(EXTRA_PRODUCT_ID, productId);

        return intent;
    }
}
