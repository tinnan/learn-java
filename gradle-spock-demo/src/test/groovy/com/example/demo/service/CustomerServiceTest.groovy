package com.example.demo.service

import com.example.demo.clients.FraudClient
import com.example.demo.clients.NotificationClient
import com.example.demo.domain.Customer
import com.example.demo.domain.Notification
import com.example.demo.model.FraudResponse
import com.example.demo.repo.CustomerRepository
import spock.lang.Specification

class CustomerServiceTest extends Specification {

    def fraudClient = Mock(FraudClient)
    def notificationClient = Mock(NotificationClient)
    def customerRepository = Mock(CustomerRepository)
    def unitUnderTest = new CustomerService(fraudClient, notificationClient, customerRepository)

    def "Should be able to register customer given the customer is not a fraudster"() {
        given:
        def email = "john.d@test.com"
        def customer = new Customer(email: email, username: "jane")
        def registeredCustomer = new Customer(id: 1L, email: email, username: "jane")
        def notificationResult = new Notification(id: 1L, notifyToEmail: email,
                message: CustomerService.CUSTOMER_REGISTERED_MESSAGE)

        and:
        fraudClient.isFraudster(_ as String) >> {
            String customerEmail ->
                verifyAll {
                    customerEmail == email
                }
                return FraudResponse.builder().fraudster(false).build()
        }

        when:
        def registerResult = unitUnderTest.register(customer)

        then:
        verifyAll(registerResult) {
            it.getRegistered() == registeredCustomer
            it.getNotified() == notificationResult
        }
        then:
        1 * customerRepository.save(_ as Customer) >> {
            Customer newCustomer ->
                verifyAll {
                    newCustomer == customer
                }
                return registeredCustomer
        }
        then:
        1 * notificationClient.notify(_ as Notification) >> {
            Notification notifying ->
                verifyAll(notifying) {
                    it.getNotifyToEmail() == email
                    it.getMessage() == CustomerService.CUSTOMER_REGISTERED_MESSAGE
                }
                return notificationResult
        }
    }
}
