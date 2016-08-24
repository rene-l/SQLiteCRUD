package com.liossi.r.apps.sqlite_crud;

import android.support.v4.app.Fragment;

public class ProductListActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return ProductListFragment.newInstance();
    }
}
