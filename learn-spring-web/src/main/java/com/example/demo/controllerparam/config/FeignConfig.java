package com.example.demo.controllerparam.config;

import com.example.demo.controllerparam.model.GetParams;
import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import java.lang.reflect.Type;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

public class FeignConfig {

    @Bean
    public Encoder encoder() {
        return new CustomEncode();
    }

    @Slf4j
    public static class CustomEncode implements Encoder {

        @Override
        public void encode(Object o, Type type, RequestTemplate requestTemplate) throws EncodeException {
            log.info("Encoder 1");
            if (o instanceof GetParams params) {
                requestTemplate.query("name", params.getName());
                if (params.getAge() != null) {
                    requestTemplate.query("age", params.getAge().toString());
                }
            }
        }
    }
}
