package send.money.transfer

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.client.RxHttpClient
import io.micronaut.runtime.server.EmbeddedServer
import send.money.accounts.Account
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import java.util.concurrent.Executors
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit

@Stepwise
class OperationsControllerSpec extends Specification {

    @Shared @AutoCleanup EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer)
    @Shared @AutoCleanup RxHttpClient client = embeddedServer.applicationContext.createBean(RxHttpClient, embeddedServer.getURL())


    /**
     * Create 4 accounts
     */
    void setupSpec() {
        client.toBlocking().exchange(
                HttpRequest.POST("/accounts", new Account("Idan", 1_000_030))
                        .contentType(MediaType.APPLICATION_JSON), Account)
        client.toBlocking().exchange(
                HttpRequest.POST("/accounts", new Account("Leksa", 1_000_000))
                        .contentType(MediaType.APPLICATION_JSON), Account)
        client.toBlocking().exchange(
                HttpRequest.POST("/accounts", new Account("Radomir", 1_000_000))
                        .contentType(MediaType.APPLICATION_JSON), Account)
        client.toBlocking().exchange(
                HttpRequest.POST("/accounts", new Account("Ivanka", 1_000_000))
                        .contentType(MediaType.APPLICATION_JSON), Account)
    }

    def "send money simple"() {
        given:
        HttpResponse<Operation> operationResponse

        when:
        operationResponse = client.toBlocking().exchange(
                HttpRequest.POST(OperationsController.OPERATIONS_LINK, new Operation(1, 2, 10))
                        .contentType(MediaType.APPLICATION_JSON), Operation)

        then:
        Operation operation = operationResponse.getBody().get()
        operation.sender == 1L
        operation.recipient == 2L
        operation.amount == 10L
        operation.transferStatus == TransferStatus.DELIVERED

        when:
        def idanResponse = client.toBlocking().exchange("/accounts/1", Account)
        def leksaResponse = client.toBlocking().exchange("/accounts/2", Account)

        then:
        idanResponse.getBody().get().balance == 1_000_020L
        leksaResponse.getBody().get().balance == 1_000_010L
    }

    def "huge density of operations"() {
        given:
        int n = 10_000
        def pool = Executors.newFixedThreadPool(32)

        when:
        for (int i = 0; i<n; i++) {
            pool.submit {
                long senderId = randomId(1, 4)
                long recipientId = randomId(senderId, senderId + 2) % 4 + 1

                client.toBlocking().retrieve(HttpRequest
                        .POST(OperationsController.OPERATIONS_LINK, new Operation(senderId, recipientId, randomMoneyAmount()))
                        .contentType(MediaType.APPLICATION_JSON))
            }
        }
        pool.shutdown()
        pool.awaitTermination(1, TimeUnit.MINUTES)

        long sumExpected = 4_000_030
        def idanBalance= client.toBlocking().exchange("/accounts/1", Account).getBody().get().balance
        def leksaBalance = client.toBlocking().exchange("/accounts/2", Account).getBody().get().balance
        def radomirBalance = client.toBlocking().exchange("/accounts/3", Account).getBody().get().balance
        def ivankaBalance = client.toBlocking().exchange("/accounts/4", Account).getBody().get().balance

        println("Idan: " + idanBalance)
        println("Leksa: " + leksaBalance)
        println("Radomir: " + radomirBalance)
        println("Ivanka: " + ivankaBalance)

        then:
        idanBalance + leksaBalance + radomirBalance + ivankaBalance == sumExpected
    }

    private static long randomId(long from, long to) {
        return ThreadLocalRandom.current().nextLong(from, to + 1)
    }

    private static long randomMoneyAmount() {
        return ThreadLocalRandom.current().nextLong(1,16)
    }
}
