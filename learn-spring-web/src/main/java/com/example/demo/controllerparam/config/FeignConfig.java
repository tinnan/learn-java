package com.example.demo.controllerparam.config;

import com.example.demo.controllerparam.model.GetParams;
import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import java.lang.reflect.Type;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;

public class FeignConfig {

    @Bean
    public Encoder encoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new CustomEncode(messageConverters);
    }

    @Slf4j
    public static class CustomEncode extends SpringEncoder {

        public CustomEncode(ObjectFactory<HttpMessageConverters> messageConverters) {
            super(messageConverters);
        }

        @Override
        public void encode(Object o, Type type, RequestTemplate requestTemplate) throws EncodeException {
            log.info("Encoder: {}", o);
            if (o instanceof GetParams params) {
                requestTemplate.query("name", params.getName());
                if (params.getAge() != null) {
                    requestTemplate.query("age", params.getAge().toString());
                }
            } else {
                super.encode(o, type, requestTemplate);
            }
        }
    }
}
