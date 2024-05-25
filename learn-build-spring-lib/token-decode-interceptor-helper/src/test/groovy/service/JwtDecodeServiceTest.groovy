package service

import org.example.security.service.JwtDecodeService
import spock.lang.Shared
import spock.lang.Specification

class JwtDecodeServiceTest extends Specification {
    @Shared
    JwtDecodeService unitUnderTest = new JwtDecodeService()

    def "decode authorization header"() {
        when:
        def result = unitUnderTest.extractPermissions(authorization)

        then:
        result == expectedResult

        where:
        authorization                                                                                                                   | expectedResult
        null                                                                                                                            | []
        ""                                                                                                                              | []
        "Bearer "                                                                                                                       | []
        "bearer  "                                                                                                                      | []
        "Bearer YWJj"                                                                                                                   | []
        "Bearer YWJj.a2FzZA=="                                                                                                          | []
        "Bearer YWJj..a2FzZA=="                                                                                                         | []
        "Bearer YWJj.jahjsd987qw.a2FzZA=="                                                                                              | [] // Payload section is not Base64
        "Bearer ZHVtbXk=.aW52YWxpZCBwYXlsb2Fk.ZHVtbXk="                                                                                 | [] // Invalid payload format (not JSON)
        "Bearer ZHVtbXk=.eyJzdGFmZl9pZCI6IjYxNjQzIn0=.ZHVtbXk="                                                                         | []
        "Bearer ZHVtbXk=.eyJzdGFmZl9pZCI6IjYxNjQzIiwicGVybWlzc2lvbnMiOlsiRVhQT1JUX1JFUE9SVCIsIlFVRVJZX0FDVElWSVRZX0xPRyJdfQ==.ZHVtbXk=" | ["EXPORT_REPORT", "QUERY_ACTIVITY_LOG"]
    }
}
