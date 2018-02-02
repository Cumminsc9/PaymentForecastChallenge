package co.uk.tcummins.utils;

import co.uk.tcummins.obj.Merchant;
import co.uk.tcummins.obj.Payment;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * File: ParseCSV Project: PaymentForecastChallenge Created: 01/02/2018 Author: Tom
 */
public class ParseCSV
{
    public List<Merchant> readCSV()
    {
        final Reader fileReader = locateFile();
        if( fileReader == null )
        {
            return new ArrayList<>();
        }

        CSVParser parser = null;

        try
        {
            parser = new CSVParser( fileReader, CSVFormat.EXCEL.withHeader() );

            int prevID = 0;
            Merchant merchant = null;

            final List<Merchant> merchantList = new ArrayList<>();

            for( CSVRecord record : parser.getRecords() )
            {
                final int merchantID = Integer.parseInt( record.get( "MerchantId" ) );
                final String merchantName = record.get( "MerchantName" );
                final String merchantPubKey = record.get( "MerchantPubKey" );

                if( merchantID != prevID )
                {
                    merchant = new Merchant( merchantID, merchantName, merchantPubKey );
                    merchantList.add( merchant );
                }

                final Payment payment = parsePayment( record, merchantPubKey );
                if( merchant != null && payment != null )
                {
                    merchant.addPayment( payment );
                }

                prevID = merchantID;
            }

            merchantList.forEach( System.out::println );
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
        finally
        {
            if( parser != null )
            {
                try
                {
                    parser.close();
                }
                catch( IOException e )
                {
                    e.printStackTrace();
                }
            }
        }

        return new ArrayList<>();
    }


    private Payment parsePayment( final CSVRecord record, final String merchantPubKey )
    {
        try
        {
            final int payerID = Integer.parseInt( record.get( "PayerId" ) );
            final String payerPubKey = record.get( "PayerPubKey" );

            final LocalDateTime dueUTC = LocalDateTime.ofInstant( Instant.parse( record.get( "DueUTC" ) ),
                    ZoneOffset.ofHours( 0 ) );
            final LocalDateTime receivedUTC = LocalDateTime.ofInstant( Instant.parse( record.get( "ReceivedUTC" ) ),
                    ZoneOffset.ofHours( 0 ) );
            final long dueEpoch = Long.parseLong( record.get( "DueEpoc" ) );

            final int debitPermissionId = Integer.parseInt( record.get( "DebitPermissionId" ) );
            final String currency = record.get( "Currency" );
            final double amount = Double.parseDouble( record.get( "Amount" ) );

            final String SHA256 = record.get( "SHA256(MerchantPubKeyPayerPubKeyDebitPermissionIdDueEpocAmount)" );

            final boolean validHash = calculateSecurityHash( SHA256, merchantPubKey, payerPubKey, debitPermissionId, dueEpoch,
                    amount );

            return new Payment( payerID, payerPubKey, dueUTC, receivedUTC, dueEpoch, debitPermissionId, currency, amount, SHA256,
                    validHash );
        }
        catch( Exception ignored )
        {

        }

        return null;
    }


    private boolean calculateSecurityHash( String SHA256,
                                           String merchantPubKey,
                                           String payerPubKey,
                                           int debitPermissionId,
                                           long dueEpoch,
                                           double amount )
    {
        final String appendedString = new StringBuilder().append( merchantPubKey )
                .append( payerPubKey )
                .append( debitPermissionId )
                .append( dueEpoch )
                .append( amount )
                .toString();

        try
        {
            final MessageDigest messageDigest = MessageDigest.getInstance( "SHA-256" );
            final String validationString = DatatypeConverter
                    .printHexBinary( messageDigest.digest( appendedString.getBytes( StandardCharsets.UTF_8 ) ) );

            //System.out.println( SHA256.toLowerCase() );
            //System.out.println( validationString.toLowerCase() );
            //System.out.println( "---------------------------------------------" );

            if( SHA256.equalsIgnoreCase( validationString ) )
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


    private Reader locateFile()
    {
        try
        {
            final String fileName = "payment-forecast-data.csv";
            //final String fileName = "test.csv";
            final ClassLoader classLoader = getClass().getClassLoader();
            final URL resource = classLoader.getResource( fileName );

            if( resource != null )
            {
                return new InputStreamReader( new BOMInputStream( resource.openStream() ), "UTF-8" );
            }
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }

        return null;
    }
}
