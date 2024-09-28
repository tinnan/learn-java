package com.example.demo.async.clients;

import com.example.demo.async.api.CustomerInfoApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "customer-info", url = "${clients.customer.url}", path = "/api/v1")
public interface CustomerInfoClient extends CustomerInfoApi {

}
