package com.driver.services;

import com.driver.EntryDto.ProductionHouseEntryDto;
import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo
        WebSeries existingWebSeries = webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName());
        if(existingWebSeries !=null){
            throw new Exception("Series is already present");
        }
        WebSeries webSeries = new WebSeries(webSeriesEntryDto.getSeriesName(),webSeriesEntryDto.getAgeLimit(),webSeriesEntryDto.getRating(),webSeriesEntryDto.getSubscriptionType());
        ProductionHouse productionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).orElseThrow(()-> new IllegalArgumentException("Production with the ID not found"));;
        webSeries.setProductionHouse(productionHouse);
        double currentRating = productionHouse.getRatings();
        double finalRating=(currentRating * productionHouse.getWebSeriesList().size() + webSeries.getRating())/(productionHouse.getWebSeriesList().size()+1);
        productionHouse.getWebSeriesList().add(webSeries);
        productionHouse.setRatings(finalRating);
        productionHouseRepository.save(productionHouse);
        webSeriesRepository.save(webSeries);

        return null;
    }

}
