package send.money.transfer;

import java.util.Optional;

public interface OperationRepository {

    /**
     * Creates new money transfer request
     *
     * @param operation a wrapper contains info about from whom, to who and how much funds are send.
     * @return an id of the request
     */
    Operation createNew(Operation operation);

    boolean withdraw(Operation operation);

    void deposit(Operation operation);

    Optional<Operation> find(Long id);
}
