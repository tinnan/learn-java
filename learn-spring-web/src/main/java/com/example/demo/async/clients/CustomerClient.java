package com.example.demo.async.clients;

import com.example.demo.async.api.CustomerApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "customer", url = "${clients.customer.url}")
public interface CustomerClient extends CustomerApi {

}
