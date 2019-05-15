package send.money.accounts;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Singleton
public class AccountRepositoryImpl implements AccountRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Account create(String name, long amount) {
        Account account = new Account(name, amount);
        entityManager.persist(account);
        return account;
    }

    @Override
    public List<Account> findAll() {
        String sqlQuery = "SELECT a FROM Account as a ORDER BY a.id";
        TypedQuery<Account> query = entityManager.createQuery(sqlQuery, Account.class);
        return query.getResultList();
    }

    @Override
    public Optional<Account> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Account.class, id));
    }
}
