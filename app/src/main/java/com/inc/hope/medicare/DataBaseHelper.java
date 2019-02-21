package com.inc.hope.medicare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rashi on 12/11/17.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "med.db" ;
    public static final String TABLE_NAME = "Product_table";


    public static final String COL_1 = "NAME";
    public static final String COL_2 = "PRICE";
    public static final String COL_3 = "EXPIRY";
    public static final String COL_4 = "QUANTITY";
    public static Cursor price, quantity;


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (NAME TEXT, PRICE INTEGER, EXPIRY INTEGER, QUANTITY INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

    }

    public boolean insertData(String name, String price, String expiry, String quantity){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, name);
        contentValues.put(COL_2, price);
        contentValues.put(COL_3, expiry);
        contentValues.put(COL_4, quantity);
        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();

        return result != -1;
    }

    public boolean updateData(String name, int quantity){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("QUANTITY", quantity);
        String where = "NAME=?";
        String[] whereArgs = new String[] {name};
        long result = db.update(TABLE_NAME, contentValues, where, whereArgs);

        return result != -1;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + TABLE_NAME, null);
        return res;
    }

    public Cursor getPrice(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        if (name !=null) {
             price = db.rawQuery("SELECT PRICE FROM " + TABLE_NAME + " WHERE NAME = ?", new String[]{name}, null);
        }
        if (price != null){
            price.moveToFirst();
        }
        return price;
    }

    public Cursor getQuantity(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        if (name != null){
            quantity = db.rawQuery("SELECT QUANTITY FROM " + TABLE_NAME + " WHERE NAME = ?", new String[] {name}, null);
        }
        if (quantity != null){
            quantity.moveToFirst();
        }

        return quantity;
    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

    public void deleteSingle(String name){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE NAME = " + "'" + name + "'");
    }
}
