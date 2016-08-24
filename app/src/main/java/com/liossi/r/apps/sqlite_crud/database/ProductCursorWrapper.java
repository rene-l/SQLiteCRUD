package com.liossi.r.apps.sqlite_crud.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.liossi.r.apps.sqlite_crud.database.ProductDbSchema.ProductTable;
import com.liossi.r.apps.sqlite_crud.model.Product;

import java.util.UUID;

/**
 * Created by Rene on 21/06/2016.
 */
public class ProductCursorWrapper  extends CursorWrapper{
    public ProductCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Product getProduct(){
        String uuidString = getString(getColumnIndex(ProductTable.Cols.UUID.toString()));
        String name = getString(getColumnIndex(ProductTable.Cols.NAME));
        String description = getString(getColumnIndex(ProductTable.Cols.DESCRIPTION));
        String price = getString(getColumnIndex(ProductTable.Cols.PRICE));
        String photo = getString(getColumnIndex(ProductTable.Cols.PHOTO));

        Product p = new Product(UUID.fromString(uuidString));
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setPhoto(photo);

        return p;
    }
}
