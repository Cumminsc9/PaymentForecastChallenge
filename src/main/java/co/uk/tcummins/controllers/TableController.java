package co.uk.tcummins.controllers;

import co.uk.tcummins.utils.ParseData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

/**
 * File: TableController
 * Project: PaymentForecastChallenge
 * Created: 02/02/2018
 * Author: Tom
 */
@Controller
public class TableController
{
    @RequestMapping("/")
    public String tableController( final Model model )
    {
        List<List<ParseData.TableData>> tableDataList = new ArrayList<>();

        DayOfWeek prevDay = null;
        List<ParseData.TableData> foo = new ArrayList<>();
        for (ParseData.TableData tableData :  ParseData.getInstance().getTableDataList())
        {
            if(prevDay == null)
                prevDay = tableData.getDay();

            if( !tableData.getDay().equals(prevDay) )
            {
                tableDataList.add( foo );
                foo = new ArrayList<>();
            }

            foo.add(tableData);

            prevDay = tableData.getDay();
        }
        tableDataList.add(foo);

        model.addAttribute("tableDataList", tableDataList );
        return "table";
    }


//    @RequestMapping(value = "/upload", method = RequestMethod.POST)
//    public String uploadPaymentFile( @RequestParam(value = "file") MultipartFile file)
//    {
//        if( file.isEmpty() )
//        {
//            return "table";
//        }
//
//        try
//        {
//            // read and write the file to the selected location-
//            byte[] bytes = file.getBytes();
//            Path path = Paths.get( "" + file.getOriginalFilename() );
//            Files.write( path, bytes );
//        }
//        catch( IOException e )
//        {
//            e.printStackTrace();
//        }
//
//        return "table";
//    }
}
