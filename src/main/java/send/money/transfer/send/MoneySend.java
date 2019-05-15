package send.money.transfer.send;

import send.money.transfer.Operation;

import java.util.Queue;

/**
 * Send money between accounts
 */
public interface MoneySend {

    /**
     * Send money between accounts by performing all {@link MoneySendAction}s
     *
     * @param operation money send operations
     * @param actions a queue of simple actions to perform
     * @return a money send operation (with another status than it was before).
     */
    Operation sendMoney(Operation operation, Queue<MoneySendAction> actions);
}
