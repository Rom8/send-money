package send.money.transfer;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.hateos.AbstractResource;
import io.micronaut.http.hateos.Link;
import io.micronaut.http.hateos.Resource;
import io.micronaut.http.hateos.VndError;
import io.micronaut.spring.tx.annotation.Transactional;
import send.money.ResourceUtils;
import send.money.ResourceWrapper;
import send.money.accounts.Account;
import send.money.accounts.AccountRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;
    private final AccountRepository accountRepository;

    @Inject
    public OperationServiceImpl(OperationRepository operationRepository, AccountRepository accountRepository) {
        this.operationRepository = operationRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public HttpResponse<Resource> findOperation(Long id) {
        Optional<Operation> operation = operationRepository.find(id);
        if (!operation.isPresent()) {
            VndError notFoundError = new VndError(String.format("Operation with id %d is not found.", id));
            return HttpResponse.notFound(notFoundError);
        }
        AbstractResource<ResourceWrapper> accountResource = new ResourceWrapper(operation.get());
        accountResource.link(Link.SELF, String.format("%s/%d", OperationsController.OPERATIONS_LINK, id));
        return HttpResponse.ok(accountResource);
    }

    @Transactional
    @Override
    public Operation createNewOperation(Operation operation) {
        Optional<Account> sender = accountRepository.findById(operation.getSender());
        Optional<Account> recipient = accountRepository.findById(operation.getRecipient());
        String message = "";
        if (!sender.isPresent()) {
            message += notFoundIdMessage("Sender", operation.getSender());
        }
        if (!recipient.isPresent()) {
            message += notFoundIdMessage("Recipient", operation.getRecipient());
        }
        if (!sender.isPresent() || !recipient.isPresent()) {
            throw new MoneyTransferException(HttpResponse.notFound(new VndError(message)));
        }
        return operationRepository.createNew(operation);
    }

    private String notFoundIdMessage(String person, Long id) {
        return String.format("%s with id %d is not found. ", person, id);
    }

    @Transactional
    @Override
    public boolean withdraw(Operation operation) {
        return operationRepository.withdraw(operation);
    }

    @Transactional
    @Override
    public void deposit(Operation operation) {
        operationRepository.deposit(operation);
    }

    @Override
    public HttpResponse<Resource> createOperationResource(Operation operation) {
        AbstractResource<ResourceWrapper> accountResources = new ResourceWrapper(operation);
        accountResources.link(Link.SELF, String.format("/operations/%d", operation.getId()));
        accountResources.link("from", ResourceUtils.accountLink(operation.getSender()));
        accountResources.link("to", ResourceUtils.accountLink(operation.getRecipient()));
        return HttpResponse.ok(accountResources);
    }
}
