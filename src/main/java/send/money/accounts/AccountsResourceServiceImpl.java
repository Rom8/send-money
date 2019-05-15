package send.money.accounts;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.hateos.AbstractResource;
import io.micronaut.http.hateos.Link;
import io.micronaut.http.hateos.Resource;
import io.micronaut.http.hateos.VndError;
import io.micronaut.spring.tx.annotation.Transactional;
import send.money.ResourceUtils;
import send.money.ResourceWrapper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static send.money.accounts.AccountsController.ACCOUNTS_LINK;

@Singleton
public class AccountsResourceServiceImpl implements AccountsResourceService {

    private final AccountRepository accountRepository;

    @Inject
    public AccountsResourceServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Resource> getAccounts() {
        return accountRepository.findAll().stream()
                .map(account -> {
                    ResourceWrapper resourceWrapper = new ResourceWrapper(account);
                    resourceWrapper.link(Link.SELF, ResourceUtils.accountLink(account.getId()));
                    return resourceWrapper;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public HttpResponse<Resource> findAccount(Long id) {
        Optional<Account> account = accountRepository.findById(id);
        if (!account.isPresent()) {
            VndError notFoundError = new VndError(String.format("Account with id %d is not found.", id));
            notFoundError.link("accounts", ACCOUNTS_LINK);
            return HttpResponse.notFound(notFoundError);
        }
        AbstractResource<ResourceWrapper> accountResource = new ResourceWrapper(account.get());
        accountResource.link(Link.SELF, String.format("%s/%d", ACCOUNTS_LINK, id));
        accountResource.link("accounts", ACCOUNTS_LINK);
        return HttpResponse.ok(accountResource);
    }

    @Transactional
    @Override
    public HttpResponse<Resource> createAccount(Account newAccount) {
        Account account = accountRepository.create(newAccount.getName(), newAccount.getBalance());
        ResourceWrapper resourceWrapper = new ResourceWrapper(account);
        resourceWrapper.link(Link.SELF, ResourceUtils.accountLink(account.getId()));
        return HttpResponse.created(resourceWrapper);
    }
}
