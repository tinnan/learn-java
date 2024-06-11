package com.example.demo.spockfeature

import com.example.demo.spockfeature.model.ObjectClass
import com.example.demo.spockfeature.model.TestClass
import spock.lang.Specification

class MockingStubbingSpec extends Specification {
    def testClass = Mock(TestClass)

    def "Normal stubbing"() {
        given:
        testClass.createObject(_ as String, _ as Integer) >> new ObjectClass(100, "John Doe")

        when:
        def something = testClass.createObject("X", 1)

        then:
        verifyAll(something, ObjectClass) {
            val == 100
            name == "John Doe"
        }
    }

    def "Custom stubbing"() {
        given:
        testClass.createObject(_ as String, _ as Integer) >> { name, val ->
            val > 50 ? new ObjectClass((Integer) val, "Z") : new ObjectClass((Integer) val, "A")
        }

        expect:
        verifyAll(testClass.createObject("", 70), ObjectClass) {
            val == 70
            name == "Z"
        }
        verifyAll(testClass.createObject("", 40), ObjectClass) {
            val == 40
            name == "A"
        }
    }
}
