package org.example.security.service

import org.example.security.model.JwtAuthentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import spock.lang.Shared
import spock.lang.Specification

class JwtDecodeServiceTest extends Specification {
    @Shared
    JwtDecodeService unitUnderTest = new JwtDecodeService()

    def "decode authorization header"() {
        when:
        def result = unitUnderTest.decodeToken(authorization)

        then:
        result == expectedResult

        where:
        authorization                                                                                                                   | expectedResult
        null                                                                                                                            | JwtAuthentication.unauthenticated()
        ""                                                                                                                              | JwtAuthentication.unauthenticated()
        "Bearer "                                                                                                                       | JwtAuthentication.unauthenticated()
        "bearer  "                                                                                                                      | JwtAuthentication.unauthenticated()
        "Bearer YWJj"                                                                                                                   | JwtAuthentication.unauthenticated()
        "Bearer YWJj.a2FzZA=="                                                                                                          | JwtAuthentication.unauthenticated()
        "Bearer YWJj..a2FzZA=="                                                                                                         | JwtAuthentication.unauthenticated()
        "Bearer YWJj.jahjsd987qw.a2FzZA=="                                                                                              | JwtAuthentication.unauthenticated() // Payload section is not Base64
        "Bearer ZHVtbXk=.aW52YWxpZCBwYXlsb2Fk.ZHVtbXk="                                                                                 | JwtAuthentication.unauthenticated() // Invalid payload format (not JSON)
        "Bearer ZHVtbXk=.eyJzdGFmZl9pZCI6IjYxNjQzIn0=.ZHVtbXk="                                                                         | new JwtAuthentication("61643")
        "Bearer ZHVtbXk=.eyJzdGFmZl9pZCI6IjYxNjQzIiwicGVybWlzc2lvbnMiOlsiRVhQT1JUX1JFUE9SVCIsIlFVRVJZX0FDVElWSVRZX0xPRyJdfQ==.ZHVtbXk=" | new JwtAuthentication("61643", List.of(new SimpleGrantedAuthority("EXPORT_REPORT"), new SimpleGrantedAuthority("QUERY_ACTIVITY_LOG")))
    }
}
