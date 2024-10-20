package com.example.demo.feignconfig.clients;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "server-client", url = "${feign.server-client.url}")
public interface ServerClient extends ServerApi {

}
