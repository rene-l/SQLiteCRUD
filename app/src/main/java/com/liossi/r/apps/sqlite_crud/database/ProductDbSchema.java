package com.liossi.r.apps.sqlite_crud.database;

/**
 * Created by Rene on 21/06/2016.
 */
public class ProductDbSchema {
    public static final class ProductTable {
        public static final String  NAME = "products";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String PRICE = "price";
            public static final String DESCRIPTION = "description";
            public static final String PHOTO = "photo";
        }
    }
}
