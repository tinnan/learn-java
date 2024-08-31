package spockfeature

import spockfeature.model.ObjectClass
import spockfeature.model.TestClass
import spock.lang.Specification

class MockingStubbingSpec extends Specification {
    def testClass = Mock(TestClass)

    def "Return fixed value"() {
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

    def "Compute return values"() {
        given:
        testClass.createObject(_ as String, _ as Integer) >> { String name, Integer val ->
            val > 50 ? new ObjectClass(val, "Z") : new ObjectClass(val, "A")
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

    def "Response chaining"() {
        given:
        testClass.createNumber(_) >>> [10, 20, 30]
                >> { throw new IllegalStateException() }
                >>> [40, 50]
                >> 60
                >> { String s -> s == "Y" ? 70 : -1 }

        expect:
        testClass.createNumber("") == 10
        testClass.createNumber("") == 20
        testClass.createNumber("") == 30

        when:
        testClass.createNumber("")

        then:
        thrown(IllegalStateException)

        expect:
        testClass.createNumber("") == 40
        testClass.createNumber("") == 50
        testClass.createNumber("") == 60
        testClass.createNumber("Y") == 70
    }
}
