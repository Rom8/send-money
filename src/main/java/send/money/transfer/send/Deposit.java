package send.money.transfer.send;

import send.money.transfer.Operation;
import send.money.transfer.OperationService;

public class Deposit implements MoneySendAction {

    private final OperationService operationService;

    public Deposit(OperationService operationService) {
        this.operationService = operationService;
    }

    @Override
    public void call(Operation operation) {
        operationService.deposit(operation);
    }

    @Override
    public Long getAccountId(Operation operation) {
        return operation.getRecipient();
    }
}
