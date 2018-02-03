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
    public static void main(String[] args)
    {
        SpringApplication.run(Main.class, args);
    }

    public Main()
    {
        ParseData.getInstance().parseCSV( locateFile() );
        Logger.getInstance().log( "Application started", Main.class.getName(), Log.LogLevel.INFO );

    }


    private Reader locateFile()
    {
        try
        {
            final String fileName = "csv\\payment-forecast-data.csv";
            //final String fileName = "csv\\test.csv";
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
