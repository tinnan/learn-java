package com.example.demo.service;

import com.example.demo.clients.FraudClient;
import com.example.demo.clients.NotificationClient;
import com.example.demo.controller.exception.IneligibleCustomerException;
import com.example.demo.domain.Customer;
import com.example.demo.domain.CustomerRegisterResult;
import com.example.demo.domain.Notification;
import com.example.demo.repo.CustomerRepository;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {
    public static final String CUSTOMER_REGISTERED_MESSAGE = "You are registered. Welcome aboard!";

    private final FraudClient fraudClient;
    private final NotificationClient notificationClient;
    private final CustomerRepository customerRepository;

    public CustomerRegisterResult register(Customer customer) throws IneligibleCustomerException {
        boolean isFraudster = fraudClient.isFraudster(customer.getEmail());
        if (isFraudster) {
            throw new IneligibleCustomerException(customer.getEmail());
        }

        customer.setJoinDate(LocalDate.now());
        Customer registered = customerRepository.save(customer);

        Notification notification = new Notification().setNotifyToEmail(customer.getEmail())
            .setMessage(CustomerService.CUSTOMER_REGISTERED_MESSAGE);
        Notification notified = notificationClient.notify(notification);

        return new CustomerRegisterResult(registered, notified);
    }

    public Customer find(long customerId) {
        Optional<Customer> byId = customerRepository.findById(customerId);
        return byId.orElse(null);
    }
}
