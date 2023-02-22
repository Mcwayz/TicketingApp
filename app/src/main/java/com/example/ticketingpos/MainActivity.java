package com.example.ticketingpos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.example.ticketingpos.ticket.activity.CashActivity;
import com.example.ticketingpos.ticket.activity.HistoryActivity;
import com.example.ticketingpos.ticket.activity.MobileTicketActivity;

public class MainActivity extends AppCompatActivity {

    private CardView cvMNO, cvHistory, cvCash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        cvMNO = (CardView) findViewById(R.id.cv_mno);
        cvCash = (CardView) findViewById(R.id.cv_cash);
        cvHistory = (CardView) findViewById(R.id.cv_history);

        cvMNO.setOnClickListener((View view)->{
            Intent momo = new Intent(MainActivity.this, MobileTicketActivity.class);
            startActivity(momo);
            finish();
        });

        cvHistory.setOnClickListener(v -> {
            Intent history = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(history);
            finish();
        });

        cvCash.setOnClickListener(v -> {
            Intent cash = new Intent(MainActivity.this, CashActivity.class);
            startActivity(cash);
            finish();
        });


    }
}