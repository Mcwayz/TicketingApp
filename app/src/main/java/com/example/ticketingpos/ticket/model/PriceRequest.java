package com.example.ticketingpos.ticket.model;

public class PriceRequest {

    //sending
    private  int id;
    private int adult;
    private int children;

    //expecting
    private double price;

    public PriceRequest() {

    }

    public PriceRequest(int id, int adult, int children, double price) {
        this.id = id;
        this.adult = adult;
        this.children = children;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAdult() {
        return adult;
    }

    public void setAdult(int adult) {
        this.adult = adult;
    }

    public int getChildren() {
        return children;
    }

    public void setChildren(int children) {
        this.children = children;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "PriceRequest{" +
                "id=" + id +
                ", adult=" + adult +
                ", children=" + children +
                ", price=" + price +
                '}';
    }
}
