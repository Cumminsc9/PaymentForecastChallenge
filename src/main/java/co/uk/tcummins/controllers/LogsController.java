package co.uk.tcummins.controllers;

import co.uk.tcummins.utils.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * File:         LogsController
 * Project:      PaymentForecastChallenge
 * Created:      02/02/2018
 * Author:       Tom
 */
@Controller
public class LogsController
{
    @RequestMapping("/logs")
    public String logController( final Model model )
    {
        model.addAttribute("logs",  Logger.getInstance().getLogs());
        return "logs";
    }


    /**
     * @return The standard log file.
     */
    @ResponseBody
    @RequestMapping(value = "/logs/download/logs", method = RequestMethod.GET)
    private ResponseEntity<byte[]> exportLogFile()
    {
        final String filename = "logOutput.txt";
        final byte[] logFile = Logger.getInstance().getLogFile(filename, Logger.getInstance().getLogs());
        return new ResponseEntity<>(logFile, getFileHeaders(filename), HttpStatus.OK);
    }


    /**
     * @return The parser error log file.
     */
    @ResponseBody
    @RequestMapping(value = "/logs/download/errors", method = RequestMethod.GET)
    private ResponseEntity<byte[]> exportParserErrors()
    {
        final String filename = "parserErrorOutput.txt";
        final byte[] logFile = Logger.getInstance().getLogFile(filename, Logger.getInstance().getParserErrors());
        return new ResponseEntity<>(logFile, getFileHeaders(filename), HttpStatus.OK);
    }


    /**
     * @param filename The filename the header will be created with.
     *
     * @return The HttpHeaders for the response to allow the user to download the
     *          log files.
     */
    private HttpHeaders getFileHeaders( final String filename )
    {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/plain"));
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return headers;
    }
}
