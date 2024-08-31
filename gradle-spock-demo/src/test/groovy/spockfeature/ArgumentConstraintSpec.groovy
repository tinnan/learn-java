package spockfeature


import spockfeature.model.TestClass
import spock.lang.Specification
import spockfeature.model.ObjectClass

class ArgumentConstraintSpec extends Specification {
    def testClass = Mock(TestClass)

    def "Equality match"() {
        when:
        testClass.doSomething(10, "+")

        then:
        1 * testClass.doSomething(10, "+")
    }

    def "Type match"() {
        when:
        testClass.doSomething(10, "+")

        then:
        1 * testClass.doSomething(10, _ as String)

        when:
        testClass.doSomething(10, "+")

        then: "Any matching"
        1 * testClass.doSomething(_, _)
    }

    def "Null match"() {
        when:
        testClass.doSomething(10, null)

        then: "will not match in type matching"
        0 * testClass.doSomething(10, _ as String)

        then: "(but) will match null type matching"
        1 * testClass.doSomething(10, null)
    }

    def "Closure"() {
        when:
        testClass.doSomething(10, "+")

        then:
        // Note: variable inside argument constraint closure must be named "it".
        1 * testClass.doSomething({ it > 9 }, _ as String)
    }

    def "Closure - Regex matching"() {
        when:
        testClass.doSomething(10, "John Doe")

        then: "Regex matching"
        1 * testClass.doSomething(10, { it ==~ /.*n Doe/ })
    }

    def "Closure - verify all"() {
        when:
        testClass.doOtherThing(new ObjectClass(10, "John"))

        then:
        1 * testClass.doOtherThing({
            verifyAll(it, ObjectClass) {
                val == 10
                name == "John"
            }
        })
    }
}
