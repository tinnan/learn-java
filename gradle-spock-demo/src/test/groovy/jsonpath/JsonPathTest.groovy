package jsonpath

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.Option
import groovy.util.logging.Slf4j
import spock.lang.Specification

import static com.jayway.jsonpath.JsonPath.parse
import static groovy.json.JsonOutput.toJson

@Slf4j
class JsonPathTest extends Specification {

    def "Test JSON path"() {
        given:
        def json = toJson([
                "firstName": "John",
                "lastName": "doe",
                "age": 26,
                "address": [
                    "streetAddress": "naist street",
                    "city": "Nara",
                    "postalCode": "630-0192"
                ],
                "phoneNumbers": [
                        [
                            "type": "iPhone",
                            "number": "0123-4567-8888"
                        ],
                        [
                            "type": "home",
                            "number": "0123-4567-8910"
                        ]
                ],
                "emptyObject": [:],
                "emptyArray": [],
                "nullNode": null
        ])
        def document = parse(json,
                Configuration.builder().options(Option.DEFAULT_PATH_LEAF_TO_NULL).build())

        when: "reading leaf node should return value"
        def result = document.read('$.address.streetAddress')
        log.info("Leaf node {} value: {}", result.getClass(), result)
        then:
        result == "naist street"

        when: "reading object node should return Map"
        result = document.read('$.address')
        log.info("Object node {} value: {}", result.getClass(), result)
        then:
        result instanceof Map

        when: "reading array node should return List"
        result = document.read('$.phoneNumbers')
        log.info("Array node {} value: {}", result.getClass(), result)
        then:
        result instanceof List

        when: "reading empty Object node should return empty Map"
        result = document.read('$.emptyObject')
        log.info("Empty object node {} value: {}", result.getClass(), result)
        then:
        result instanceof Map
        (result as Map).isEmpty()

        when: "reading empty array node should return List"
        result = document.read('$.emptyArray')
        log.info("Empty array node {} value: {}", result.getClass(), result)
        then:
        result instanceof List
        (result as List).isEmpty()

        when: "reading null node should return null"
        result = document.read('$.nullNode')
        log.info("Null node value: {}", result)
        then:
        result == null

        when: "reading non-existing node should return null"
        // This returns null because option DEFAULT_PATH_LEAF_TO_NULL is turned on.
        // Otherwise, this operation will throw PathNotFoundException.
        result = document.read('$.nonExistNode')
        log.info("Non-existing node value: {}", result)
        then:
        result == null
    }
}
