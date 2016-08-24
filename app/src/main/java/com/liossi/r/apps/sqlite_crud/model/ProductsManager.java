package com.liossi.r.apps.sqlite_crud.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.liossi.r.apps.sqlite_crud.database.ProductCursorWrapper;
import com.liossi.r.apps.sqlite_crud.database.ProductDbSchema.ProductTable;
import com.liossi.r.apps.sqlite_crud.database.ShopBaseHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Rene on 21/06/2016.
 */
public class ProductsManager {
    private static ProductsManager mProductsManager;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private ProductsManager(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new ShopBaseHelper(mContext).getWritableDatabase();
    }
    public static ProductsManager get(Context context){
        if (mProductsManager == null) {
            mProductsManager = new ProductsManager(context);
        }
        return mProductsManager;
    }

    public List<Product> getProducts(){
        List<Product> products = new ArrayList<>();
        ProductCursorWrapper cursor = queryProducts(null, null);

        try{
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                products.add(cursor.getProduct());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return products;
    }

    public Product getProduct(UUID id) {
        ProductCursorWrapper cursor = queryProducts(ProductTable.Cols.UUID + " = ?", new String[]{String.valueOf(id)});

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();

            return cursor.getProduct();
        } finally {
            cursor.close();
        }
    }

    private ProductCursorWrapper queryProducts(String where, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                ProductTable.NAME,
                null,
                where,
                whereArgs,
                null,
                null,
                null
        );

        return new ProductCursorWrapper(cursor);
    }

    public void addProduct(Product product) {
        ContentValues values = getContentValues(product);
        mDatabase.insert(ProductTable.NAME, null, values);
    }

    public void updateProduct(Product product){
        String id = String.valueOf(product.getId());

        ContentValues values = getContentValues(product);
        mDatabase.update(ProductTable.NAME, values,
                ProductTable.Cols.UUID + " = ?",
                new String[]{id});
    }

    public void deleteProduct(Product product) {

        String id = String.valueOf(product.getId());

        mDatabase.delete(ProductTable.NAME,
                ProductTable.Cols.UUID + " = ?",
                new String[]{id});
    }

    private ContentValues getContentValues(Product product){
        ContentValues values = new ContentValues();
        values.put(ProductTable.Cols.UUID, String.valueOf(product.getId()));
        values.put(ProductTable.Cols.NAME, product.getName());
        values.put(ProductTable.Cols.DESCRIPTION, product.getDescription());
        values.put(ProductTable.Cols.PRICE, product.getPrice());
        values.put(ProductTable.Cols.PHOTO, product.getPhotoFilename());

        return values;
    }

    public File getPhotoFile(Product product) {
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, product.getPhotoFilename());
    }
}
