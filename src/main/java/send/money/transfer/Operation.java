package send.money.transfer;

import javax.persistence.*;

/**
 * A money send operation entity.
 */
@Entity
@Table(name = "operation")
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private long sender;

    @Column
    private long recipient;

    @Column
    private long amount;

    @Column
    private volatile TransferStatus transferStatus;

    public Operation() {
    }

    public Operation(long sender, long recipient, long amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        transferStatus = TransferStatus.NEW;
    }

    public Long getId() {
        return id;
    }

    public long getSender() {
        return sender;
    }

    public void setSender(long sender) {
        this.sender = sender;
    }

    public long getRecipient() {
        return recipient;
    }

    public void setRecipient(long recipient) {
        this.recipient = recipient;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public TransferStatus getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(TransferStatus transferStatus) {
        this.transferStatus = transferStatus;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "id=" + id +
                ", sender=" + sender +
                ", recipient=" + recipient +
                ", amount=" + amount +
                ", transferStatus=" + transferStatus +
                '}';
    }
}
