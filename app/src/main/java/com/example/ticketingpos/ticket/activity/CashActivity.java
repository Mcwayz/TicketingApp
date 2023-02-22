package com.example.ticketingpos.ticket.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ticketingpos.MainActivity;
import com.example.ticketingpos.R;
import com.example.ticketingpos.service.ApiService;
import com.example.ticketingpos.ticket.model.MyDatabase;
import com.example.ticketingpos.ticket.model.PriceRequest;
import com.example.ticketingpos.ticket.model.Ticket;
import com.example.ticketingpos.ticket.model.TicketRequest;
import com.example.ticketingpos.ticket.model.TicketResponse;
import com.example.ticketingpos.ticket.model.TicketType;
import com.example.ticketingpos.ticket.restapi.TicketInterface;
import com.google.android.material.textfield.TextInputEditText;
import com.rey.material.widget.CheckBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class CashActivity extends AppCompatActivity {
    private String ticketType, adultNo, childNo;
    private int ticketTypeId;
    private TextInputEditText tfAdultNo,tfChildrenNo;
    private android.widget.TextView totalPrice;
    private ImageView imgBack;
    private AutoCompleteTextView tfTicketType;
    private ArrayList<String> getTicketType = new ArrayList<>();
    private CheckBox chkMultiple;
    private Button btnPrice, btnPurchase;
    Dialog dialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        dialog = new Dialog(CashActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wait2);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        totalPrice = findViewById(R.id.tv_total_price_csh);
        imgBack = findViewById(R.id.img_back_cash);
        tfAdultNo = findViewById(R.id.tf_adult_no_csh);
        tfChildrenNo = findViewById(R.id.tf_child_no_csh);
        tfTicketType = findViewById(R.id.tf_ticket_type_csh);
        btnPrice = findViewById(R.id.btn_get_price_csh);
        chkMultiple = findViewById(R.id.chk_multiple_csh);
        btnPurchase = findViewById(R.id.btn_purchase_csh);
        btnPrice.setOnClickListener((View view)->{validateInput();});
        btnPurchase.setOnClickListener((View view)->{
            validatePurchaseInput();
        });
        btnPurchase.setEnabled(false);

        imgBack.setOnClickListener((View view)->{
            Intent intent = new Intent(CashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        getTicketType();
    }

    private void validatePurchaseInput() {

        ticketType = tfTicketType.getText().toString();
        adultNo = tfAdultNo.getText().toString();
        childNo = tfChildrenNo.getText().toString();

        if (TextUtils.isEmpty(ticketType))
        {
            Toast.makeText(this, "Please Select Ticket Type", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(adultNo) && TextUtils.isEmpty(childNo))
        {
            Toast.makeText(this, "Please Enter Adult or Children Number", Toast.LENGTH_SHORT).show();
        }
        else if(childNo.equals("0") && adultNo.equals("0"))
        {
            Toast.makeText(this, "Please Enter Adult or Children Number", Toast.LENGTH_SHORT).show();
        }
        else
        {
            buyTicket(getTicket());
            dialog = new Dialog(CashActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_wait2);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }


    private void buyTicket(TicketRequest ticketRequest) {
        Call<TicketResponse> call = ApiService.getTicketApiService().getTicket(ticketRequest);
        call.enqueue(new Callback<TicketResponse>() {
            @Override
            public void onResponse(Call<TicketResponse> call, Response<TicketResponse> response) {
                if (response.isSuccessful()) {
                    TicketResponse ticketResponse = response.body();
                    List<Ticket> tickets = ticketResponse.getTicket();
                    // Iterate through the list of tickets and log each one

                    for (Ticket ticket : tickets) {
                        Log.d("TicketResponse", "Ticket ID: " + ticket.getId() + ", Barcode: " + ticket.getBarcode() + ", Ticket Type: " + ticket.getTicketName() + ", Ticket Number: " + ticket.getTicketNumber());
                        int ticketId = ticket.getId();
                        String barcode = ticket.getBarcode();
                        String ticketType = ticket.getTicketName();
                        String ticketNumber = ticket.getTicketNumber();
                        String price = totalPrice.getText().toString();
                        String childrenSlot = ticket.getChildren();
                        String adultSlot = ticket.getAdult();
                        String status = "First Print";
                        int print_no = 1;
                        String method = "Cash";
                        //
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        Date date = new Date();
                        String new_date = formatter.format(date);
                        MyDatabase myDB = new MyDatabase(CashActivity.this);
                        myDB.saveTransaction(String.valueOf(ticketId), barcode,  ticketType, ticketNumber,Integer.parseInt(childrenSlot), Integer.parseInt(adultSlot), price, method, new_date, print_no, status);
                        //
                        Intent intent = new Intent(CashActivity.this, TicketDetailActivity.class);
                        intent.putExtra("ticketId", String.valueOf(ticketId));
                        intent.putExtra("barcode", barcode);
                        intent.putExtra("ticketType", ticketType);
                        intent.putExtra("ticketNumber", ticketNumber);
                        intent.putExtra("ticketPrice", price);
                        intent.putExtra("childrenSlot", childrenSlot);
                        intent.putExtra("adultSlot", adultSlot);
                        intent.putExtra("paymentMethod", method);
                        intent.putExtra("print_no", print_no);
                        intent.putExtra("status", status);
                        startActivity(intent);
                        finish();
                    }
                    Toast.makeText(CashActivity.this, "Ticket Purchased", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    // Handle error response
                    Toast.makeText(CashActivity.this, "Request Failed", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<TicketResponse> call, Throwable t) {
                Toast.makeText(CashActivity.this, "Request Failed" +t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }


    private TicketRequest getTicket() {
        int adult, child;
        TicketRequest purchaseRequest = new TicketRequest();
        if(Objects.requireNonNull(tfAdultNo.getText()).toString().equals("")) {
            adult = 0;
            purchaseRequest.setEmail("");
            purchaseRequest.setPhoneNumber("");
            child = Integer.parseInt(tfChildrenNo.getText().toString());
            purchaseRequest.setTickettypeid(ticketTypeId);
            purchaseRequest.setAdult(adult);
            purchaseRequest.setChildren(child);
            if(chkMultiple.isChecked()){
                purchaseRequest.setMultiple(true);
            }
            else
            {
                purchaseRequest.setMultiple(false);
            }
        }
        else if(Objects.requireNonNull(tfChildrenNo.getText()).toString().equals(""))
        {
            child = 0;
            purchaseRequest.setEmail("");
            purchaseRequest.setPhoneNumber("");
            adult = Integer.parseInt(tfAdultNo.getText().toString());
            purchaseRequest.setTickettypeid(ticketTypeId);
            purchaseRequest.setAdult(adult);
            purchaseRequest.setChildren(child);
            if(chkMultiple.isChecked()){
                purchaseRequest.setMultiple(true);
            }
            else
            {
                purchaseRequest.setMultiple(false);
            }
        }
        else
        {
            adult = Integer.parseInt(tfAdultNo.getText().toString());
            child = Integer.parseInt(tfChildrenNo.getText().toString());
            purchaseRequest.setEmail("");
            purchaseRequest.setPhoneNumber("");
            purchaseRequest.setTickettypeid(ticketTypeId);
            purchaseRequest.setAdult(adult);
            purchaseRequest.setChildren(child);
            if(chkMultiple.isChecked()){
                purchaseRequest.setMultiple(true);
            }
            else
            {
                purchaseRequest.setMultiple(false);
            }
        }

        return purchaseRequest;
    }


    private void validateInput()
    {
        ticketType = tfTicketType.getText().toString();
        adultNo = tfAdultNo.getText().toString();
        childNo = tfChildrenNo.getText().toString();

        if (TextUtils.isEmpty(ticketType))
        {
            Toast.makeText(this, "Please Select Ticket Type", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(adultNo) && TextUtils.isEmpty(childNo))
        {
            Toast.makeText(this, "Please Enter Adult or Children Number", Toast.LENGTH_SHORT).show();
        }
        else if(childNo.equals("0") && adultNo.equals("0"))
        {
            Toast.makeText(this, "Please Enter Adult or Children Number", Toast.LENGTH_SHORT).show();
        }
        else
        {
            dialog = new Dialog(CashActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_wait2);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            getPrice(getQuote());
        }

    }

    private void getPrice(PriceRequest priceRequest) {

        Call<PriceRequest> priceRequestCall = ApiService.getTicketApiService().getPrice(priceRequest);
        priceRequestCall.enqueue(new Callback<PriceRequest>() {
            @Override
            public void onResponse(Call<PriceRequest> call, Response<PriceRequest> response) {
                if(response.isSuccessful())
                {
                    // Get the response body as a string
                    PriceRequest priceTotal = response.body();
                    double total = priceTotal.getPrice();
                    String ans;
                    ans = String.valueOf(total);
                    totalPrice.setText(ans);
                    dialog.dismiss();
                    btnPurchase.setEnabled(true);
                }
                else
                {
                    Toast.makeText(CashActivity.this, "Request Failed", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<PriceRequest> call, Throwable t) {
                Toast.makeText(CashActivity.this, "Request Failed" +t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    public PriceRequest getQuote()
    {
        PriceRequest priceRequest = new PriceRequest();
        String adult;
        String child;
        if(Objects.requireNonNull(tfAdultNo.getText()).toString().equals("")) {
            adult = "0";
            child = tfChildrenNo.getText().toString();
            priceRequest.setId(ticketTypeId);
            priceRequest.setAdult(Integer.parseInt(adult));
            priceRequest.setChildren(Integer.parseInt(child));
        }else if(Objects.requireNonNull(tfChildrenNo.getText()).toString().equals("")) {
            child = "0";
            adult = tfAdultNo.getText().toString();
            priceRequest.setId(ticketTypeId);
            priceRequest.setAdult(Integer.parseInt(adult));
            priceRequest.setChildren(Integer.parseInt(child));
        }else{
            adult = tfAdultNo.getText().toString();
            child = tfChildrenNo.getText().toString();
            priceRequest.setId(ticketTypeId);
            priceRequest.setAdult(Integer.parseInt(adult));
            priceRequest.setChildren(Integer.parseInt(child));
        }

        return priceRequest;
    }

    private void getTicketType()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TicketInterface.base_url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TicketInterface api = retrofit.create(TicketInterface.class);
        Call<String> call = api.getTicketType();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i("Response", response.body());
                if(response.isSuccessful()){
                    if(response.body()!=null){
                        Log.i("Success", response.body());
                        try {
                            String getResponse = response.body();
                            List<TicketType> getTicketTypeData = new ArrayList<TicketType>();
                            JSONObject object = new JSONObject(getResponse);
                            JSONArray array  = object.getJSONArray("types");
                            getTicketTypeData.add(new TicketType(-1, "---Select---"));

                            for (int i = 0; i < array.length(); i++)
                            {
                                TicketType ticket = new TicketType();
                                JSONObject JsonObject = array.getJSONObject(i);
                                ticket.setId(JsonObject.getInt("id"));
                                ticket.setName(JsonObject.getString("name"));
                                getTicketTypeData.add(ticket);
                            }
                            for(int i=0; i<getTicketTypeData.size(); i++){
                                getTicketType.add(getTicketTypeData.get(i).getName());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(CashActivity.this, android.R.layout.simple_spinner_item,getTicketType);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                            tfTicketType.setAdapter(adapter);

                            tfTicketType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String selectedItem = (String) parent.getItemAtPosition(position);
                                    // Get the index number of the selected item
                                    int index = getTicketType.indexOf(selectedItem);
                                    // You can now use the "index" variable to get the item from the JSON array
                                    Log.d("item selected", String.valueOf(index));

                                    ticketTypeId = index;

                                    // You can now use the index variable to access the selected item in the JSON array
                                }
                            });


                        }catch (JSONException ex){
                            ex.printStackTrace();
                        }

                        dialog.dismiss();
                        Toast.makeText(CashActivity.this, "Data Loaded Successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(CashActivity.this, "Request Failed" +t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
}