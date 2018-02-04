package co.uk.tcummins.controllers;

import co.uk.tcummins.objs.Log;
import co.uk.tcummins.utils.Logger;
import co.uk.tcummins.utils.ParseData;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * File: TableController
 * Project: PaymentForecastChallenge
 * Created: 02/02/2018
 * Author: Tom
 */
@Controller
public class TableController
{
    @RequestMapping( "/" )
    public String tableController( final Model model )
    {
        model.addAttribute("tableDataList", ParseData.getInstance().getSortedTableDataList() );
        return "table";
    }


    @RequestMapping( value = "/", method = RequestMethod.POST )
    public RedirectView uploadPaymentFile( @RequestParam(value = "file") MultipartFile uploadedFile )
    {
        if( uploadedFile.isEmpty() )
        {
            return new RedirectView("/");
        }

        try
        {
            final byte[] uploadBytes = uploadedFile.getBytes();
            final Path writePath = Paths.get( "" + uploadedFile.getOriginalFilename() );
            final Path filePath = Files.write(writePath, uploadBytes);
            ParseData.getInstance().parseCSV( new InputStreamReader(
                    new BOMInputStream( Files.newInputStream( filePath ) ), "UTF-8" ) );
        }
        catch( IOException e )
        {
            Logger.getInstance().log( "Unsuccessful upload of file: " + e.getMessage(),
                    TableController.class.getName(), Log.LogLevel.ERROR );
            e.printStackTrace();
        }

        return new RedirectView( "/" );
    }
}
