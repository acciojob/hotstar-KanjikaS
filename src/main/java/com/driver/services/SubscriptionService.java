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
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto) {

        if(subscriptionEntryDto.getSubscriptionType() == null){
            return null;
        }
        if(subscriptionEntryDto.getNoOfScreensRequired() <= 0){
            return null;
        }
        if(subscriptionRepository.findSubscriptionByUserId(subscriptionEntryDto.getUserId()).isPresent()) return null;

        User user = userRepository.findById(subscriptionEntryDto.getUserId()).orElse(null);
        if (user == null){
            return null;
        }
        Date startDate = new Date();
        int finalAmount;
        SubscriptionType subType = subscriptionEntryDto.getSubscriptionType();
        int noOfScreens = subscriptionEntryDto.getNoOfScreensRequired();

        if(subType==SubscriptionType.BASIC){
            finalAmount = (200 * noOfScreens)+500;
        } else if (subType==SubscriptionType.PRO) {
            finalAmount = (250 * noOfScreens)+800;
        } else if (subType==SubscriptionType.ELITE) {
            finalAmount = (350 * noOfScreens)+1000;
        }
        else{
            finalAmount=0;
        }
        Subscription subscription = new Subscription(subType,noOfScreens,startDate,finalAmount);
        subscription.setUser(user);
        user.setSubscription(subscription);
        subscriptionRepository.save(subscription);
        userRepository.save(user);
        if(subscription.getId() > 0) return subscription.getTotalAmountPaid();

        return null;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        Subscription subscription = subscriptionRepository.findSubscriptionByUserId(userId).orElse(null);
        if(subscription==null){
            return null;
        }
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
            throw new Exception("Already the best Subscription");
        }
        else{
            return null;
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
