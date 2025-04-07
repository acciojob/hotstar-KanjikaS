package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        User newUser = userRepository.save(user);
        if(userRepository.findById(newUser.getId()).isPresent()){
            return newUser.getId();
        }
        return null;
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        User user = userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException("User not found with the ID"));
        List <WebSeries> webSeriesList = webSeriesRepository.findAll();
        int noOfSeriesAllowedToView = 0;
        for(WebSeries webSeries : webSeriesList){
            if(user.getSubscription()==null || webSeries.getSubscriptionType()==null||user.getSubscription().getSubscriptionType()==null){
                continue;
                //pass
            }
            if((user.getAge() >= webSeries.getAgeLimit()) && (user.getSubscription().getSubscriptionType().ordinal() >= webSeries.getSubscriptionType().ordinal()))
            {
                noOfSeriesAllowedToView += 1;
            }
        }
        if(noOfSeriesAllowedToView>0){
            return noOfSeriesAllowedToView;
        }


        return null;
    }


}
