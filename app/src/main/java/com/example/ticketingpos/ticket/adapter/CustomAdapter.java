package com.example.ticketingpos.ticket.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ticketingpos.R;
import com.example.ticketingpos.ticket.restapi.RecyclerViewInterface;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.sql.Array;
import java.util.ArrayList;
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> implements RecyclerViewInterface {
    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private ArrayList txn_id, ticket_id, barcode, ticket_type, ticket_number, children, adult, price, date;
    public CustomAdapter(Context context, ArrayList txn_id, ArrayList ticket_id, ArrayList barcode, ArrayList ticket_type,
                         ArrayList ticket_number, ArrayList children, ArrayList adult, ArrayList price, ArrayList date,
                         RecyclerViewInterface recyclerViewInterface){
        this.context = context;
        this.txn_id = txn_id;
        this.ticket_id = ticket_id;
        this.barcode = barcode;
        this.ticket_type = ticket_type;
        this.ticket_number = ticket_number;
        this.children = children;
        this.adult = adult;
        this.price = price;
        this.date = date;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvTxnId.setText(String.valueOf(txn_id.get(position)));
        holder.tvTicketId.setText(String.valueOf(ticket_id.get(position)));
        holder.tvTicketType.setText(String.valueOf(ticket_type.get(position)));
        holder.tvPrice.setText(String.valueOf(price.get(position)));
        holder.tvDate.setText(String.valueOf(date.get(position)));
        holder.tvAdult.setText(String.valueOf(adult.get(position)));
        holder.tvChild.setText(String.valueOf(children.get(position)));

        MultiFormatWriter write = new MultiFormatWriter();
        try {
            BitMatrix matrix = write.encode(String.valueOf(barcode.get(position)), BarcodeFormat.QR_CODE, 350, 350);
            BarcodeEncoder encoder = new BarcodeEncoder();
            //Initialize bitmap
            Bitmap bitmap = encoder.createBitmap(matrix);
            holder.imgQRC.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return txn_id.size();
    }
    @Override
    public void onItemClick(int position) {

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTxnId, tvTicketId, tvTicketType,  tvPrice, tvDate, tvAdult, tvChild;
        ImageView imgQRC;
        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
             tvTxnId = itemView.findViewById(R.id.tv_txn_id);
             tvTicketId = itemView.findViewById(R.id.tv_ticket_id_his);
             tvTicketType = itemView.findViewById(R.id.tv_ticket_type_his);
             tvPrice = itemView.findViewById(R.id.tv_price_his);
             tvDate = itemView.findViewById(R.id.tv_date_his);
             tvAdult = itemView.findViewById(R.id.tv_adult_slot_his);
             tvChild = itemView.findViewById(R.id.tv_children_slot_his);
             imgQRC = itemView.findViewById(R.id.img_his_qrc);

             itemView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if(recyclerViewInterface != null){
                         int pos = getAdapterPosition();
                         if(pos !=RecyclerView.NO_POSITION){
                             recyclerViewInterface.onItemClick(pos);
                         }
                     }
                 }
             });
        }
    }
}
