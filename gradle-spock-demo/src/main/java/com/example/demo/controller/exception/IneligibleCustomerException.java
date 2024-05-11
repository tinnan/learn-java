package com.example.demo.controller.exception;

public class IneligibleCustomerException extends Exception {
    public IneligibleCustomerException(String customerEmail) {
        super("Customer with e-mail [" + customerEmail +"] is ineligible for the operation.");
    }
}
