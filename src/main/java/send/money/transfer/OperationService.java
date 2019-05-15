package send.money.transfer;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.hateos.Resource;

/**
 * Money transfer operation service.
 */
public interface OperationService {

    HttpResponse<Resource> findOperation(Long id);

    Operation createNewOperation(Operation operation);

    boolean withdraw(Operation operation);

    void deposit(Operation operation);

    HttpResponse<Resource> createOperationResource(Operation operation);
}
