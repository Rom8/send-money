package send.money.transfer;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.hateos.Resource;

public class MoneyTransferException extends RuntimeException {


    private final HttpResponse<Resource> response;

    public MoneyTransferException(HttpResponse<Resource> response) {
        this.response = response;
    }

    public HttpResponse<Resource> getResponse() {
        return response;
    }
}
