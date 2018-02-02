package co.uk.tcummins.obj;

import java.util.ArrayList;
import java.util.List;

/**
 * File: Merchant Project: PaymentForecastChallenge Created: 01/02/2018 Author: Tom
 */
public class Merchant
{
    private int merchantID;
    private String merchantName;
    private String merchantPublicKey;
    private List<Payment> payments;


    public Merchant( int merchantID, String merchantName, String merchantPublicKey )
    {
        this.setMerchantID(merchantID);
        this.setMerchantName(merchantName);
        this.setMerchantPublicKey(merchantPublicKey);
        this.setPayments(new ArrayList<>());
    }

    public int getMerchantID() {
        return merchantID;
    }

    public void setMerchantID(int merchantID) {
        this.merchantID = merchantID;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantPublicKey() {
        return merchantPublicKey;
    }

    public void setMerchantPublicKey(String merchantPublicKey) {
        this.merchantPublicKey = merchantPublicKey;
    }

    public void addPayment( final Payment payment )
    {
        this.getPayments().add(payment);
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    @Override
    public String toString() {
        return "Merchant{" +
                "merchantID=" + getMerchantID() +
                ", merchantName='" + getMerchantName() + '\'' +
                ", merchantPublicKey='" + getMerchantPublicKey() + '\'' +
                ", payments=" + getPayments() +
                '}';
    }
}

