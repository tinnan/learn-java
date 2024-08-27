package com.example.demo.mapper;

import com.example.demo.model.mapper.source.SourceOneModel;
import com.example.demo.model.mapper.source.SourceTwoModel;
import com.example.demo.model.mapper.target.TargetModel;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(builder = @Builder(disableBuilder = true))
public interface TestMapper {

    TestMapper INSTANCE = Mappers.getMapper(TestMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "targetInner.codeOne", source = "one.code")
    @Mapping(target = "targetInner.codeTwo", source = "two.code")
    TargetModel mapFromMultipleSource(String id, SourceOneModel one, SourceTwoModel two);
}
