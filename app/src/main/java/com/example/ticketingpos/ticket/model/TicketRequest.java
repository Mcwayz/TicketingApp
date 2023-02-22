package com.example.ticketingpos.ticket.model;

public class TicketRequest {

    //sending
    private String email;
    private String phoneNumber;
    private int adult;
    private int children;
    private Boolean multiple;
    private int tickettypeid;


    public TicketRequest()
    {

    }

    public TicketRequest(String email, String phoneNumber, int adult, int children, Boolean multiple, int tickettypeid) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.adult = adult;
        this.children = children;
        this.multiple = multiple;
        this.tickettypeid = tickettypeid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public Boolean getMultiple() {
        return multiple;
    }

    public void setMultiple(Boolean multiple) {
        this.multiple = multiple;
    }

    public int getTickettypeid() {
        return tickettypeid;
    }

    public void setTickettypeid(int tickettypeid) {
        this.tickettypeid = tickettypeid;
    }

    @Override
    public String toString() {
        return "TicketRequest{" +
                "email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", adult=" + adult +
                ", children=" + children +
                ", multiple=" + multiple +
                ", tickettypeid=" + tickettypeid +
                '}';
    }
}
