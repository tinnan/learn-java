package com.example.demo.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class PersonItemProcessor implements ItemProcessor<Person, People> {
    @Override
    public People process(Person item) {
        final String firstName = item.firstName().toUpperCase();
        final String lastName = item.lastName().toUpperCase();
        final People transformed = new People(firstName, lastName);
        log.info("Converting ({}) into ({})", item, transformed);
        return transformed;
    }
}
