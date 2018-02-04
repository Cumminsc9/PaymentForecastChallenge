package co.uk.tcummins.utils;

import org.apache.commons.io.input.BOMInputStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.util.List;

/**
 * File:         ParseDataTest
 * Project:      PaymentForecastChallenge
 * Created:      04/02/2018
 * Author:       Tom
 */
public class ParseDataTest
{
    private ParseData parseDataInstance;


    @Test
    public void testSortedTableDataList() throws Exception
    {
        createTestParseCase("csv\\test\\test.csv");

        final List<List<ParseData.TableData>> sortedTableDataList = parseDataInstance.getSortedTableDataList();
        final String[] merchants = { "Sky", "British_Gas", "Vodafone", "Thames_Water", "Tamar_Crossing" };
        final double amounts[] = { 40.8, 50.03, 62.96, 11.11, 5.76 };

        Assert.assertEquals( "The size of sorted table data is 5", 5, sortedTableDataList.size());

        for (int i = 0; i < sortedTableDataList.size(); i++)
        {
            final List<ParseData.TableData> tableData = sortedTableDataList.get(i);
            final DayOfWeek dayOfWeek = DayOfWeek.of(i+1);
            final String merchant = merchants[i];
            final String amount = NumberFormat.getCurrencyInstance().format( amounts[i] );

            Assert.assertEquals( "Nested array is equal to size 1", 1, tableData.size() );
            Assert.assertEquals( "Nested array day is equal to the current day loop", dayOfWeek, tableData.get(0).getDay() );
            Assert.assertEquals( "Nested array merchant is equal to the merchant array element", merchant, tableData.get(0).getMerchant() );
            Assert.assertEquals( "Nested array amount is equal to the amount array element", amount, tableData.get(0).getAmount() );
        }
    }


    @Test
    public void testSortedTableDataList2() throws Exception
    {
        createTestParseCase("csv\\test\\test2.csv");

        final List<List<ParseData.TableData>> sortedTableDataList = parseDataInstance.getSortedTableDataList();
        final String[] merchants = { "Sky", "British_Gas", "Vodafone", "Thames_Water", "Tamar_Crossing", "British_Telecom" };
        final double[] amountsMonday = { 63.96, 91.97, 86.91, 71.15, 49.88, 155.5 };
        final double[] amountsTuesday = { 96.31, 139.04, 65.38, 160.65, 58.11, 53.42 };

        Assert.assertEquals( "The size of sorted table data is 2", 2, sortedTableDataList.size());

        for( int i = 0; i < sortedTableDataList.size(); i++ )
        {
            final List<ParseData.TableData> tableDataList = sortedTableDataList.get(i);
            final DayOfWeek dayOfWeek = DayOfWeek.of(i+1);

            Assert.assertEquals( "Nested array is equal to size 6", 6, tableDataList.size() );
            Assert.assertEquals( "Nested array day is equal to the current day loop", dayOfWeek, tableDataList.get(0).getDay() );

            for( int j = 0; j < tableDataList.size(); j++ )
            {
                final ParseData.TableData tableData = tableDataList.get(j);
                final String merchant = merchants[j];
                String amount;

                if( dayOfWeek.equals(DayOfWeek.MONDAY) )
                {
                    amount = NumberFormat.getCurrencyInstance().format( amountsMonday[j] );
                }
                else
                {
                    amount = NumberFormat.getCurrencyInstance().format( amountsTuesday[j] );
                }

                Assert.assertEquals( "Merchant is equal to the merchant array element", merchant, tableData.getMerchant() );
                Assert.assertEquals( "Amount is equal to the amount array element", amount, tableData.getAmount() );
            }
        }
    }


    private void createTestParseCase( final String fileName ) throws Exception
    {
        if( parseDataInstance != null )
        {
            parseDataInstance.getSortedTableDataList().clear();
        }

        final ClassLoader classLoader = getClass().getClassLoader();
        final URL resource = classLoader.getResource( fileName );
        Reader reader = null;

        if( resource != null )
        {
            reader = new InputStreamReader(new BOMInputStream(resource.openStream()), "UTF-8");
        }

        parseDataInstance = ParseData.getInstance();
        parseDataInstance.parseCSV(reader);
    }
}
