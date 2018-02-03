package co.uk.tcummins.controllers;

import co.uk.tcummins.objs.Merchant;
import co.uk.tcummins.utils.ParseCSV;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * File:         TableController
 * Project:      PaymentForecastChallenge
 * Created:      02/02/2018
 * Author:       Tom
 */
@Controller
public class TableController
{
    @RequestMapping("/")
    public String tableController()
    {
        return "table";
    }


    private void calculateMerchantTotals()
    {
        final List<Merchant> merchantList = ParseCSV.getInstance().getMerchantList();
        final Map<String, Double> merchatTotals = new HashMap<>();

        for (Merchant merchant : merchantList)
        {

        }
    }
}
