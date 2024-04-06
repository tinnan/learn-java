package com.example.demo.batch.tut1;

import lombok.AllArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@AllArgsConstructor
public class PeopleItemWriter implements ItemWriter<People> {
    private final PeopleRepository peopleRepository;

    @Override
    public void write(Chunk<? extends People> chunk) {
        peopleRepository.saveAll(chunk.getItems());
    }
}
