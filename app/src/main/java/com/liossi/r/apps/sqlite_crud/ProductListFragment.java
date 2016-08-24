package com.liossi.r.apps.sqlite_crud;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.liossi.r.apps.sqlite_crud.model.Product;
import com.liossi.r.apps.sqlite_crud.model.ProductsManager;
import com.liossi.r.apps.sqlite_crud.utils.PictureUtils;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rene on 20/06/2016.
 */
public class ProductListFragment extends Fragment implements ActionMode.Callback {
    private static String TAG = "ProductListFragment";
    private RecyclerView mRecyclerView;
    private ProductsManager mProductsManager;
    private ProductsAdapter mProductsAdapter;
    private ActionMode mActionMode;
    private MultiSelector mMultiSelector;
    private NumberFormat mNumberFormat;


    public static ProductListFragment newInstance(){
        return new ProductListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProductsManager = ProductsManager.get(getContext());
        mMultiSelector = new MultiSelector();
        mNumberFormat = NumberFormat.getCurrencyInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_list_fragment, container, false);

        Drawable divider = getResources().getDrawable(R.drawable.product_list_divider);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_add_product);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product product = new Product(getContext());
                mProductsManager.addProduct(product);

                Intent intent = ProductEditActivity.newIntent(getContext(), product.getId());
                startActivity(intent);
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new ProductItemDecoration(divider));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapter();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }

    public void setAdapter(){
        List<Product> products = mProductsManager.getProducts();

        if (mProductsAdapter == null) {
            mProductsAdapter = new ProductsAdapter(products);
            mRecyclerView.setAdapter(mProductsAdapter);
        } else {
            mProductsAdapter.setProducts(products);
            mProductsAdapter.notifyDataSetChanged();
        }

    }

    private class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private Product mProduct;
        private File mPhotoFile;
        private TextView mNameTextView;
        private TextView mPriceTextView;
        private ImageView mPhotoImageView;

        public ProductHolder(View itemView){
            super(itemView);

            mNameTextView = (TextView) itemView.findViewById(R.id.item_description_text_view);
            mPriceTextView = (TextView) itemView.findViewById(R.id.item_price_text_view);
            mPhotoImageView = (ImageView) itemView.findViewById(R.id.item_image_view);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bindProduct(Product product){
            mProduct = product;
            mPhotoFile = mProductsManager.getPhotoFile(mProduct);

            mNameTextView.setText(product.getName());
            mPriceTextView.setText(mNumberFormat.format(Double.valueOf(product.getPrice())));

            if (mPhotoFile == null && !mPhotoFile.exists()) {
                mPhotoImageView.setImageDrawable(null);
            } else {
                Bitmap bmp = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
                mPhotoImageView.setImageBitmap(bmp);
            }

            boolean isActivated = mMultiSelector.isSelected(getAdapterPosition());
            itemView.setActivated(isActivated);
        }

        @Override
        public void onClick(View v) {
            if (mMultiSelector.isInChoiceMode()) {
                if (v.isActivated()) {
                    v.setActivated(false);
                    mMultiSelector.setSelected(this, false);
                    if (mMultiSelector.getActiveItems().size() == 0) {
                        mActionMode.finish();
                    }
                } else {
                    v.setActivated(true);
                    mMultiSelector.setSelected(this, true);
                }
                mActionMode.setTitle("(" + String.valueOf(mMultiSelector.getActiveItems().size()) + ")");

                if (mMultiSelector.getActiveItems().size() > 1) {
                    toggleEditMenuItem(false);
                } else {
                    toggleEditMenuItem(true);
                }
            } else{
                Intent intent = ProductDetailsActivity.newIntent(getContext(), mProduct.getId());
                startActivity(intent);
            }
        }

        private void toggleEditMenuItem(boolean visible) {
            Menu menu = mActionMode.getMenu();
            MenuItem item = menu.findItem(R.id.item_menu_edit);
            item.setVisible(visible);
        }

        @Override
        public boolean onLongClick(View v) {
            if (!mMultiSelector.isInChoiceMode()) {
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                mActionMode = activity.startSupportActionMode(ProductListFragment.this);

                v.setActivated(true);
                mMultiSelector.setSelected(this, true);
                mActionMode.setTitle("(" + String.valueOf(mMultiSelector.getActiveItems().size()) + ")");
            }

            return true;
        }
    }

    private class ProductsAdapter extends RecyclerView.Adapter<ProductHolder> {
        private List<Product> mProducts = new ArrayList<>();

        public ProductsAdapter(List<Product> products){
            mProducts = products;
        }

        public void deleteItems(int[] selected){
            for (int i = 0; i < selected.length; i++) {
                mProductsManager.deleteProduct(mProducts.get(selected[i]));
            }
        }

        public Product getProductForPosition(int pos){
            Product product = mProducts.get(pos);
            return product;
        }

        @Override
        public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.product_list_item, parent, false);

            return new ProductHolder(view);
        }

        @Override
        public void onBindViewHolder(ProductHolder holder, int position) {
            holder.bindProduct(mProducts.get(position));
        }

        @Override
        public int getItemCount() {
            return mProducts.size();
        }

        public void setProducts(List<Product> products){
            mProducts = products;
        }
    }

    private class ProductItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public ProductItemDecoration(Drawable divider){
            mDivider = divider;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) == 0) {
                return;
            }
            outRect.top = mDivider.getIntrinsicHeight();
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int dividerLeft = parent.getPaddingLeft();
            int dividerRight = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount - 1; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int dividerTop = child.getBottom() + params.bottomMargin;
                int dividerBottom = dividerTop + mDivider.getIntrinsicHeight();

                mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
                mDivider.draw(c);
            }
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        int[] selected = mMultiSelector.getActiveKeys();

        switch (item.getItemId()) {
            case R.id.item_menu_delete:
                mProductsAdapter.deleteItems(selected);
                break;
            case R.id.item_menu_edit:
                Product product = mProductsAdapter.getProductForPosition(selected[0]);
                Intent intent = ProductEditActivity.newIntent(getContext(), product.getId());
                startActivity(intent);
                break;
        }
        mode.finish();
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mMultiSelector.clearSelected();
        setAdapter();
    }


}
