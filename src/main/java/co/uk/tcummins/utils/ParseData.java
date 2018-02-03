package co.uk.tcummins.utils;

import co.uk.tcummins.objs.Log;
import co.uk.tcummins.objs.Merchant;
import co.uk.tcummins.objs.Payment;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

/**
 * File: ParseData
 * Project: PaymentForecastChallenge
 * Created: 02/02/2018
 * Author: Tom
 */
public class ParseData
{
    private final static Object SINGLETON = new Object();

    private static ParseData instance;

    private final List<TableData> tableDataList;
    private final List<Merchant> merchantList;


    private ParseData()
    {
        merchantList = new ArrayList<>();
        tableDataList = new ArrayList<>();
    }


    public static ParseData getInstance()
    {
        synchronized( SINGLETON )
        {
            if( instance == null )
            {
                instance = new ParseData();
            }
        }

        return instance;
    }


    public List<TableData> getTableDataList()
    {
        return tableDataList;
    }


    private void calculateMerchantTotals()
    {

        double dayAmount = 0;
        DayOfWeek previousDay = null;

        for( Merchant merchant : merchantList )
        {
            for( Payment payment : merchant.getPayments() )
            {
                final LocalDateTime date = payment.getPaymentDue();

                if( date.get(ChronoField.CLOCK_HOUR_OF_DAY) > 16 && date.get(ChronoField.CLOCK_HOUR_OF_DAY) != 24 )
                {
                    payment.setPaymentDue( payment.getPaymentDue().plusDays(1) );
                }

                dayAmount += payment.getAmount();

                if( !date.getDayOfWeek().equals(previousDay) )
                {
                    dayAmount = Math.round(dayAmount * 100.0) / 100.0;
                    tableDataList.add( new TableData( date.getDayOfWeek().toString(), merchant.getMerchantName(), dayAmount ) );
                    dayAmount = 0;
                }

                previousDay = date.getDayOfWeek();
            }
        }

        System.out.println( tableDataList );
    }


    class TableData
    {
        private String day;
        private String merchant;
        private double amount;


        TableData( String day, String merchant, double amount )
        {
            this.day = day;
            this.merchant = merchant;
            this.amount = amount;
        }

        public String getAmount()
        {
            return NumberFormat.getCurrencyInstance().format(amount);
        }

        @Override
        public String toString()
        {
            return "\nTableData: " + "\n\tDay: " + day + "\n\tName: " + merchant + "\n\tAmount: " + amount;
        }
    }


    public void parseCSV( final Reader reader )
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

            calculateMerchantTotals();
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
            final long recordNum = record.getRecordNumber() + 1;
            Logger.getInstance().log( "Error parsing record: " + recordNum + ", " + ex.getMessage(), ParseData.class.getName(),
                    Log.LogLevel.ERROR );
            Logger.getInstance().logParserError( record, recordNum, ex.getMessage() );
        }

        return null;
    }
}
