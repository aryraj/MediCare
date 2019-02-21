package com.inc.hope.medicare;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by aryan on 12/11/17.
 */

public class SaleBillFragment extends android.support.v4.app.Fragment {

    private EditText cust_name_ed;
    private EditText cust_email_ed;

    private FloatingActionButton fab;
    public ArrayAdapter<String> adapter;
    private DataBaseHelper myDB;
    private BillDatabaseHelper billDB;

    private String str_auto_prod_selected, str_custName, str_custEmail;
    private Button btn_empty, btn_cart;



    public SaleBillFragment(){
        // Required Empty Constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_sale_bill, container, false);
        perform(view);

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        cust_name_ed.setText("");
        cust_email_ed.setText("");
    }





    private void perform(View view) {


        cust_name_ed = (EditText) view.findViewById(R.id.cust_name_ed);
        cust_email_ed = (EditText) view.findViewById(R.id.cust_email_ed);

        fab = (FloatingActionButton) view.findViewById(R.id.add_prod_fab);
        myDB = new DataBaseHelper(getContext());
        billDB = new BillDatabaseHelper(getContext());

        btn_empty = (Button) view.findViewById(R.id.btn_empty);
        btn_cart = (Button) view.findViewById(R.id.btn_cart);

        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CartActivity.class);
                str_custName = cust_name_ed.getText().toString();
                str_custEmail = cust_email_ed.getText().toString();
                intent.putExtra("name", str_custName);
                intent.putExtra("email", str_custEmail);
                startActivity(intent);
            }
        });

        btn_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                billDB.deleteAll();
                Toast.makeText(getContext(), "Cart Emptied!", Toast.LENGTH_SHORT).show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewBill();
            }
        });

    }

    private void createNewBill() {


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        final View getItems = layoutInflater.inflate(R.layout.dialog_sale_bill, null);

        final AutoCompleteTextView ac_ed_prod = (AutoCompleteTextView) getItems.findViewById(R.id.auto_ed_prod);
        ArrayAdapter<String> auto_adapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);

        Cursor res = myDB.getAllData();

        if (res.moveToFirst()){
            do {
                String name = res.getString(res.getColumnIndex("NAME"));
                auto_adapter1.add(name);
            }while (res.moveToNext());
        }

        ac_ed_prod.setAdapter(auto_adapter1);
        ac_ed_prod.setThreshold(1);
        ac_ed_prod.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                str_auto_prod_selected = ((TextView)view).getText().toString();
                Cursor res2 = myDB.getQuantity(str_auto_prod_selected);
                int quantity = res2.getInt(0);
                Toast.makeText(getContext(), "In Stock: " + quantity, Toast.LENGTH_SHORT).show();

            }
        });


        alertDialog.setView(getItems);

        final EditText ed_qty = (EditText) getItems.findViewById(R.id.ed_qty);


        alertDialog
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String str_prod = ac_ed_prod.getText().toString().toUpperCase().trim();
                        String str_qty = ed_qty.getText().toString().trim();

                        Cursor cursor = myDB.getQuantity(str_prod);
                        int qty = cursor.getInt(0);
                        int new_qty = qty - Integer.parseInt(str_qty);
                        if (new_qty < 0){
                            Toast.makeText(getContext(), "Cannot add " + str_qty + "in cart. Only " + new_qty + " left in stock.", Toast.LENGTH_SHORT).show();
                        }else {
                            Boolean result = billDB.insertData(str_prod, str_qty);
                        }



                        if (str_qty.isEmpty() || str_prod.isEmpty()){
                            Toast.makeText(getContext(), "Please enter details!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getContext(), "Product added to cart", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create()
                .show();
    }




}
