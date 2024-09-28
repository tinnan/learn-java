package com.example.demo.async.clients;

import com.example.demo.async.api.FraudApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "fraud", url = "${clients.fraud.url}", path = "/api/v1")
public interface FraudClient extends FraudApi {

}
