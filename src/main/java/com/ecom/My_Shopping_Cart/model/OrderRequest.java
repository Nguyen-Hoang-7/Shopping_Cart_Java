package com.ecom.My_Shopping_Cart.model;

import jakarta.persistence.Entity;
import lombok.*;

@ToString
@Data
public class OrderRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNo;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String paymentType;
}
