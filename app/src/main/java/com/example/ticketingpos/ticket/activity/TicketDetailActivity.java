package com.example.ticketingpos.ticket.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ticketingpos.MainActivity;
import com.example.ticketingpos.R;
import com.example.ticketingpos.ticket.model.MyDatabase;
import com.ftpos.library.smartpos.device.Device;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.ftpos.library.smartpos.printer.OnPrinterCallback;
import com.ftpos.library.smartpos.printer.PrintStatus;
import com.ftpos.library.smartpos.printer.Printer;
import com.ftpos.library.smartpos.servicemanager.ServiceManager;
import com.ftpos.library.smartpos.servicemanager.OnServiceConnectCallback;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import static com.ftpos.library.smartpos.device.Device.*;
import static com.ftpos.library.smartpos.errcode.ErrCode.ERR_SUCCESS;
import static com.ftpos.library.smartpos.printer.AlignStyle.PRINT_STYLE_CENTER;
import static com.ftpos.library.smartpos.printer.AlignStyle.PRINT_STYLE_LEFT;
import static com.ftpos.library.smartpos.printer.AlignStyle.PRINT_STYLE_RIGHT;
import static java.lang.Math.ceil;


public class TicketDetailActivity extends AppCompatActivity {
    private String ticketId, barcode, ticketType, phoneNo, ticketNumber, ticketPrice, childrenSlot, adultSlot, saveCurrentDate, saveCurrentTime, paymentMethod, print_no;
    private ImageView imgQRC, imgBack;
    private TextView tvTicketId, tvTicketType, tvPhoneNo, tvTicketNumber, tvAdultSlot, tvChildrenSlot, tvTotalPrice, tvPaymentMethod, tvDate;
    private static final String CHANNEL = "com.example.ticketingpos/printAction";
    private Printer printer;
    private static Paint paint = null;
    private Context mContext;
    private Button btnPrint;
    private String state;
    private String type = "";
    private Device device;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);
        ticketId = getIntent().getStringExtra("ticketId");
        barcode = getIntent().getStringExtra("barcode");
        ticketType = getIntent().getStringExtra("ticketType");
        phoneNo = getIntent().getStringExtra("phoneNo");
        ticketNumber = getIntent().getStringExtra("ticketNumber");
        ticketPrice = getIntent().getStringExtra("ticketPrice");
        childrenSlot = getIntent().getStringExtra("childrenSlot");
        adultSlot = getIntent().getStringExtra("adultSlot");
        paymentMethod = getIntent().getStringExtra("paymentMethod");
        state = getIntent().getStringExtra("status");
        print_no = getIntent().getStringExtra("print_no");

        /////////////////////////////////////////////////////////////////

        imgQRC = findViewById(R.id.logoQRC);
        tvTicketId = findViewById(R.id.tv_d_ticket_id);
        tvTicketType = findViewById(R.id.tv_d_ticket_type);
        tvTicketNumber = findViewById(R.id.tv_d_ticket_no);
        tvPhoneNo = findViewById(R.id.tv_d_phone_no);
        tvChildrenSlot = findViewById(R.id.tv_d_children_no);
        tvAdultSlot = findViewById(R.id.tv_d_adult_no);
        tvTotalPrice = findViewById(R.id.tv_d_ticket_price);
        tvPaymentMethod = findViewById(R.id.tv_d_payment_method);
        tvDate = findViewById(R.id.tv_d_date);
        imgBack = findViewById(R.id.img_back_ticket);
        btnPrint = findViewById(R.id.btn_print);
        mContext = TicketDetailActivity.this;

        /////////////////////////////////////////////////////////////////
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null)
        {
            if (bundle.containsKey("Cash"))
            {
                type = getIntent().getExtras().get("Cash").toString();
            }
            else if (bundle.containsKey("Card"))
            {
                type = getIntent().getExtras().get("Card").toString();
            }
            else if (bundle.containsKey("MNO"))
            {
                type = getIntent().getExtras().get("MNO").toString();
            }
            else if (bundle.containsKey("History")){
                type = getIntent().getExtras().get("History").toString();
            }

        }

        /////////////////////////////////////////////////////////////////

        try {
            ServiceManager.bindPosServer(TicketDetailActivity.this, new OnServiceConnectCallback() {
                @Override
                public void onSuccess() {
                    printer = Printer.getInstance(mContext);
                    String packageName = getApplicationContext().getPackageName();
                }

                @Override
                public void onFail(int var1) {
                    Log.e("binding", "onFail");
                }
            });
        } catch (Exception e) {

        }

        btnPrint.setOnClickListener(v ->
            printReceipt(tvTicketId.getText().toString(), tvTicketType.getText().toString(), tvTicketNumber.getText().toString(), ticketPrice, adultSlot, childrenSlot, paymentMethod, print_no));

        imgBack.setOnClickListener(v -> {
            Intent i = new Intent(TicketDetailActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        String id = String.valueOf(ticketId);
        tvTicketId.setText(id);
        tvTicketType.setText(ticketType);
        tvPhoneNo.setText(phoneNo);
        tvTicketNumber.setText(ticketNumber);
        tvChildrenSlot.setText(childrenSlot);
        tvAdultSlot.setText(adultSlot);
        tvTotalPrice.setText("K"+" "+ticketPrice);
        tvPaymentMethod.setText(paymentMethod);
        tvDate.setText(saveCurrentDate);
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
    }

    private static int getTextWidth(String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) ceil(widths[j]);
            }
        }
        return iRet;
    }

    void printReceipt(String ticketID, String ticketType, String ticketNumber, String ticketPrice, String childrenSlot, String adultSlot, String paymentMethod, String print_no) {
        try {
            int ret;
            ret = printer.open();
            if (ret != ERR_SUCCESS) {
                System.out.println("open failed" + String.format(" errCode = 0x%x\n", ret));
                Toast.makeText(TicketDetailActivity.this, "open failed" + String.format(" errCode = 0x%x\n", ret), Toast.LENGTH_LONG).show();
                return;
            }

            ret = printer.startCaching();
            if (ret != ERR_SUCCESS) {
                System.out.println("startCaching failed" + String.format(" errCode = 0x%x\n", ret));
                Toast.makeText(TicketDetailActivity.this, "startCaching failed" + String.format(" errCode = 0x%x\n", ret), Toast.LENGTH_LONG).show();
                return;
            }

            ret = printer.setGray(3);
            if (ret != ERR_SUCCESS) {
                System.out.println("startCaching failed" + String.format(" errCode = 0x%x\n", ret));
                Toast.makeText(TicketDetailActivity.this, "startCaching failed" + String.format(" errCode = 0x%x\n", ret), Toast.LENGTH_LONG).show();
                return;
            }

            PrintStatus printStatus = new PrintStatus();
            ret = printer.getStatus(printStatus);
            if (ret != ERR_SUCCESS) {
                System.out.println("getStatus failed" + String.format(" errCode = 0x%x\n", ret));
                Toast.makeText(TicketDetailActivity.this, "getStatus failed" + String.format(" errCode = 0x%x\n", ret), Toast.LENGTH_LONG).show();
                return;
            }

            System.out.println("Temperature = " + printStatus.getmTemperature() + "\n");
            System.out.println("Gray = " + printStatus.getmGray() + "\n");
            if (!printStatus.getmIsHavePaper()) {
                System.out.println("Printer out of paper\n");
                Toast.makeText(TicketDetailActivity.this, "Printer Out of Paper", Toast.LENGTH_LONG).show();
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
            printer.printStr(ticketID);

            printer.printStr("\n");

            //Single line print left justified, right justified
            printer.setAlignStyle(PRINT_STYLE_LEFT);
            printer.printStr("Ticket Type");
            printer.setAlignStyle(PRINT_STYLE_RIGHT);
            printer.printStr(ticketType);

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
            printer.printStr(childrenSlot);

            printer.printStr("\n");
            //Single line print left justified, right justified
            printer.setAlignStyle(PRINT_STYLE_LEFT);
            printer.printStr("Children Slots");
            printer.setAlignStyle(PRINT_STYLE_RIGHT);
            printer.printStr(adultSlot);
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

            //Single line print left justified, right justified
            printer.setAlignStyle(PRINT_STYLE_LEFT);
            printer.printStr("Device ID");
            String serialNumber = device.getSerialNumber();
            printer.setAlignStyle(PRINT_STYLE_RIGHT);
            printer.printStr(serialNumber);
            printer.printStr("\n");

            printer.setAlignStyle(PRINT_STYLE_CENTER);
            printer.printStr("__________________________________\n");
            printer.printStr(state+" Thank You");

            ret = printer.getUsedPaperLenManage();
            if (ret < 0) {
                System.out.println("getUsedPaperLenManage failed" + String.format(" errCode = 0x%x\n", ret));
                Toast.makeText(TicketDetailActivity.this, "getUsedPaperLenManage failed" + String.format(" errCode = 0x%x\n", ret), Toast.LENGTH_LONG).show();
                Intent dash = new Intent(TicketDetailActivity.this, HistoryActivity.class);
                startActivity(dash);
                finish();
            }

            System.out.println("UsedPaperLenManage = " + ret + "mm \n");

            printer.print(new OnPrinterCallback() {
                @Override
                public void onSuccess() {
                    System.out.println("Print Success\n");
                    printer.feed(32);
                    MyDatabase myDB = new MyDatabase(TicketDetailActivity.this);
                    myDB.updateTicket(ticketId);
                    Intent dash = new Intent(TicketDetailActivity.this, MainActivity.class);
                    startActivity(dash);
                    finish();
                }

                @Override
                public void onError(int i) {
                    System.out.println("printBmp failed" + String.format(" errCode = 0x%x\n", i));
                    Toast.makeText(TicketDetailActivity.this, "PrintBmp Failed" + String.format(" errCode = 0x%x\n", i), Toast.LENGTH_LONG).show();
                    Intent dash = new Intent(TicketDetailActivity.this, HistoryActivity.class);
                    startActivity(dash);
                    finish();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Print Failed" + e + "\n");
            Toast.makeText(TicketDetailActivity.this, "Print Failed" + e, Toast.LENGTH_LONG).show();
            Intent dash = new Intent(TicketDetailActivity.this, HistoryActivity.class);
            startActivity(dash);
            finish();
        }
    }
}