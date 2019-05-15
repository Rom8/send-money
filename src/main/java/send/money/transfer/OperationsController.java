package send.money.transfer;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.hateos.Resource;
import io.micronaut.http.hateos.VndError;
import io.micronaut.validation.Validated;
import send.money.transfer.send.Deposit;
import send.money.transfer.send.MoneySend;
import send.money.transfer.send.MoneySendAction;
import send.money.transfer.send.Withdraw;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

@Validated
@Controller(OperationsController.OPERATIONS_LINK)
public class OperationsController {

    public static final String OPERATIONS_LINK = "/operations";

    private final OperationService operationService;

    private final MoneySend moneySend;

    @Inject
    public OperationsController(OperationService operationService,
                                MoneySend moneySend) {
        this.operationService = operationService;
        this.moneySend = moneySend;
    }

    @Post(produces = MediaType.APPLICATION_HAL_JSON, consumes = MediaType.APPLICATION_JSON)
    public CompletableFuture<HttpResponse<Resource>> send(@Valid @Body Operation moneyTransfer) {
        return CompletableFuture
                .supplyAsync(() -> operationService.createNewOperation(moneyTransfer))
                .thenApplyAsync(this::sendMoney)
                .thenApplyAsync(operationService::createOperationResource)
                .exceptionally(this::exceptionalResponse);
    }

    private Operation sendMoney(Operation operation) {
        Queue<MoneySendAction> actions = new LinkedList<>();
        actions.add(new Withdraw(operationService));
        actions.add(new Deposit(operationService));
        moneySend.sendMoney(operation, actions);
        return operation;
    }

    private HttpResponse<Resource> exceptionalResponse(Throwable throwable) {
        if (throwable.getCause() instanceof MoneyTransferException) {
            MoneyTransferException moneyTransferException = (MoneyTransferException) throwable.getCause();
            return moneyTransferException.getResponse();
        } else {
            return HttpResponse.serverError(new VndError(throwable.getMessage()));
        }
    }

    @Get(value = "/{id}")
    public CompletableFuture<HttpResponse<Resource>> get(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> operationService.findOperation(id));
    }
}
