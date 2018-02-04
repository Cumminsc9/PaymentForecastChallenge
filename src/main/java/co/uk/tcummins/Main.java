package co.uk.tcummins;

import co.uk.tcummins.objs.Log;
import co.uk.tcummins.utils.Logger;
import co.uk.tcummins.utils.ParseData;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

/**
 * File:         Main
 * Project:      PaymentForecastChallenge
 * Created:      02/02/2018
 * Author:       Tom
 */
@SpringBootApplication
public class Main
{
    /**
     * Entry point to the Spring Application.
     *
     * @param args Arguments, are not required.
     */
    public static void main(String[] args)
    {
        SpringApplication.run(Main.class, args);
    }



    public Main()
    {
        Logger.getInstance().log( "Application started", Main.class.getName(), Log.LogLevel.INFO );
        ParseData.getInstance().parseCSV( locateFile() );
    }


    /**
     * locateFile attempts to locate the file resource through the Classloader and returns a
     * Reader object that can be parsed.
     *
     * @return The Reader object that allows us to read the contents of the located file.
     *          Otherwise return null if the file cannot be located or read.
     */
    private Reader locateFile()
    {
        try
        {
            final String fileName = "csv\\payment-forecast-data.csv";
            final ClassLoader classLoader = getClass().getClassLoader();
            final URL resource = classLoader.getResource( fileName );

            if( resource != null )
            {
                Logger.getInstance().log( "Located csv file: " + resource.getPath(), Main.class.getName(), Log.LogLevel.INFO );
                return new InputStreamReader( new BOMInputStream( resource.openStream() ), "UTF-8" );
            }
        }
        catch( IOException e )
        {
            Logger.getInstance().log( "Unable to locate csv file", Main.class.getName(), Log.LogLevel.FATAL );
            e.printStackTrace();
        }

        return null;
    }
}
