package com.example.demo.service;

import com.example.demo.clients.FraudClient;
import com.example.demo.controller.exception.IneligibleCustomerException;
import com.example.demo.domain.Customer;
import com.example.demo.repo.CustomerRepository;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final FraudClient fraudClient;
    private final CustomerRepository customerRepository;

    public Customer register(Customer customer) throws IneligibleCustomerException {
        boolean isFraudster = fraudClient.isFraudster(customer.getEmail());
        if (isFraudster) {
            throw new IneligibleCustomerException(customer.getEmail());
        }
        customer.setJoinDate(LocalDate.now());
        return customerRepository.save(customer);
    }

    public Customer find(long customerId) {
        Optional<Customer> byId = customerRepository.findById(customerId);
        return byId.orElse(null);
    }
}
