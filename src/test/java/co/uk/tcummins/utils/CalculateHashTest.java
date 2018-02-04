package co.uk.tcummins.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * File: CalculateHashTest Project: PaymentForecastChallenge Created: 04/02/2018 Author: Tom
 */
public class CalculateHashTest
{
    private final String shaString = "b06458828fb49db805278a739039de05a24a22c2ea165b8a70dd2067504bb76f";
    private final String merchantPubKey = "qx6uZRXaTLQpiiTafm6X";
    private final String payerPubKey = "VysvJW1Ox2dMNdK2Oibl";
    private final String amount = "98.54";
    private final int debitPermissionId = 7896634;
    private final long dueEpoch = 1578347022;


    @Test
    public void testValidSHAHash() throws Exception
    {
        boolean result = CalculateHash.calculateSecurityHash( shaString, merchantPubKey, payerPubKey, debitPermissionId, dueEpoch,
                amount );

        Assert.assertEquals( "Outcome should have been successful", true, result );
    }


    @Test
    public void testInvalidSHAHash() throws Exception
    {
        final String tempSHAHash = shaString + "123";
        boolean result = CalculateHash.calculateSecurityHash( tempSHAHash, merchantPubKey, payerPubKey, debitPermissionId,
                dueEpoch, amount );

        Assert.assertEquals( "Outcome should have been unsuccessful", false, result );
    }


    @Test
    public void testInvalidMerchantPubKey() throws Exception
    {
        final String tempMerchantPubKey = merchantPubKey + "123";
        boolean result = CalculateHash.calculateSecurityHash( shaString, tempMerchantPubKey, payerPubKey, debitPermissionId,
                dueEpoch, amount );

        Assert.assertEquals( "Outcome should have been unsuccessful", false, result );
    }


    @Test
    public void testInvalidAmount() throws Exception
    {
        final String tempAmount = amount.replace( "98", "100" );
        boolean result = CalculateHash.calculateSecurityHash( shaString, merchantPubKey, payerPubKey, debitPermissionId, dueEpoch,
                tempAmount );

        Assert.assertEquals( "Outcome should have been unsuccessful", false, result );
    }


    @Test
    public void testEmptyValues() throws Exception
    {
        boolean result = CalculateHash.calculateSecurityHash( shaString, "", "", 0, 0, "" );

        Assert.assertEquals( "Outcome should have been unsuccessful", false, result );
    }


    @Test
    public void testNullValues() throws Exception
    {
        boolean result = CalculateHash.calculateSecurityHash( shaString, null, null, 0, 0, null );

        Assert.assertEquals( "Outcome should have been unsuccessful", false, result );
    }


    @Test
    public void testEmptySHAString() throws Exception
    {
        boolean result = CalculateHash.calculateSecurityHash( "", merchantPubKey, payerPubKey, debitPermissionId, dueEpoch,
                amount );

        Assert.assertEquals( "Outcome should have been unsuccessful", false, result );
    }
}
