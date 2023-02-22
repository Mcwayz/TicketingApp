package com.example.ticketingpos.ticket.restapi;

import com.example.ticketingpos.ticket.model.PriceRequest;
import com.example.ticketingpos.ticket.model.TicketRequest;
import com.example.ticketingpos.ticket.model.TicketResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface TicketInterface {

    String base_url = "http://192.168.100.28:8080";
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })

    // Get Ticket Types

    @GET("/api/ticketTypes")
    Call<String> getTicketType();

    // Get ticket price
    @POST("/api/ticketPrice")
    Call<PriceRequest> getPrice(@Body PriceRequest priceRequest);

    // Purchase Cash Ticket
    @POST("/api/tickets")
    Call<TicketResponse> getTicket(@Body TicketRequest purchaseRequest);

    // Purchase Ticket using Airtel Money
    @POST("/api/tickets/mno")
    Call<TicketResponse> getMNOTicket(@Body TicketRequest purchaseRequest);
}
