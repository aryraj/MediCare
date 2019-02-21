package com.inc.hope.medicare;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CartActivity extends AppCompatActivity {

    private ListView mListView;
    private BillDatabaseHelper billDatabaseHelper;
    private DataBaseHelper mDB;
    private ArrayAdapter mAdapter;
    private TextView tv_name, tv_email, tv_total;
    private String str_name, str_email, name;
    private Button btn_save;
    private int total, quantity, initial_qty, final_qty, new_qty;
    private Cursor cur3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mListView = (ListView) findViewById(R.id.mListView);
        billDatabaseHelper = new BillDatabaseHelper(this);
        mDB = new DataBaseHelper(this);
        mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_total = (TextView) findViewById(R.id.tv_total);
        btn_save = (Button) findViewById(R.id.btn_save);

        str_name = getIntent().getExtras().getString("name");
        str_email = getIntent().getExtras().getString("email");

        tv_name.setText(str_name);
        tv_email.setText(str_email);

        addData();

        // For some reason the code written here have been written some where below too. And they seem to work only this way.
        // Unless you know what you are doing don't try to change them.

        // STARTING FROM HERE

        cur3 = billDatabaseHelper.getAllData("SELECT * FROM BILLING_TABLE");



        while (cur3.moveToNext()) {
            String name2 = cur3.getString(cur3.getColumnIndex("NAME"));

            initial_qty = mDB.getQuantity(name2).getInt(0);
            final_qty = cur3.getInt(cur3.getColumnIndex("QUANTITY"));

            new_qty = initial_qty - final_qty;
        }

        // UP TILL HERE


        // Summing the 'PRICE' column and saving the value to 'total'.
        final Cursor cursor = billDatabaseHelper.getAllData("Select sum(price) as total from Billing_Table");
        if (cursor.moveToFirst()){
            total = cursor.getInt(0);
            tv_total.setText("Rs. " + String.valueOf(total));

        }

        // Getting the root activity containing the cart for screenshot.
        final View rootView = this.getWindow().getDecorView().findViewById(android.R.id.content).getRootView();

       btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String path = store(rootView);
                    Boolean bol = mDB.updateData(name, new_qty);
                    shareImage(path, str_email);
                    Toast.makeText(CartActivity.this, "Left quantity of: " + name + " is: " + new_qty, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {

                }

            }
        });



    }

    @Override
    public void onResume(){
        super.onResume();

        billDatabaseHelper.deleteAll();


    }

    public void addData(){

        // This is what I was talking about

        Cursor cur = billDatabaseHelper.getAllData("SELECT * FROM BILLING_TABLE");


        while (cur.moveToNext()){
            name = cur.getString(cur.getColumnIndex("NAME"));

            quantity = cur.getInt(cur.getColumnIndex("QUANTITY"));
            int price = mDB.getPrice(name).getInt(0);

            int total = price * quantity;

           Boolean check = billDatabaseHelper.updatePrice(name, String.valueOf(quantity), String.valueOf(total));



            mAdapter.add("Name: " + name + "\t \t \t" + "Quantity: " + quantity + "\t \t \t" + "Net: Rs."+ total);
            mListView.setAdapter(mAdapter);

        }
        mAdapter.notifyDataSetChanged();


    }

    public static Bitmap getScreenShot(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return bitmap;
    }

    public static String store(View view) throws IOException {

        Bitmap bitmap = getScreenShot(view);
        String path = Environment.getExternalStorageDirectory().toString() + "/screenshot.jpg";

        FileOutputStream outputStream = new FileOutputStream(new File(path));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.flush();
        outputStream.close();
        bitmap.recycle();

        return path;
    }

    private void shareImage(String path, String to){

        String[] TO = {to};

        Uri uri = Uri.parse("file://" + path);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");


        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));

        intent.putExtra(Intent.EXTRA_EMAIL, TO);
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Medicare Purchase bill");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "Dear Mr./Mrs./Miss " + str_name + ". Please find the bill for your purchase at Medicare.");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            startActivity(Intent.createChooser(intent, "Share mail..."));
            finish();
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No App Available", Toast.LENGTH_SHORT).show();
        }

    }



}
