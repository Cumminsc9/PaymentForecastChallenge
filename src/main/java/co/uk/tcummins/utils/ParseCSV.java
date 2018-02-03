package co.uk.tcummins.utils;

import co.uk.tcummins.objs.Log;
import co.uk.tcummins.objs.Merchant;
import co.uk.tcummins.objs.Payment;
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
    private final static Object SINGLETON = new Object();

    private static ParseCSV instance;

    private final List<Merchant> merchantList;


    private ParseCSV()
    {
        merchantList = new ArrayList<>();
    }


    public static ParseCSV getInstance()
    {
        synchronized( SINGLETON )
        {
            if( instance == null )
            {
                instance = new ParseCSV();
            }
        }

        return instance;
    }


    public List<Merchant> getMerchantList()
    {
        return merchantList;
    }


    public void parseCSV(final Reader reader )
    {
        if( reader == null )
        {
            return;
        }

        CSVParser parser = null;

        try
        {
            parser = new CSVParser( reader, CSVFormat.EXCEL.withHeader() );

            int prevID = 0;
            Merchant merchant = null;
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
            final long recordNum = record.getRecordNumber()+1;
            Logger.getInstance().log( "Error parsing record: " + recordNum + ", " +
                    ex.getMessage(), ParseCSV.class.getName(), Log.LogLevel.ERROR );
            Logger.getInstance().logParserError( record, recordNum, ex.getMessage() );
        }

        return null;
    }
}
