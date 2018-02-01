package co.uk.tcummins;

import co.uk.tcummins.utils.ParseCSV;

/**
 * File:         Main
 * Project:      PaymentForecastChallenge
 * Created:      01/02/2018
 * Author:       Tom
 */
public class Main
{
    public static void main(String[] args)
    {
        new Main();
    }

    private Main()
    {
        new ParseCSV().readCSV();
    }
}
