package co.uk.tcummins.utils;

import co.uk.tcummins.obj.Merchant;
import co.uk.tcummins.obj.Payment;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * File: ParseCSV Project: PaymentForecastChallenge Created: 01/02/2018 Author: Tom
 */
public class ParseCSV
{
    public List<Merchant> parseCSV()
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

                final Payment payment = parsePaymentRecord( record, merchantPubKey );
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


    private Payment parsePaymentRecord( final CSVRecord record, final String merchantPubKey )
    {
        try
        {
            final int payerID = Integer.valueOf( record.get( "PayerId" ) );
            final String payerPubKey = record.get( "PayerPubKey" );
            final LocalDateTime dueUTC = LocalDateTime.ofInstant( Instant.parse( record.get( "DueUTC" ) ),
                    ZoneOffset.ofHours( 0 ) );
            final LocalDateTime receivedUTC = LocalDateTime.ofInstant( Instant.parse( record.get( "ReceivedUTC" ) ),
                    ZoneOffset.ofHours( 0 ) );
            final long dueEpoch = Long.parseLong( record.get( "DueEpoc" ) );
            final int debitPermissionId = Integer.valueOf( record.get( "DebitPermissionId" ) );
            final String currency = record.get( "Currency" );
            final double amount = Double.valueOf( record.get( "Amount" ) );
            final String formattedAmount = String.format( "%.2f", amount );
            final String SHA256 = record.get( "SHA256(MerchantPubKeyPayerPubKeyDebitPermissionIdDueEpocAmount)" );

            final boolean validHash = CalculateHash.calculateSecurityHash( SHA256, merchantPubKey, payerPubKey, debitPermissionId,
                    dueEpoch, formattedAmount );

            return new Payment( payerID, payerPubKey, dueUTC, receivedUTC, dueEpoch, debitPermissionId, currency, amount, SHA256,
                    validHash );
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
        }

        return null;
    }





    private Reader locateFile()
    {
        try
        {
            //final String fileName = "payment-forecast-data.csv";
            final String fileName = "test.csv";
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
