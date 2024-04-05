package com.example.demo.batch.tut1;

import lombok.AllArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Profile({ "tut1" })
public class PeopleItemWriter implements ItemWriter<People> {
    private final PeopleRepository peopleRepository;
    @Override
    public void write(Chunk<? extends People> chunk) {
        peopleRepository.saveAll(chunk.getItems());
    }
}
