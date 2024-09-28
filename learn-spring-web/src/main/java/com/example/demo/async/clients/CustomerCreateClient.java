package com.example.demo.async.clients;

import com.example.demo.async.api.CustomerCreateApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "customer-create", url = "${clients.customer.url}", path = "/api/v1")
public interface CustomerCreateClient extends CustomerCreateApi {

}
