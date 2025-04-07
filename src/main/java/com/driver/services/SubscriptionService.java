package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Date startDate = new Date();
        int finalAmount;
        SubscriptionType subType = subscriptionEntryDto.getSubscriptionType();
        int noOfScreens = subscriptionEntryDto.getNoOfScreensRequired();

        if(subType==SubscriptionType.BASIC){
            finalAmount = 200*noOfScreens+500;
        } else if (subType==SubscriptionType.PRO) {
            finalAmount = 250*noOfScreens+800;
        } else if (subType==SubscriptionType.ELITE) {
            finalAmount = 350*noOfScreens+1000;
        }
        else{
            finalAmount=0;
        }
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).orElseThrow(()-> new IllegalArgumentException("User with the ID not found"));
        Subscription subscription = new Subscription(subType,noOfScreens,startDate,finalAmount);
        subscription.setUser(user);
        subscriptionRepository.save(subscription);

        return finalAmount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        Subscription subscription = subscriptionRepository.findSubscriptionByUserId(userId);
        SubscriptionType subType = subscription.getSubscriptionType();
        int noOfScreens = subscription.getNoOfScreensSubscribed();
        int finalAmount = subscription.getTotalAmountPaid();
        int updatedAmount;
        int balance;

        if(subType==SubscriptionType.BASIC){
            subscription.setSubscriptionType(SubscriptionType.PRO);
            updatedAmount = noOfScreens*250 + 800;
            subscription.setTotalAmountPaid(updatedAmount);
            balance = updatedAmount-finalAmount;
        } else if (subType==SubscriptionType.PRO) {
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            updatedAmount = noOfScreens*350 +1000;
            subscription.setTotalAmountPaid(updatedAmount);
            balance = updatedAmount-finalAmount;
        } else if (subType==SubscriptionType.ELITE) {
            throw new RuntimeException("Already the best Subscription");
        }
        else{
            balance=0;
        }
        subscriptionRepository.save(subscription);
        return balance;
    }


    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List <Subscription> subscriptions = subscriptionRepository.findAll();
        int totalAmount = 0;
        for (Subscription subscription : subscriptions){
            totalAmount +=subscription.getTotalAmountPaid();
        }

        return totalAmount;
    }

}
