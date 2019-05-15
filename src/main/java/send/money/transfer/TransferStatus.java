package send.money.transfer;

/**
 * Money transfer status
 */
public enum TransferStatus {

    /**
     * Just created request.
     */
    NEW,

    /**
     * Sender does not have enough funds
     */
    NOT_ENOUGH_FUNDS,

    /**
     * Money was withdrawn from the sender account.
     */
    WITHDRAWN,

    /**
     * Money was delivered to the recipient.
     */
    DELIVERED
}
