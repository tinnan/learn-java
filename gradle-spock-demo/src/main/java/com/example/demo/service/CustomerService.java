package com.example.demo.service;

import com.example.demo.domain.Customer;
import com.example.demo.repo.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Customer register(Customer customer) {
        customer.setJoinDate(LocalDate.now());
        return customerRepository.save(customer);
    }
}
