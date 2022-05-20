package com.visma.shop.warehouse.warehouse;

import com.item.ItemDTO;
import com.visma.shop.warehouse.item.Item;
import com.visma.shop.warehouse.exception.NoSuchItemException;
import com.visma.shop.warehouse.exception.NotEnoughInStorageException;
import com.visma.shop.warehouse.user.UserRepository;
import com.visma.shop.warehouse.userActivity.UserActivityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WarehouseServiceTest {

    WarehouseRepository warehouseRepository = Mockito.mock(WarehouseRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    UserActivityRepository userActivityRepository = Mockito.mock(UserActivityRepository.class);
    WarehouseService warehouseService =
            new WarehouseService(warehouseRepository, userRepository, userActivityRepository);

    Item item = new Item(
            2,
            new BigDecimal("9.99"),
            "hammer",
            "good old hammer",
            10);

    @Test
    void getItemTest() {

        Integer id = item.getId();

        Mockito.when(warehouseRepository.getById(id)).thenReturn(item);

        Assertions.assertEquals(item, warehouseService.getItemById(id));
    }

    @Test
    void getItemsTest(){

        Item item1 = new Item(2,new BigDecimal("4.99"), "box", "wooden box", 5);
        Item item2 = new Item(3,new BigDecimal("5.99"), "earphones", "cheap chinese earphones", 10);
        Item item3 = new Item(4,new BigDecimal("6.99"), "cup", "black cup", 15);

        List<Item> items = List.of(item1, item2, item3);

        doReturn(items).when(warehouseRepository).findAll();

        assertEquals(items, warehouseService.getItems());

    }

    @Test
    void sellItemSuccessTest() throws NoSuchItemException, NotEnoughInStorageException {

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        doReturn(Optional.of(item)).when(warehouseRepository).findById(eq(item.getId()));

        Integer soldAmount = 3;

        String result = warehouseService.sellItem(item.getId(), soldAmount);

        Mockito.verify(warehouseRepository).setAmountInStorageById(eq(7), eq(item.getId()));

        assertEquals("left in storage: 7", result);
    }

    @Test
    void sellItemNotEnoughInStorageTest() throws NoSuchItemException, NotEnoughInStorageException {

        Mockito.when(
                warehouseRepository.findById(any()))
                .thenReturn(Optional.of(item)
                );

        Integer soldAmount = 11;

        NotEnoughInStorageException thrown =
                Assertions.assertThrows( NotEnoughInStorageException.class,
                () -> { warehouseService.sellItem( eq( item.getId() ), soldAmount); }
                 );

        assertEquals("Not enough items in storage", thrown.getMessage());

    }

    @Test
    void sellItemNoSuchIdTest() {

        Mockito.when(warehouseRepository.findById(any())).thenReturn(Optional.empty());

        NoSuchItemException thrown =
                Assertions.assertThrows(
                        NoSuchItemException.class,
                        () -> { warehouseService.sellItem(anyInt(), 5); }
                );

        assertEquals("No item with such id", thrown.getMessage());
    }

    @Test
    void saveItemTest(){

        ItemDTO itemDto = new ItemDTO(
                2,
                "9.99",
                "hammer",
                "good old hammer",
                10);

        Mockito.when(warehouseRepository.save(any(Item.class))).thenAnswer(i -> i.getArguments()[0]);

        Item result = warehouseService.saveItem(itemDto);

        Mockito.verify(warehouseRepository, times(1))
                .save(any(Item.class));

        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getAmountInStorage(), result.getAmountInStorage());

    }

    @Test
    void isItemExistsByIdTrueTest(){
        Mockito
                .when(warehouseRepository.existsById( eq(item.getId()) ))
                .thenReturn(true);

        assertTrue(warehouseService.isItemExistsById(item.getId()));

    }

    @Test
    void isItemExistsByIdFalseTest(){
        Mockito
                .when(warehouseRepository.existsById(anyInt()))
                .thenReturn(false);

        assertFalse(warehouseService.isItemExistsById(anyInt()));

    }
}