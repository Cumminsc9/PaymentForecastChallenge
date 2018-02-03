package co.uk.tcummins.objs;

import java.time.LocalDateTime;

/**
 * File: Payment Project: PaymentForecastChallenge Created: 01/02/2018 Author: Tom
 */
public class Payment
{
    private int payerID;
    private String payerPublicKey;

    private LocalDateTime paymentDue;
    private LocalDateTime paymentReceived;
    private long dueEpoch;

    private String currency;
    private int debitPermissionID;
    private double amount;

    private String securityHash;
    private boolean validHash;

    public Payment() {
    }

    public Payment(int payerID, String payerPublicKey, LocalDateTime paymentDue, LocalDateTime paymentReceived, long dueEpoch, int debitPermissionID, String currency, double amount, String securityHash, boolean validHash )
    {
        this.payerID = payerID;
        this.payerPublicKey = payerPublicKey;
        this.paymentDue = paymentDue;
        this.paymentReceived = paymentReceived;
        this.dueEpoch = dueEpoch;
        this.debitPermissionID = debitPermissionID;
        this.currency = currency;
        this.amount = amount;
        this.securityHash = securityHash;
        this.validHash = validHash;
    }

    @Override
    public String toString() {
        return "Payment: " +
                "\n\tID: " + payerID +
                "\n\tPublicKey: " + payerPublicKey +
                "\n\tDue: " + paymentDue +
                "\n\tReceived: " + paymentReceived +
                "\n\tDueEpoch: " + dueEpoch +
                "\n\tCurrency: " + currency +
                "\n\tDebitPermissionID: " + debitPermissionID +
                "\n\tAmount: " + amount +
                "\n\tSecurityHash: " + securityHash +
                "\n\tValidHash: " + validHash + "\n";

    }
}