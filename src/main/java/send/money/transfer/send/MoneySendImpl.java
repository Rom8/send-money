package send.money.transfer.send;

import io.micronaut.caffeine.cache.Caffeine;
import io.micronaut.caffeine.cache.LoadingCache;
import send.money.transfer.Operation;

import javax.inject.Singleton;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Singleton
public class MoneySendImpl implements MoneySend {

    private final LoadingCache<Long, Lock> cache = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .build(id -> new ReentrantLock());

    @Override
    public Operation sendMoney(Operation operation, Queue<MoneySendAction> actions) {
        while (!actions.isEmpty()) {
            Long accountId = actions.element().getAccountId(operation);
            Lock lock = cache.get(accountId);
            lock.lock();
            try {
                actions.poll().call(operation);
            } finally {
                lock.unlock();
            }
        }
        return operation;
    }

}
