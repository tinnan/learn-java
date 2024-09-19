package jsonpath

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.Option
import groovy.util.logging.Slf4j
import spock.lang.Specification

import static com.jayway.jsonpath.JsonPath.parse
import static groovy.json.JsonOutput.toJson

@Slf4j
class JsonPathTest extends Specification {

    def "reading leaf node should return value"() {
        given:
        def document = jsonDocument()

        when:
        def result = document.read('$.address.streetAddress')
        log.info("Leaf node {} value: {}", result.getClass(), result)
        then:
        result == "naist street"
    }

    def "reading object node should return Map"() {
        given:
        def document = jsonDocument()

        when:
        def result = document.read('$.address')
        log.info("Object node {} value: {}", result.getClass(), result)
        then:
        result instanceof Map
    }

    def "reading array node should return List"() {
        given:
        def document = jsonDocument()

        when:
        def result = document.read('$.phoneNumbers')
        log.info("Array node {} value: {}", result.getClass(), result)
        then:
        result instanceof List
    }

    def "reading specific leaf node from every element in array node should return the leaf node in every element of array"() {
        given:
        def document = jsonDocument()

        when:
        def result = document.read('$.phoneNumbers[*].type')
        log.info("Leaf node in array node {} value: {}", result.getClass(), result)
        then:
        (result as List).get(0) == "iPhone"
        (result as List).get(1) == "home"
    }

    def "reading empty Object node should return empty Map"() {
        given:
        def document = jsonDocument()

        when:
        def result = document.read('$.emptyObject')
        log.info("Empty object node {} value: {}", result.getClass(), result)
        then:
        result instanceof Map
        (result as Map).isEmpty()
    }

    def "reading empty array node should return List"() {
        given:
        def document = jsonDocument()

        when:
        def result = document.read('$.emptyArray')
        log.info("Empty array node {} value: {}", result.getClass(), result)
        then:
        result instanceof List
        (result as List).isEmpty()
    }

    def "reading null node should return null"() {
        given:
        def document = jsonDocument()

        when:
        def result = document.read('$.nullNode')
        log.info("Null node value: {}", result)
        then:
        result == null
    }

    def "reading non-existing node should return null"() {
        given:
        def document = jsonDocument()

        when:
        // This returns null because option DEFAULT_PATH_LEAF_TO_NULL is turned on.
        // Otherwise, this operation will throw PathNotFoundException.
        def result = document.read('$.nonExistNode')
        log.info("Non-existing node value: {}", result)
        then:
        result == null
    }

    def "reading numbered key map node should return Map with integer key"() {
        given:
        def document = jsonDocument()

        when:
        def result = document.read('$.numberedKeyMap')
        log.info("Numbered key map {} value: {}", result.getClass(), result)
        then:
        (result as Map).get("1") == "Val 1"
    }

    def "reading object node with wildcard should return value as List"() {
        given:
        def document = jsonDocument()

        when:
        def result = document.read('$.deepMap.*.type')
        log.info("{} value: {}", result.getClass(), result)
        then:
        (result as List).size() == 3
    }

    def jsonDocument() {
        def json = toJson([
                "firstName"     : "John",
                "lastName"      : "doe",
                "age"           : 26,
                "address"       : [
                        "streetAddress": "naist street",
                        "city"         : "Nara",
                        "postalCode"   : "630-0192"
                ],
                "phoneNumbers"  : [
                        [
                                "type"  : "iPhone",
                                "number": "0123-4567-8888"
                        ],
                        [
                                "type"  : "home",
                                "number": "0123-4567-8910"
                        ]
                ],
                "emptyObject"   : [:],
                "emptyArray"    : [],
                "nullNode"      : null,
                "numberedKeyMap": [
                        1: "Val 1",
                        2: "Val 2"
                ],
                "deepMap"       : [
                        "intermediate1": [
                                type: "A",
                                value: "1"
                        ],
                        "intermediate2": [
                                type: "B",
                                value: "2"
                        ],
                        "intermediate3": [
                                type: "C",
                                value: "3"
                        ],
                ]
        ])
        return parse(json, Configuration.builder().options(Option.DEFAULT_PATH_LEAF_TO_NULL).build())
    }
}
