package send.money.accounts;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.hateos.Resource;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller(AccountsController.ACCOUNTS_LINK)
public class AccountsController {

    public static final String ACCOUNTS = "accounts";
    public static final String ACCOUNTS_LINK = "/" + ACCOUNTS;

    private final AccountsResourceService accountsResourceService;

    @Inject
    public AccountsController(AccountsResourceService accountsResourceService) {
        this.accountsResourceService = accountsResourceService;
    }

    /**
     * @return a list of accounts
     */
    @Get(produces = MediaType.APPLICATION_HAL_JSON)
    public CompletableFuture<List<Resource>> getAccountsList() {
        return CompletableFuture
                .supplyAsync(accountsResourceService::getAccounts);
    }

    /**
     * @param id of account
     * @return an account by id
     */
    @Get(value = "/{id}", produces = MediaType.APPLICATION_HAL_JSON)
    public CompletableFuture<HttpResponse<Resource>> getAccount(@PathVariable("id") Long id) {
        return CompletableFuture
                .supplyAsync(() -> accountsResourceService.findAccount(id));
    }

    /**
     * Creates new account
     * @param account to be created
     * @return newly created account
     */
    @Post(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_HAL_JSON)
    public CompletableFuture<HttpResponse<Resource>> create(@Body Account account) {
        return CompletableFuture
                .supplyAsync(() -> accountsResourceService.createAccount(account));
    }
}