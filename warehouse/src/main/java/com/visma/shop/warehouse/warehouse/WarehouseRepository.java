package com.visma.shop.warehouse.warehouse;

import com.visma.shop.warehouse.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface WarehouseRepository extends JpaRepository<Item, Integer> {
    @Modifying
    @Query("update Item i set i.amountInStorage = ?1 where i.id = ?2")
    void setAmountInStorageById(Integer inStorage, Integer id);
}
