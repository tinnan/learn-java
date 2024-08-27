package com.example.demo.mapper

import com.example.demo.model.mapper.source.SourceOneModel
import com.example.demo.model.mapper.source.SourceTwoModel
import spock.lang.Specification

class TestMapperTest extends Specification {

    def "Test mapFromMultipleSource"() {
        given:
        def id = "ID-1"
        def sourceOne = SourceOneModel.builder().code("SRC-1").build()
        def sourceTwo = SourceTwoModel.builder().code("SRC-2").build()

        when:
        def result = TestMapper.INSTANCE.mapFromMultipleSource(id, sourceOne, sourceTwo)

        then:
        verifyAll(result) {
            it.getId() == id
            verifyAll(it.getTargetInner()) {
                it.getCodeOne() == sourceOne.getCode()
                it.getCodeTwo() == sourceTwo.getCode()
            }
        }
    }
}
