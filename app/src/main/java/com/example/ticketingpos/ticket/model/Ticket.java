package com.example.ticketingpos.ticket.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Ticket implements Parcelable {

    //expecting
    private int id;
    private String barcode;
    private String ticketName;
    private String ticketNumber;
    private String adult;
    private String children;
    private double totalPrice;


    public Ticket()
    {

    }

    public Ticket(int id, String barcode, String ticketName, String ticketNumber, String adult, String children, double totalPrice) {
        this.id = id;
        this.barcode = barcode;
        this.ticketName = ticketName;
        this.ticketNumber = ticketNumber;
        this.adult = adult;
        this.children = children;
        this.totalPrice = totalPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getTicketName() {
        return ticketName;
    }

    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getAdult() {
        return adult;
    }

    public void setAdult(String adult) {
        this.adult = adult;
    }

    public String getChildren() {
        return children;
    }

    public void setChildren(String children) {
        this.children = children;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", barcode='" + barcode + '\'' +
                ", ticketName='" + ticketName + '\'' +
                ", ticketNumber='" + ticketNumber + '\'' +
                ", adult='" + adult + '\'' +
                ", children='" + children + '\'' +
                ", totalPrice=" + totalPrice +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(barcode);
        dest.writeString(ticketName);
        dest.writeString(ticketNumber);
        dest.writeString(adult);
        dest.writeString(children);
        dest.writeDouble(totalPrice);
    }

    public static final Creator<Ticket> CREATOR = new Creator<Ticket>() {
        @Override
        public Ticket createFromParcel(Parcel in) {
            return new Ticket(in);
        }

        @Override
        public Ticket[] newArray(int size) {
            return new Ticket[size];
        }
    };

    private Ticket(Parcel in) {
        id = in.readInt();
        barcode = in.readString();
        ticketName = in.readString();
        ticketNumber = in.readString();
        adult = in.readString();
        children = in.readString();
        totalPrice = in.readDouble();
    }

}
