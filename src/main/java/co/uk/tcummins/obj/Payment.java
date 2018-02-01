package co.uk.tcummins.obj;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

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


    public int getPayerID()
    {
        return payerID;
    }


    public void setPayerID( int payerID )
    {
        this.payerID = payerID;
    }


    public String getPayerPublicKey()
    {
        return payerPublicKey;
    }


    public void setPayerPublicKey( String payerPublicKey )
    {
        this.payerPublicKey = payerPublicKey;
    }


    public LocalDateTime getPaymentDue()
    {
        return paymentDue;
    }


    public void setPaymentDue( LocalDateTime paymentDue )
    {
        this.paymentDue = paymentDue;
    }


    public LocalDateTime getPaymentReceived()
    {
        return paymentReceived;
    }


    public void setPaymentReceived( LocalDateTime paymentReceived )
    {
        this.paymentReceived = paymentReceived;
    }


    public long getDueEpoch()
    {
        return dueEpoch;
    }


    public void setDueEpoch( long dueEpoch )
    {
        this.dueEpoch = dueEpoch;
    }


    public int getDebitPermissionID()
    {
        return debitPermissionID;
    }


    public void setDebitPermissionID(int debitPermissionID)
    {
        this.debitPermissionID = debitPermissionID;
    }


    public String getCurrency()
    {
        return currency;
    }


    public void setCurrency( String currency )
    {
        this.currency = currency;
    }


    public double getAmount()
    {
        return amount;
    }


    public void setAmount( double amount )
    {
        this.amount = amount;
    }


    public String getSecurityHash()
    {
        return securityHash;
    }


    public void setSecurityHash( String securityHash )
    {
        this.securityHash = securityHash;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "payerID=" + payerID +
                ", payerPublicKey='" + payerPublicKey + '\'' +
                ", paymentDue=" + paymentDue +
                ", paymentReceived=" + paymentReceived +
                ", dueEpoch=" + dueEpoch +
                ", currency='" + currency + '\'' +
                ", debitPermissionID=" + debitPermissionID +
                ", amount=" + amount +
                ", securityHash='" + securityHash + '\'' +
                ", validHash=" + validHash +
                '}';
    }
}
