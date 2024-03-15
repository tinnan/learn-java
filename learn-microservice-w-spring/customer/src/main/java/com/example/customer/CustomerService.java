package com.example.customer;

import com.example.amqp.RabbitMQMessageProducer;
import com.example.amqp.message.CustomerCreatedMessage;
import com.example.common.Global;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;

    public void registerCustomer(CustomerRegistrationRequest customerRequest) {
        Customer customer = Customer
                .builder()
                .firstName(customerRequest.firstName())
                .lastName(customerRequest.lastName())
                .email(customerRequest.email())
                .build();
        // todo: check if email valid, not taken
        // todo: store customer in db
        customerRepository.saveAndFlush(customer);
        // todo: check if fraudster
        FraudCheckResponse fraudCheckResponse = restTemplate.getForObject("http://FRAUD/api/v1/fraud-check" +
                        "/{customerId}",
                FraudCheckResponse.class,
                customer.getId());

        if (fraudCheckResponse != null && fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }
        // todo: send notification
        rabbitMQMessageProducer.publish(new CustomerCreatedMessage(customer.getId(), customer.getEmail()), Global.RABBIT_EXCHANGE,
                "created");
    }
}
