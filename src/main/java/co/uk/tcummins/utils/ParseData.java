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
import java.util.Comparator;
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
    private final List<List<ParseData.TableData>> sortedDataTableList;


    private ParseData()
    {
        this.merchantList = new ArrayList<>();
        this.tableDataList = new ArrayList<>();
        this.sortedDataTableList = new ArrayList<>();
    }


    /**
     * If ParseData has not yet been created, create a single instance and return it. Otherwise return
     * the current created instance.
     *
     * @return The ParseData instance.
     */
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


    /**
     * @return The sorted data to be inserted into the UI table
     */
    public List<List<ParseData.TableData>> getSortedTableDataList()
    {
        return sortedDataTableList;
    }


    /**
     * sortTableData() sorts the data into a format that can be easily used by
     * the front end UI to display the data in a simple way.
     */
    private void sortTableData()
    {
        tableDataList.sort(Comparator.comparingInt(o -> o.getDay().getValue()));

        DayOfWeek prevDay = null;
        List<ParseData.TableData> selectSortedTableData = new ArrayList<>();
        for (ParseData.TableData tableData : tableDataList)
        {
            if(prevDay == null)
            {
                prevDay = tableData.getDay();
            }

            if( !tableData.getDay().equals(prevDay) )
            {
                sortedDataTableList.add( selectSortedTableData );
                selectSortedTableData = new ArrayList<>();
            }

            selectSortedTableData.add(tableData);

            prevDay = tableData.getDay();
        }
        sortedDataTableList.add( selectSortedTableData );
    }


    /**
     * calculateMerchantTotals() iterates over the current parsed data
     * and possibly moves the payment to the correct day, and then calculates
     * the correct amount for the Merchant on the specified day.
     */
    private void calculateMerchantTotals()
    {
        double dayAmount = 0;
        DayOfWeek previousDay = null;
        String previousName = "";

        for( Merchant merchant : merchantList )
        {
            for( Payment payment : merchant.getPayments() )
            {
                if( payment.getPaymentDue().get(ChronoField.CLOCK_HOUR_OF_DAY) >= 16 &&
                    payment.getPaymentDue().get(ChronoField.CLOCK_HOUR_OF_DAY) != 24 )
                {
                    payment.setPaymentDue( payment.getPaymentDue().plusDays(1) );
                }

                dayAmount += payment.getAmount();

                if(previousDay == null)
                {
                    previousDay = payment.getPaymentDue().getDayOfWeek();
                }

                if( !payment.getPaymentDue().getDayOfWeek().equals( previousDay ) )
                {
                    dayAmount = Math.round(dayAmount * 100.0) / 100.0;
                    tableDataList.add( new TableData( previousDay, previousName, dayAmount ) );
                    dayAmount = 0;
                }

                previousDay = payment.getPaymentDue().getDayOfWeek();
                previousName = merchant.getMerchantName();
            }
        }
        tableDataList.add( new TableData( previousDay, previousName, dayAmount ) );
        sortTableData();
    }


    /**
     * The POJO that defines the data that will be inserted into the UI table
     */
    public class TableData
    {
        private DayOfWeek day;
        private String merchant;
        private double amount;


        TableData( DayOfWeek day, String merchant, double amount )
        {
            this.day = day;
            this.merchant = merchant;
            this.amount = amount;
        }


        public DayOfWeek getDay()
        {
            return day;
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


    /**
     * parseCSV() Reads the content of the passed in reader and utilises the CSVParser class
     * to easily parse and read the CSV file.
     *
     * @param reader The character stream of the passed in CSV file.
     */
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
            Logger.getInstance().log("Error during parsing of file: " + e.getMessage(), ParseData.class.getName(), Log.LogLevel.ERROR);
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
                    Logger.getInstance().log("Error during parsing of file: "+ e.getMessage(), ParseData.class.getName(), Log.LogLevel.ERROR);
                }
            }
        }
    }


    /**
     * parsePaymentRecord() parses the actual payment for the passed record, this is then returned
     * to the parseCSV() method that assigns the returned Payment POJO to the correct Merchant POJO.
     *
     * @param record The CSVRecord to parse.
     * @param merchantPubKey The merchantPubKey that's used to compute the SHA-256 hash.
     *
     * @return A Payment POJO that is linked to the correct Merchant POJO. Otherwise return if there has been
     *          an error during parsing.
     */
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
