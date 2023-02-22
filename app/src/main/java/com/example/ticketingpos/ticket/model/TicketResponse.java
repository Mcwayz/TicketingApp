package com.example.ticketingpos.ticket.model;

import java.util.List;

public class TicketResponse {

    private List<Ticket> ticket;

    private List<String> billerResponse;
    private List<String> billerResponseCode;



    public List<Ticket> getTicket() {
        return ticket;
    }

    public void setTicket(List<Ticket> ticket) {
        this.ticket = ticket;
    }

    public List<String> getBillerResponse() {
        return billerResponse;
    }

    public void setBillerResponse(List<String> billerResponse) {
        this.billerResponse = billerResponse;
    }

    public List<String> getBillerResponseCode() {
        return billerResponseCode;
    }

    public void setBillerResponseCode(List<String> billerResponseCode) {
        this.billerResponseCode = billerResponseCode;
    }


}
