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

    def "Math.max with data table input"() {
        expect:
        Math.max(a, b) == c;

        where:
        a << [2, 7, 5]
        b << [5, 3, 5]
        c << [5, 7, 5]
    }
}
