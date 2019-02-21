package com.inc.hope.medicare;

import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by aryan on 12/11/17.
 */

public class StockFragment extends android.support.v4.app.Fragment {

    private DataBaseHelper myDB;
    private ListView listView;
    private EditText inputSearch;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    private FloatingActionButton fab;
    private LinearLayout linearLayout;
    private String str_prod_name;

    public StockFragment(){
        // Required empty constructor for fragment
    }

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        // Add the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stock, container, false);
        perform(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    public void perform(View view) {


        myDB = new DataBaseHelper(getContext());
        listView = (ListView) view.findViewById(R.id.mListView);
        inputSearch = (EditText) view.findViewById(R.id.inputSearch);
        fab = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);


        // Getting all the data from the database as a cursor
        Cursor cur = myDB.getAllData();

        // Move the cursor to the first item in the database
        if (cur.moveToFirst()) {
            do {
                String name = cur.getString(cur.getColumnIndex("NAME"));
                int quantity = cur.getInt(cur.getColumnIndex("QUANTITY"));
                arrayList.add("Name: " + name + "\n" + "Quantity: " + quantity);
            } while (cur.moveToNext()); // Check if there's more than one item in the database
        }
        adapter.notifyDataSetChanged();




        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewStock();
            }
        });

        // Searching the list view for required product
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String text = inputSearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.getFilter().filter(text);
            }
        });



    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int pos = info.position;
        String str_prod_name_raw = (String) listView.getItemAtPosition(pos);
        String[] words = str_prod_name_raw.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            // You may want to check for a non-word character before blindly
            // performing a replacement
            // It may also be necessary to adjust the character class
            words[i] = words[i].replaceAll("[^\\w]", "");
        }

        str_prod_name = words[1];

        menu.setHeaderTitle("Select an Action for " + str_prod_name);
        menu.add(0, v.getId(), 0, "Delete");
        menu.add(0, v.getId(), 0, "Update");
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {

        if (menuItem.getTitle() == "Delete") {

            myDB.deleteSingle(str_prod_name);
            Toast.makeText(getContext(), str_prod_name + " deleted!", Toast.LENGTH_SHORT).show();
        } else if (menuItem.getTitle() == "Update") {
            Toast.makeText(getContext(), "Update is pressed!", Toast.LENGTH_SHORT).show();
        }


        return true;
    }


    private void addNewStock() {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View getListItemView = layoutInflater.inflate(R.layout.dialog_stock, null);
        linearLayout = (LinearLayout) getListItemView.findViewById(R.id.linearLayout);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setView(getListItemView);
        final ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1);


        final AutoCompleteTextView product = (AutoCompleteTextView) getListItemView.findViewById(R.id.autoCompleteTextView3);
        product.setAdapter(autoCompleteAdapter);
        product.setThreshold(1);

        final EditText ed_quantity = (EditText) getListItemView.findViewById(R.id.editText);
        final EditText ed_mrp = (EditText) getListItemView.findViewById(R.id.editText3);
        final EditText ed_exp = (EditText) getListItemView.findViewById(R.id.editText9);

        alertDialog
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final String str_product = product.getText().toString().toUpperCase().trim();
                        final String str_mrp = ed_mrp.getText().toString().toUpperCase().trim();
                        final String str_quantity = ed_quantity.getText().toString().toUpperCase().trim();
                        final String str_expiry = ed_exp.getText().toString().toUpperCase().trim();

                        Boolean result = myDB.insertData(str_product, str_mrp, str_expiry, str_quantity);
                        if (result){
                            Toast.makeText(getContext(), "Data succesfully inserted.", Toast.LENGTH_SHORT).show();


                        }else {
                            Toast.makeText(getContext(), "Not inserted", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create()
                .show();


    }







}
