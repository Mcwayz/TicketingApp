package com.example.ticketingpos.ticket.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ticketingpos.R;
import com.example.ticketingpos.ticket.model.Ticket;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {

    private List<Ticket> ticketList;

    public TicketAdapter(List<Ticket> ticketList) {
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_row_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);

        holder.tvTicketId.setText(String.valueOf(ticket.getId()));
        holder.tvTicketType.setText(ticket.getTicketName());
        holder.tvTicketNumber.setText(String.valueOf(ticket.getTicketNumber()));
        holder.tvAdult.setText(String.valueOf(ticket.getAdult()));
        holder.tvChildren.setText(String.valueOf(ticket.getChildren()));
        holder.tvPrice.setText(String.valueOf(ticket.getTotalPrice()));

        MultiFormatWriter write = new MultiFormatWriter();
        try {
            BitMatrix matrix = write.encode(ticket.getBarcode(), BarcodeFormat.QR_CODE, 250, 250);
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
        return ticketList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTicketId, tvTicketType, tvTicketNumber, tvAdult, tvChildren, tvPrice;
        ImageView imgQRC;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTicketId = itemView.findViewById(R.id.tv_ticket_id_mul);
            tvTicketType = itemView.findViewById(R.id.tv_ticket_type_mul);
            tvPrice = itemView.findViewById(R.id.tv_price_mul);
            tvTicketNumber = itemView.findViewById(R.id.tv_ticket_number_mul);
            tvAdult = itemView.findViewById(R.id.tv_adult_slot_mul);
            tvChildren = itemView.findViewById(R.id.tv_children_slot_mul);
            imgQRC = itemView.findViewById(R.id.img_mul_qrc);
        }
    }
}
