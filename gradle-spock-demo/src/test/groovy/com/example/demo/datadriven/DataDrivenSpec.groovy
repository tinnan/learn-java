package com.example.demo.datadriven

import spock.lang.Specification

class DataDrivenSpec extends Specification {
    def "Math.max with data table input"() {
        expect:
        Math.max(a, b) == c;

        where:
        a | b | c
        2 | 5 | 5
        7 | 3 | 7
        5 | 5 | 5
    }
}
