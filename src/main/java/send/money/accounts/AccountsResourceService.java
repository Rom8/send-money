package send.money.accounts;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.hateos.Resource;

import java.util.List;

public interface AccountsResourceService {

    List<Resource> getAccounts();

    HttpResponse<Resource> findAccount(Long id);

    HttpResponse<Resource> createAccount(Account account);
}
