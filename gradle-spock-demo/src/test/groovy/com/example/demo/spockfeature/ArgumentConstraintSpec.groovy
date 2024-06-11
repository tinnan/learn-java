package com.example.demo.spockfeature


import spock.lang.Specification

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

class TestClass {
    void doSomething(int val, String name) {
        // Do something.
    }

    void doOtherThing(ObjectClass o) {
        // Do something.
    }
}

class ObjectClass {
    private int val
    private String name

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    int getVal() {
        return val
    }

    void setVal(int val) {
        this.val = val
    }

    ObjectClass(int val, String name) {
        this.val = val
        this.name = name
    }
}
