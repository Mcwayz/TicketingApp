package com.example.ticketingpos.ticket.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ticketingpos.MainActivity;
import com.example.ticketingpos.R;
import com.example.ticketingpos.ticket.adapter.CustomAdapter;
import com.example.ticketingpos.ticket.model.MyDatabase;
import com.example.ticketingpos.ticket.restapi.RecyclerViewInterface;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity implements RecyclerViewInterface{
    private RecyclerView recyclerView;
    private MyDatabase myDB;
    private ArrayList<String> txn_id, ticket_id, barcode, ticket_type, ticket_number, children, adult, price, payment, date, status, print_no;
    private ImageView imgBack;
    private CustomAdapter customAdapter;
    private EditText searchText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        recyclerView = findViewById(R.id.recyclerView);
        searchText = findViewById(R.id.search_input);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        imgBack = findViewById(R.id.img_back_his);
        myDB = new MyDatabase(HistoryActivity.this);
        txn_id = new ArrayList<>();
        ticket_id = new ArrayList<>();
        barcode = new ArrayList<>();
        ticket_type = new ArrayList<>();
        ticket_number = new ArrayList<>();
        children = new ArrayList<>();
        adult = new ArrayList<>();
        price = new ArrayList<>();
        date = new ArrayList<>();
        payment = new ArrayList<>();
        status = new ArrayList<>();
        print_no = new ArrayList<>();
        storeDataInArrays();
        imgBack.setOnClickListener((View view) -> {
            Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                storeDataInArrays();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                storeDataInArrays();
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                searchDataInArrays(text);
            }
        });
    }

    void storeDataInArrays(){
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No Transactions History Found!", Toast.LENGTH_SHORT).show();
        }else{
            while(cursor.moveToNext()){
                txn_id.add(cursor.getString(0));
                ticket_id.add(cursor.getString(1));
                barcode.add(cursor.getString(2));
                ticket_type.add(cursor.getString(3));
                ticket_number.add(cursor.getString(4));
                children.add(cursor.getString(5));
                adult.add(cursor.getString(6));
                price.add(cursor.getString(7));
                payment.add(cursor.getString(8));
                date.add(cursor.getString(9));
                print_no.add(String.valueOf(cursor.getInt(10)));
                status.add(cursor.getString(11));
            }
            customAdapter = new CustomAdapter(HistoryActivity.this, txn_id, ticket_id, barcode, ticket_type, ticket_number,
                    children, adult,price, date, this);
            recyclerView.setAdapter(customAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
        }
    }

    void searchDataInArrays(String text){
        Cursor cursor = myDB.searchData(text);
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No Transactions History Found!", Toast.LENGTH_SHORT).show();
        }else{
            while(cursor.moveToNext()){
                txn_id.add(cursor.getString(0));
                ticket_id.add(cursor.getString(1));
                barcode.add(cursor.getString(2));
                ticket_type.add(cursor.getString(3));
                ticket_number.add(cursor.getString(4));
                children.add(cursor.getString(5));
                adult.add(cursor.getString(6));
                price.add(cursor.getString(7));
                payment.add(cursor.getString(8));
                date.add(cursor.getString(9));
                print_no.add(String.valueOf(cursor.getInt(10)));
                status.add(cursor.getString(11));
            }
            customAdapter = new CustomAdapter(HistoryActivity.this, txn_id, ticket_id, barcode, ticket_type, ticket_number,
                    children, adult,price, date, this);
            recyclerView.setAdapter(customAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(HistoryActivity.this, TicketDetailActivity.class);
        intent.putExtra("ticketId", String.valueOf(ticket_id.get(position)));
        intent.putExtra("barcode", String.valueOf(barcode.get(position)));
        intent.putExtra("ticketType", String.valueOf(ticket_type.get(position)));
        intent.putExtra("ticketNumber", String.valueOf(ticket_number.get(position)));
        intent.putExtra("ticketPrice", String.valueOf(price.get(position)));
        intent.putExtra("childrenSlot", String.valueOf(children.get(position)));
        intent.putExtra("adultSlot", String.valueOf(adult.get(position)));
        intent.putExtra("paymentMethod", String.valueOf(payment.get(position)));
        intent.putExtra("Print_no", String.valueOf(print_no.get(position)));
        intent.putExtra("status", "Reprint");
        intent.putExtra("History","History");
        startActivity(intent);
        finish();
    }
}