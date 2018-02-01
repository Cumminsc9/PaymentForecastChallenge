package co.uk.tcummins.obj;

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
        this.merchantID = merchantID;
        this.merchantName = merchantName;
        this.merchantPublicKey = merchantPublicKey;
    }


    public List<Payment> getPayments()
    {
        return payments;
    }


    public void setPayments( List<Payment> payments )
    {
        this.payments = payments;
    }

    @Override
    public String toString() {
        return "Merchant{" +
                "merchantID=" + merchantID +
                ", merchantName='" + merchantName + '\'' +
                ", merchantPublicKey='" + merchantPublicKey + '\'' +
                ", payments=" + payments +
                '}';
    }
}

