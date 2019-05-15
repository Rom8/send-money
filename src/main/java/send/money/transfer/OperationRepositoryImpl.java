package send.money.transfer;

import send.money.accounts.Account;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Singleton
public class OperationRepositoryImpl implements OperationRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Operation createNew(Operation operation) {
        entityManager.persist(operation);
        return operation;
    }

    @Override
    public boolean withdraw(Operation operation) {
        Account from = entityManager.find(Account.class, operation.getSender());
        if (from.getBalance() < operation.getAmount()) {
            operation.setTransferStatus(TransferStatus.NOT_ENOUGH_FUNDS);
            entityManager.merge(operation);
            return false;
        }
        from.setBalance(from.getBalance() - operation.getAmount());
        operation.setTransferStatus(TransferStatus.WITHDRAWN);
        entityManager.merge(from);
        entityManager.merge(operation);
        return true;
    }

    @Override
    public void deposit(Operation operation) {
        Account to = entityManager.find(Account.class, operation.getRecipient());
        to.setBalance(to.getBalance() + operation.getAmount());
        operation.setTransferStatus(TransferStatus.DELIVERED);
        entityManager.merge(to);
        entityManager.merge(operation);
    }

    @Override
    public Optional<Operation> find(Long id) {
        return Optional.ofNullable(entityManager.find(Operation.class, id));
    }
}
