package send.money.transfer.send;

import send.money.transfer.Operation;

/**
 * A simple action to deposit or withdraw money
 */
public interface MoneySendAction {

    Long getAccountId(Operation operation);

    /**
     * Call an action upon the operation
     * @param operation which status will be changed
     */
    void call(Operation operation);
}
