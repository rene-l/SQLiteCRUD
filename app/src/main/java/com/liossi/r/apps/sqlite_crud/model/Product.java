package com.liossi.r.apps.sqlite_crud.model;

import android.content.Context;

import com.liossi.r.apps.sqlite_crud.R;

import java.util.UUID;

/**
 * Created by Rene on 20/06/2016.
 */
public class Product {
    private UUID mUUID;
    private String mName;
    private String mDescription;
    private String mPrice;
    private String mPhoto;
    private Context mContext;

    public Product(UUID productId) {
        mUUID = productId;
    }

    public Product(Context context) {
        this(UUID.randomUUID());
        mContext = context;
        setName(mContext.getString(R.string.default_prod_name));
        setDescription(mContext.getString(R.string.default_prod_desc));
        setPrice("0.00");
    }

    public UUID getId() {
        return mUUID;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
            mPrice = price;
    }

    public String getPhotoFilename(){
        return "IMG_" + getId() + ".jpg";
    }

    public void setPhoto(String photoURI){
        mPhoto = photoURI;
    }
}
