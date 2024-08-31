package integration.api

import com.example.demo.DemoApplication
import com.example.demo.clients.FraudClient
import com.example.demo.clients.NotificationClient
import com.example.demo.controller.advisor.ExceptionHandlers
import com.example.demo.domain.CustomerRegisterResult
import com.example.demo.domain.Notification
import com.example.demo.model.FraudResponse
import io.restassured.RestAssured
import io.restassured.config.LogConfig
import io.restassured.filter.log.LogDetail
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import org.spockframework.spring.SpringBean
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import spock.lang.Specification

import static com.example.demo.service.CustomerService.CUSTOMER_REGISTERED_MESSAGE
import static groovy.json.JsonOutput.toJson
import static io.restassured.http.ContentType.JSON
import static org.springframework.test.util.TestSocketUtils.findAvailableTcpPort

@SpringBootTest(
        classes = DemoApplication,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CustomerRegisterApiIT extends Specification {
    private static final String BASE_URI = "http://localhost"
    private static final int PORT = findAvailableTcpPort()
    private static final String ELIGIBLE_CUSTOMER_EMAIL = "john.d@gmail.com"
    private static final String FRAUDSTER_CUSTOMER_EMAIL = "jane.d@gmail.com"
    private static final Notification NOTIFIED = new Notification()
            .setId(1)
            .setNotifyToEmail(ELIGIBLE_CUSTOMER_EMAIL)
            .setMessage(CUSTOMER_REGISTERED_MESSAGE)

    @DynamicPropertySource
    static void dynamicProps(DynamicPropertyRegistry registry) {
        def feignClientUrl = "${BASE_URI}:${PORT}"
        registry.add("server.port", () -> PORT)
        registry.add("feign.client.customer.url", () -> feignClientUrl)
        registry.add("feign.client.fraud.url", () -> feignClientUrl)
    }

    def setupSpec() {
        RestAssured.baseURI = BASE_URI
        RestAssured.port = PORT
        RestAssured.config = RestAssured.config()
                .logConfig(LogConfig.logConfig()
                        .enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL)
                        .enablePrettyPrinting(true))
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter())
    }

    @SpringBean
    FraudClient fraudClient = Stub() {
        isFraudster(ELIGIBLE_CUSTOMER_EMAIL) >> FraudResponse.builder().fraudster(false).build()
        isFraudster(FRAUDSTER_CUSTOMER_EMAIL) >> FraudResponse.builder().fraudster(true).build()
    }

    @SpringBean
    NotificationClient notificationClient = Stub() {
        // "it" is reference to the argument object.
        notify({
            it.notifyToEmail == ELIGIBLE_CUSTOMER_EMAIL
            it.message == CUSTOMER_REGISTERED_MESSAGE
        } as Notification) >> NOTIFIED

        /*
        * This style works the same as above:
        * notify({
        *     obj ->
        *         obj.notifyToEmail == ELIGIBLE_CUSTOMER_EMAIL
        *         obj.message == CUSTOMER_REGISTERED_MESSAGE
        * } as Notification) >> NOTIFIED
        */
        // Read more about Argument constraints at
        // https://spockframework.org/spock/docs/2.0/all_in_one.html#_argument_constraints
    }

    def "should success when registering eligible customer"() {
        given:
        Map request = [username: "johnd",
                       email   : ELIGIBLE_CUSTOMER_EMAIL]

        when:
        def response = RestAssured.with()
                .contentType(JSON).body(toJson(request))
                .when().post("/api/v1/customer")

        then:
        response.statusCode() == HttpStatus.OK.value()
        verifyAll(response.body.as(CustomerRegisterResult)) {
            registered.username == "johnd"
            registered.email == ELIGIBLE_CUSTOMER_EMAIL
            registered.joinDate
            notified == NOTIFIED
        }
    }

    def "should fail when registering ineligible customer"() {
        given:
        Map request = [username: "janed",
                       email   : FRAUDSTER_CUSTOMER_EMAIL]

        when:
        def response = RestAssured.with()
                .contentType(JSON).body(toJson(request))
                .when().post("/api/v1/customer")

        then:
        response.statusCode() == ExceptionHandlers.HTTP_STATUS_INELIGIBLE_CUSTOMER
    }
}
