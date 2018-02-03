package co.uk.tcummins.objs;

import java.time.LocalDateTime;

/**
 * File: Log
 * Project: PaymentForecastChallenge
 * Created: 02/02/2018
 * Author: Tom
 */
public class Log
{
    private LocalDateTime logTime;
    private String logLocation;
    private String logMessage;
    private LogLevel logLevel;


    public Log( LocalDateTime logTime, String logLocation, String logMessage, LogLevel logLevel )
    {
        this.logTime = logTime;
        this.logLocation = logLocation;
        this.logMessage = logMessage;
        this.logLevel = logLevel;
    }


    public LocalDateTime getLogTime() {
        return logTime;
    }

    public String getLogLocation() {
        return logLocation;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public enum LogLevel
    {
        ERROR, FATAL, INFO
    }
}
