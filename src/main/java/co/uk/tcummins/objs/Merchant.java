package co.uk.tcummins.objs;

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


    public Merchant(int merchantID, String merchantName, String merchantPublicKey) {
        this.merchantID = merchantID;
        this.merchantName = merchantName;
        this.merchantPublicKey = merchantPublicKey;
        this.payments = new ArrayList<>();
    }

    public void addPayment(final Payment payment )
    {
        this.getPayments().add(payment);
    }

    public int getMerchantID() {
        return merchantID;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getMerchantPublicKey() {
        return merchantPublicKey;
    }

    public List<Payment> getPayments() {
        return payments;
    }


    @Override
    public String toString() {
        return "Merchant: " +
                "\nID: " + merchantID +
                "\nName: " + merchantName +
                "\nPublicKey: " + merchantPublicKey +
                "\nPayments: " + payments + "\n";
    }
}

