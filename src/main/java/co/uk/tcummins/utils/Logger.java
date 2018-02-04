package co.uk.tcummins.utils;

import co.uk.tcummins.objs.Log;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * File: Logger
 * Project: PaymentForecastChallenge
 * Created: 02/02/2018
 * Author: Tom
 */
public class Logger
{
    private final static Object SINGLETON = new Object();

    private static Logger instance;

    private final List<Log> logList;
    private final List<Log> parseErrorList;


    private Logger()
    {
        logList = new ArrayList<>();
        parseErrorList = new ArrayList<>();
    }


    /**
     * If ParseData has not yet been created, create a single instance and return it. Otherwise return
     * the current created instance.
     * *
     * @return The Logger instance.
     */
    public static Logger getInstance()
    {
        synchronized( SINGLETON )
        {
            if( instance == null )
            {
                instance = new Logger();
            }
        }

        return instance;
    }


    /**
     * log() appends the Log message to the log list.
     *
     * @param logMessage The log message, description or stack trace message.
     * @param className The class where the log occurred.
     * @param logLevel The level of the log depending of severity.
     */
    public void log( final String logMessage, String className, final Log.LogLevel logLevel)
    {
        logList.add( new Log( LocalDateTime.now( ZoneId.systemDefault() ), className, logMessage, logLevel ) );
    }


    /**
     * logParserError() is used to track parsing errors when attempting to parse
     * the CSV file.
     *
     * @param record The record which could not be parsed
     * @param recordNum The record number of the CSV file
     * @param exceptionMessage The exception message received from the parsing error
     */
    void logParserError(final CSVRecord record, final long recordNum, final String exceptionMessage)
    {
        try
        {
            final Field valueField = record.getClass().getDeclaredField("values");
            valueField.setAccessible(true);
            final String[] values = (String[]) valueField.get( record );

            parseErrorList.add( new Log(
                    LocalDateTime.now( ZoneId.systemDefault() ),
                    ParseData.class.getName(),
                    "Error parsing record: " + recordNum + "\r\n" +
                               "Message: " + exceptionMessage + "\r\n" +
                               "Values: " + Arrays.toString( values ), Log.LogLevel.ERROR ) );
        }
        catch (Exception ex)
        {
            Logger.getInstance().log( "Error attempting to get value field: " + ex.getMessage(),
                    Logger.class.getName(), Log.LogLevel.ERROR );
        }
    }


    /**
     * @return The standard logs.
     */
    public List<Log> getLogs()
    {
        return logList;
    }


    /**
     * @return The parser error logs.
     */
    public List<Log> getParserErrors()
    {
        return parseErrorList;
    }


    /**
     * Creates a log file from the logs depending on which logs the user requests.
     *
     * @param fileName The filename the log file will be generated with.
     * @param logList The logs the user has requested.
     *
     * @return A byte[] of the written log file.
     */
    public byte[] getLogFile( final String fileName, final List<Log> logList )
    {
        try
        {
            final BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( fileName ) );
            bufferedWriter.write( "Log Time, Log Location, Log Level, Log Message" );
            bufferedWriter.newLine();
            bufferedWriter.flush();

            for( Log log : logList )
            {

                bufferedWriter.write( log.getLogTime() + ", "
                                        + log.getLogLocation() + ", "
                                        + log.getLogLevel() + ", "
                                        + log.getLogMessage() );
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

            return Files.readAllBytes( Paths.get( fileName ) );
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }

        return null;
    }
}

