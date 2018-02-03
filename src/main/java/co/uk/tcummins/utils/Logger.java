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
 * File: Logger Project: PaymentForecastChallenge Created: 02/02/2018 Author: Tom
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


    public void log( final String logMessage, String className, final Log.LogLevel logLevel)
    {
        logList.add( new Log( LocalDateTime.now( ZoneId.systemDefault() ), className, logMessage, logLevel ) );
    }


    public void logParserError( final CSVRecord record, final long recordNum, final String exceptionMessage )
    {
        try
        {
            final Field valueField = record.getClass().getDeclaredField("values");
            valueField.setAccessible(true);
            final String[] values = (String[]) valueField.get( record );

            parseErrorList.add( new Log(
                    LocalDateTime.now( ZoneId.systemDefault() ),
                    ParseCSV.class.getName(),
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


    public List<Log> getLogs()
    {
        return logList;
    }


    public List<Log> getParserErrors()
    {
        return parseErrorList;
    }

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

