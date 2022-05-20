package com.visma.shop.warehouse.userActivity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Integer> {

    List<UserActivity> findByUserId(Integer userId);

}
