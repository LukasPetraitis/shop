package com.visma.shop.warehouse.userActivity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserActivityService {

    @Autowired
    private UserActivityRepository userActivityRepository;

    public UserActivity saveShopInfo(UserActivity userActivity){
        return userActivityRepository.save(userActivity);
    }
}
