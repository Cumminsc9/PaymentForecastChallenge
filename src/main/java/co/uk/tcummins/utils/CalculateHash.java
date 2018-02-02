package co.uk.tcummins.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Tom on 02/02/2018.
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
            e.printStackTrace();
        }

        return false;
    }
}
