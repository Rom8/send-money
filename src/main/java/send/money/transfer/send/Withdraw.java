package send.money.transfer.send;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.hateos.VndError;
import send.money.transfer.MoneyTransferException;
import send.money.transfer.Operation;
import send.money.transfer.OperationService;

public class Withdraw implements MoneySendAction {

    private final OperationService operationService;

    public Withdraw(OperationService operationService) {
        this.operationService = operationService;
    }

    @Override
    public void call(Operation operation) {
        if(!operationService.withdraw(operation)) {
            throw new MoneyTransferException(HttpResponse.badRequest(new VndError("Sender does not have enough funds.")));
        }
    }

    @Override
    public Long getAccountId(Operation operation) {
        return operation.getSender();
    }
}
