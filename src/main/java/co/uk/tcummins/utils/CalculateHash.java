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


    static boolean calculateSecurityHash(String SHA256,
                                         String merchantPubKey,
                                         String payerPubKey,
                                         int debitPermissionId,
                                         long dueEpoch,
                                         String amount)
    {
        final String unhashedString = new StringBuilder().append( merchantPubKey )
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
                hashedString.append( HEXCODE[(b >> 4) & 0xF] );
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
