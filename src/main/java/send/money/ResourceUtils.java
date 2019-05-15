package send.money;

import static send.money.accounts.AccountsController.*;

public class ResourceUtils {

    public static String accountLink(long accountId) {
        return String.format("%s/%d", ACCOUNTS_LINK, accountId);
    }
}
