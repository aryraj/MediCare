package com.inc.hope.medicare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by aryan on 12/11/17.
 */

public class BillDatabaseHelper extends SQLiteOpenHelper {



    public static final String DATABASE_NAME = "billing.db";
    public static final String TABLE_NAME = "Billing_table";

    public static final String COL_1 = "NAME";
    public static final String COL_2 = "PRICE";
    public static final String COL_3 = "QUANTITY";

    public  BillDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " (NAME TEXT, PRICE INTEGER, EXPIRY INTEGER, QUANTITY INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public boolean insertData(String name, String quantity){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, name);
        contentValues.put(COL_3, quantity);
        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();

        return result != -1;
    }

    public boolean updatePrice(String name, String quantity, String price){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("PRICE", price);
        String where = "NAME=? AND QUANTITY=?";
        String[] whereArgs = new String[] {name,quantity};
        long result = db.update(TABLE_NAME, contentValues, where, whereArgs);
        db.close();

        return result != -1;
    }



    public Cursor getAllData(String sql) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase database = getReadableDatabase();


        return database.rawQuery(sql,null);
    }

    public Cursor sumAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("Select (select sum(PRICE) from Billing_Table ) total\n" +
                "from Billing_Table", null);
        return cur;
    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }
}
