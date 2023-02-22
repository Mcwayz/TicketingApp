package com.example.ticketingpos.ticket.activity;

import static com.ftpos.library.smartpos.errcode.ErrCode.ERR_SUCCESS;
import static com.ftpos.library.smartpos.printer.AlignStyle.PRINT_STYLE_CENTER;
import static com.ftpos.library.smartpos.printer.AlignStyle.PRINT_STYLE_LEFT;
import static com.ftpos.library.smartpos.printer.AlignStyle.PRINT_STYLE_RIGHT;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ticketingpos.R;
import com.example.ticketingpos.ticket.adapter.TicketAdapter;
import com.example.ticketingpos.ticket.model.Ticket;
import com.ftpos.library.smartpos.printer.OnPrinterCallback;
import com.ftpos.library.smartpos.printer.PrintStatus;
import com.ftpos.library.smartpos.printer.Printer;
import com.ftpos.library.smartpos.servicemanager.OnServiceConnectCallback;
import com.ftpos.library.smartpos.servicemanager.ServiceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class MultipleTicketActivity extends AppCompatActivity {

    TicketAdapter ticketAdapter;
    RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ImageView imgQRC, imgBack;
    private Context mContext;

    private String paymentMethod,saveCurrentDate, saveCurrentTime;

    private static final String CHANNEL = "com.example.ticketingpos/printAction";
    private Printer printer;
    private static Paint paint = null;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_ticket);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        fab = (FloatingActionButton) findViewById(R.id.fab_print_mul);
        imgQRC = (ImageView) findViewById(R.id.img_hidden_qrc);
        recyclerView = findViewById(R.id.recyclerView1);
        imgBack = findViewById(R.id.img_back_mul);
        mContext = MultipleTicketActivity.this;

        paymentMethod = getIntent().getStringExtra("paymentMethod");

        imgBack.setOnClickListener((View view)->{
            Intent intent = new Intent(MultipleTicketActivity.this, MobileTicketActivity.class);
            startActivity(intent);
            finish();
        });



        try {
            ServiceManager.bindPosServer(MultipleTicketActivity.this, new OnServiceConnectCallback() {
                @Override
                public void onSuccess() {
                    printer = Printer.getInstance(mContext);
                }

                @Override
                public void onFail(int var1) {
                    Log.e("binding", "onFail");
                }
            });
        } catch (Exception e) {

        }




    }


    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        List<Ticket> ticketList = (List<Ticket>) intent.getSerializableExtra("tickets");

        ticketAdapter = new TicketAdapter(ticketList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(ticketAdapter);

        fab.setOnClickListener((View view)->{
            printMultipleTickets();
        });


    }


    void printMultipleTickets() {
//        Intent intent = getIntent();
//        List<Ticket> ticketList = (List<Ticket>) intent.getSerializableExtra("tickets");
//        Set<Ticket> set = new HashSet<>(ticketList);
//
//        set.forEach(ticket -> printReceipt(ticket));

        Intent intent = getIntent();
        List<Ticket> ticketList = (List<Ticket>) intent.getSerializableExtra("tickets");
        ticketList = ticketList.stream().distinct().collect(Collectors.toList());

        ticketList.forEach(ticket-> printReceipt(ticket));

    }

    void printReceipt(Ticket ticket) {

        int id = ticket.getId();
        String barcode = ticket.getBarcode();
        String ticketName = ticket.getTicketName();
        String ticketNumber = ticket.getTicketNumber();
        String children = ticket.getChildren();
        String adult = ticket.getAdult();
        double ticketPrice = ticket.getTotalPrice();
        MultiFormatWriter write = new MultiFormatWriter();
        try {
            BitMatrix matrix = write.encode(barcode, BarcodeFormat.QR_CODE, 350, 350);
            BarcodeEncoder encoder = new BarcodeEncoder();
            //Initialize bitmap
            Bitmap bitmap = encoder.createBitmap(matrix);
            imgQRC.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        try {
            int ret;
            ret = printer.open();
            if (ret != ERR_SUCCESS) {
                System.out.println("open failed" + String.format(" errCode = 0x%x\n", ret));
                Toast.makeText(MultipleTicketActivity.this, "open failed" + String.format(" errCode = 0x%x\n", ret), Toast.LENGTH_LONG).show();
                return;
            }

            ret = printer.startCaching();
            if (ret != ERR_SUCCESS) {
                System.out.println("startCaching failed" + String.format(" errCode = 0x%x\n", ret));
                Toast.makeText(MultipleTicketActivity.this, "startCaching failed" + String.format(" errCode = 0x%x\n", ret), Toast.LENGTH_LONG).show();
                return;
            }

            ret = printer.setGray(3);
            if (ret != ERR_SUCCESS) {
                System.out.println("startCaching failed" + String.format(" errCode = 0x%x\n", ret));
                Toast.makeText(MultipleTicketActivity.this, "startCaching failed" + String.format(" errCode = 0x%x\n", ret), Toast.LENGTH_LONG).show();
                return;
            }

            PrintStatus printStatus = new PrintStatus();
            ret = printer.getStatus(printStatus);
            if (ret != ERR_SUCCESS) {
                System.out.println("getStatus failed" + String.format(" errCode = 0x%x\n", ret));
                Toast.makeText(MultipleTicketActivity.this, "getStatus failed" + String.format(" errCode = 0x%x\n", ret), Toast.LENGTH_LONG).show();
                return;
            }

            System.out.println("Temperature = " + printStatus.getmTemperature() + "\n");
            System.out.println("Gray = " + printStatus.getmGray() + "\n");
            if (!printStatus.getmIsHavePaper()) {
                System.out.println("Printer out of paper\n");
                Toast.makeText(MultipleTicketActivity.this, "Printer Out of Paper", Toast.LENGTH_LONG).show();
                return;
            }

            System.out.println("IsHavePaper = true\n");

            printer.setAlignStyle(PRINT_STYLE_CENTER);
            Bitmap bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.logo);
            ret = printer.printBmp(bmp);
            printer.printStr("\n");

            printer.printStr("__________________________________\n");

            printer.setAlignStyle(PRINT_STYLE_CENTER);
            Bitmap qrocde = ((BitmapDrawable) imgQRC.getDrawable()).getBitmap();
            ret = printer.printBmp(qrocde);
            printer.printStr("\n");

            //Single line print left justified, right justified
            printer.setAlignStyle(PRINT_STYLE_LEFT);
            printer.printStr("Ticket ID");
            printer.setAlignStyle(PRINT_STYLE_RIGHT);
            printer.printStr(String.valueOf(id));
            printer.printStr("\n");

            //Single line print left justified, right justified
            printer.setAlignStyle(PRINT_STYLE_LEFT);
            printer.printStr("Ticket Type");
            printer.setAlignStyle(PRINT_STYLE_RIGHT);
            printer.printStr(ticketName);
            printer.printStr("\n");

            //Single line print left justified, right justified
            printer.setAlignStyle(PRINT_STYLE_LEFT);
            printer.printStr("Ticket Number");
            printer.setAlignStyle(PRINT_STYLE_RIGHT);
            printer.printStr(ticketNumber);
            printer.printStr("\n");

            printer.setAlignStyle(PRINT_STYLE_CENTER);
            printer.printStr("__________________________________");
            printer.printStr("\n");

            //Single line print left justified, right justified
            printer.setAlignStyle(PRINT_STYLE_LEFT);
            printer.printStr("Adult Slots");
            printer.setAlignStyle(PRINT_STYLE_RIGHT);
            printer.printStr(adult);
            printer.printStr("\n");

            //Single line print left justified, right justified
            printer.setAlignStyle(PRINT_STYLE_LEFT);
            printer.printStr("Children Slots");
            printer.setAlignStyle(PRINT_STYLE_RIGHT);
            printer.printStr(children);
            printer.printStr("\n");

            //Single line print left justified, right justified
            printer.setAlignStyle(PRINT_STYLE_LEFT);
            printer.printStr("Amount Paid");
            printer.setAlignStyle(PRINT_STYLE_RIGHT);
            printer.printStr("K"+" "+ticketPrice);
            printer.printStr("\n");

            //Single line print left justified, right justified
            printer.setAlignStyle(PRINT_STYLE_LEFT);
            printer.printStr("Payment Method");
            printer.setAlignStyle(PRINT_STYLE_RIGHT);
            printer.printStr(paymentMethod);
            printer.printStr("\n");

            printer.setAlignStyle(PRINT_STYLE_CENTER);
            printer.printStr("__________________________________");
            printer.printStr("\n");
            //Single line print left justified, right justified
            printer.setAlignStyle(PRINT_STYLE_LEFT);
            printer.printStr("Date");
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
            saveCurrentDate = currentDate.format(calendar.getTime());
            printer.setAlignStyle(PRINT_STYLE_RIGHT);
            printer.printStr(saveCurrentDate);
            printer.printStr("\n");

            //Single line print left justified, right justified
            printer.setAlignStyle(PRINT_STYLE_LEFT);
            printer.printStr("Time");
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            saveCurrentTime = currentTime.format(calendar.getTime());
            printer.setAlignStyle(PRINT_STYLE_RIGHT);
            printer.printStr(saveCurrentTime);
            printer.printStr("\n");

            printer.setAlignStyle(PRINT_STYLE_CENTER);
            printer.printStr("__________________________________\n");
            printer.printStr("Thank You");

            ret = printer.getUsedPaperLenManage();
            if (ret < 0) {
                System.out.println("getUsedPaperLenManage failed" + String.format(" errCode = 0x%x\n", ret));
                Toast.makeText(MultipleTicketActivity.this, "getUsedPaperLenManage failed" + String.format(" errCode = 0x%x\n", ret), Toast.LENGTH_LONG).show();
            }

            System.out.println("UsedPaperLenManage = " + ret + "mm \n");
            printer.print(new OnPrinterCallback() {
                @Override
                public void onSuccess() {
                    System.out.println("Print Success\n");
                    printer.feed(32);
                }

                @Override
                public void onError(int i) {

                    System.out.println("printBmp failed" + String.format(" errCode = 0x%x\n", i));
                    Toast.makeText(MultipleTicketActivity.this, "PrintBmp Failed" + String.format(" errCode = 0x%x\n", i), Toast.LENGTH_LONG).show();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Print Failed" + e.toString() + "\n");
            Toast.makeText(MultipleTicketActivity.this, "Print Failed" + e.toString(), Toast.LENGTH_LONG).show();
        }

    }


}