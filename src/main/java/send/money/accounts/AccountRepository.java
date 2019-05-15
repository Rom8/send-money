package send.money.accounts;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    Account create(String name, long amount);

    List<Account> findAll();

    Optional<Account> findById(Long id);
}
