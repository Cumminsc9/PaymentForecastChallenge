package co.uk.tcummins.utils;

import co.uk.tcummins.objs.Log;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

/**
 * File:         LoggerTest
 * Project:      PaymentForecastChallenge
 * Created:      04/02/2018
 * Author:       Tom
 */
public class LoggerTest
{
    private final Logger logger = Logger.getInstance();

    private final String logMessage = "Test logger";
    private final String logLocation = "co.uk.tcummins.Foo";
    private final String logParseLocation = "co.uk.tcummins.utils.ParseData";

    private Reader reader = null;


    @Before
    public void setUp() throws Exception
    {
        final String fileName = "csv\\test\\test.csv";
        final ClassLoader classLoader = getClass().getClassLoader();
        final URL resource = classLoader.getResource( fileName );

        if( resource != null )
        {
            reader = new InputStreamReader(new BOMInputStream(resource.openStream()), "UTF-8");
        }

        logger.getLogs().clear();
        logger.getParserErrors().clear();
    }


    @Test
    public void testLogFunction() throws Exception
    {
        logger.log(logMessage, logLocation, Log.LogLevel.INFO);

        final Log log = logger.getLogs().get(0);

        Assert.assertEquals( "Log list should contain one log message", 1, logger.getLogs().size() );

        Assert.assertEquals( "Log message should be the same as the one passed in", logMessage, log.getLogMessage());
        Assert.assertEquals( "Log location should be the same as the one passed in", logLocation, log.getLogLocation());
        Assert.assertEquals( "Log level should be the same as the one passed in", Log.LogLevel.INFO, log.getLogLevel());
    }


    @Test
    public void testLogFunctionMultipleLogs() throws Exception
    {
        logger.log(logMessage, logLocation, Log.LogLevel.INFO);
        logger.log(logMessage, logLocation, Log.LogLevel.ERROR);

        final Log log1 = logger.getLogs().get(0);
        final Log log2 = logger.getLogs().get(1);

        Assert.assertEquals( "Log list should contain two log messages", 2, logger.getLogs().size() );

        Assert.assertEquals( "Log message should be the same as the one passed in", logMessage, log1.getLogMessage());
        Assert.assertEquals( "Log location should be the same as the one passed in", logLocation, log1.getLogLocation());
        Assert.assertEquals( "Log level should be the same as the one passed in", Log.LogLevel.INFO, log1.getLogLevel());

        Assert.assertEquals( "Log message should be the same as the one passed in", logMessage, log2.getLogMessage());
        Assert.assertEquals( "Log location should be the same as the one passed in", logLocation, log2.getLogLocation());
        Assert.assertEquals( "Log level should be the same as the one passed in", Log.LogLevel.ERROR, log2.getLogLevel());
    }

    @Test
    public void testLogParerError() throws Exception
    {
        final CSVRecord csvRecord = new CSVParser(reader, CSVFormat.EXCEL.withHeader()).getRecords().get(0);
        logger.logParserError( csvRecord, 0, "Test Exception" );

        final Log log = logger.getParserErrors().get(0);

        Assert.assertEquals( "Log list should contain one parse log message", 1, logger.getParserErrors().size() );

        Assert.assertEquals( "Log location should be co.uk.tcummins.ParseData", logParseLocation, log.getLogLocation());
        Assert.assertEquals( "Log level should be ERROR", Log.LogLevel.ERROR, log.getLogLevel());
    }

    @Test
    public void testMultipleLogParerError() throws Exception
    {
        final CSVRecord csvRecord = new CSVParser(reader, CSVFormat.EXCEL.withHeader()).getRecords().get(0);
        logger.logParserError( csvRecord, 0, "Test Exception" );
        logger.logParserError( csvRecord, 1, "Test Exception" );

        final Log log1 = logger.getParserErrors().get(0);
        final Log log2 = logger.getParserErrors().get(1);

        Assert.assertEquals( "Log list should contain two parser log messages", 2, logger.getParserErrors().size() );

        Assert.assertEquals( "Log location should be co.uk.tcummins.ParseData", logParseLocation, log1.getLogLocation());
        Assert.assertEquals( "Log level should be ERROR", Log.LogLevel.ERROR, log1.getLogLevel());

        Assert.assertEquals( "Log location should be co.uk.tcummins.ParseData", logParseLocation, log2.getLogLocation());
        Assert.assertEquals( "Log level should be ERROR", Log.LogLevel.ERROR, log2.getLogLevel());
    }
}
