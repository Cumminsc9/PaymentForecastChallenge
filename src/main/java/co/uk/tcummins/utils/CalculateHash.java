package co.uk.tcummins.utils;

import co.uk.tcummins.objs.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * File: CalculateHash
 * Project: PaymentForecastChallenge
 * Created: 02/02/2018
 * Author: Tom
 */
class CalculateHash
{
    private static final char[] HEXCODE = "0123456789abcdef".toCharArray();


    /**
     * calculateSecurityHash() attempts to validate the security hash, by concatenating the
     * required fields and creating a replica of the original SHA-256 hash as retrieved in the CSV
     * file.
     *
     * @param SHA256 The original SHA-256 string as received from the CSV file.
     * @param merchantPubKey The merchantPubKey as received from the CSV file.
     * @param payerPubKey The payerPubKey as received from the CSV file.
     * @param debitPermissionId The debitPermissedId as received from the CSV file.
     * @param dueEpoch The dueEpoch as received from the CSV file.
     * @param amount The amount as received from the CSV file.
     *
     * @return A boolean value depending on if the validation of the SHA-256 hash was successful.
     *          returns true for a successful hash. Otherwise return false.
     */
    static boolean calculateSecurityHash(String SHA256,
                                         String merchantPubKey,
                                         String payerPubKey,
                                         int debitPermissionId,
                                         long dueEpoch,
                                         String amount)
    {
        final String unhashedString = new StringBuilder()
                .append( merchantPubKey )
                .append( payerPubKey )
                .append( debitPermissionId )
                .append( dueEpoch )
                .append( amount )
                .toString();

        try
        {
            final MessageDigest messageDigest = MessageDigest.getInstance( "SHA-256" );
            final byte[] digestedByteArray = messageDigest.digest( unhashedString.getBytes( StandardCharsets.UTF_8 ) );
            final StringBuilder hashedString = new StringBuilder( digestedByteArray.length * 2 );
            for( byte b : digestedByteArray )
            {
                // Right bit shift operation
                hashedString.append( HEXCODE[(b >>> 4) & 0xF] );

                // Bitwise AND operation
                hashedString.append( HEXCODE[(b & 0xF)] );
            }

            if( SHA256.equals( hashedString.toString() ) )
            {
                return true;
            }
        }
        catch( NoSuchAlgorithmException e )
        {
            Logger.getInstance().log( "Error computing secure hash: " + e.getMessage(), CalculateHash.class.getName(), Log.LogLevel.FATAL );
            e.printStackTrace();
        }

        Logger.getInstance().log( "Unsuccessful hash validation: " + SHA256, CalculateHash.class.getName(), Log.LogLevel.ERROR );
        return false;
    }
}
