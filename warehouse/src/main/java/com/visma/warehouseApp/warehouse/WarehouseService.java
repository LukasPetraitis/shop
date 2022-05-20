package com.visma.warehouseApp.warehouse;

import com.item.ItemDTO;
import com.visma.warehouseApp.exception.NoSuchItemException;
import com.visma.warehouseApp.exception.NotEnoughInStorageException;
import com.visma.warehouseApp.item.Item;
import com.visma.warehouseApp.user.UserRepository;
import com.visma.warehouseApp.user.entity.User;
import com.visma.warehouseApp.userActivity.UserActivity;
import com.visma.warehouseApp.userActivity.UserActivityRepository;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Setter
@AllArgsConstructor
public class WarehouseService {

    private WarehouseRepository warehouseRepository;

    private UserRepository userRepository;

    private UserActivityRepository userActivityRepository;

    public List<Item> getItems() {
        return warehouseRepository.findAll();
    }

    public Item getItemById(int id) {
        return warehouseRepository.getById(id);
    }

    @Transactional
    public String sellItem(Integer id, Integer soldAmount) throws NoSuchItemException, NotEnoughInStorageException {

        Optional<Item> item = warehouseRepository.findById(id);

        if (item.isEmpty()) {
            throw new NoSuchItemException();
        }

        Integer inStorage = item.get().getAmountInStorage();

        if (inStorage < soldAmount) {
            throw new NotEnoughInStorageException();
        }

        inStorage = inStorage - soldAmount;

        warehouseRepository.setAmountInStorageById(inStorage, id);
        saveUserActivity(soldAmount, item.get());
        return "left in storage: " + inStorage;
    }

    @Transactional
    public Item saveItem(ItemDTO itemDTO) {
        Item item = new Item();

        BeanUtils.copyProperties(itemDTO, item);

        item.setPrice(new BigDecimal(itemDTO.getPrice()));

        return warehouseRepository.save(item);


    }

    public boolean isItemExistsById(int id) {
        return warehouseRepository.existsById(id);
    }

    public void saveUserActivity(Integer soldAmount, Item item){

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByUsername("username");
        UserActivity userActivity = new UserActivity();

        userActivity.setBought(soldAmount);
        userActivity.setItem(item);
        userActivity.setUser(user);
//
//        userActivityRepository.save(userActivity);
    }
}
