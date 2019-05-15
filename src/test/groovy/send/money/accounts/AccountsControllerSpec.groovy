package send.money.accounts

import io.micronaut.context.ApplicationContext
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.hateos.VndError
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

@Stepwise
class AccountsControllerSpec extends Specification {

    @Shared @AutoCleanup EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer)
    @Shared @AutoCleanup RxHttpClient client = embeddedServer.applicationContext.createBean(RxHttpClient, embeddedServer.getURL())

    void "test no accounts created"() {
        given:
        HttpResponse response = client.toBlocking().exchange("/accounts")

        expect:
        response.status == HttpStatus.OK
    }

    void "create new accounts"() {
        given:
        HttpResponse<Account> response

        when:
        response = client.toBlocking().exchange(
                HttpRequest.POST("/accounts", new Account("Idan", 400))
                        .contentType(MediaType.APPLICATION_JSON), Account)

        then:
        response.status == HttpStatus.CREATED
        response.body().id == 1
        response.body().name == "Idan"

        when:
        response = client.toBlocking().exchange(
                HttpRequest.POST("/accounts", new Account("Leksa", 300))
                        .contentType(MediaType.APPLICATION_JSON), Account)

        then:
        response.status == HttpStatus.CREATED
        response.body().id == 2
        response.body().name == "Leksa"

    }

    void "check get existing account"() {
        given:
        HttpResponse<Account> response

        when:
        response = client.toBlocking().exchange("/accounts/2", Account)

        then:
        response.status == HttpStatus.OK
        response.body().id == 2
        response.body().name == "Leksa"
    }

    void "check get non-existing account"() {
        when:
        client.toBlocking().exchange("/accounts/3", Account, VndError)

        then:
        def ex = thrown(HttpClientResponseException)
        ex.response.status == HttpStatus.NOT_FOUND
        ex.response.getBody(VndError).get().message == "Account with id 3 is not found."
    }

    void "test existing accounts - GET"() {
        when:
        List<Account> accounts = client.toBlocking()
                .retrieve(HttpRequest.GET("/accounts"), Argument.of(List, Account))

        then:
        accounts.size() == 2    //only two accounts were created

        when:
        Account account1 = accounts.get(0)
        Account account2 = accounts.get(1)

        then:
        account1.id == 1L
        account1.name == "Idan"

        then:
        account2.id == 2L
        account2.name == "Leksa"
    }

}
