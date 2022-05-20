package com.visma.shop.warehouse.warehouse;

import com.item.ItemDTO;
import com.visma.shop.warehouse.exception.NoSuchItemException;
import com.visma.shop.warehouse.exception.NotEnoughInStorageException;
import com.visma.shop.warehouse.item.Item;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("warehouse")
@AllArgsConstructor
public class WarehouseController {
    private WarehouseService warehouseService;

    @GetMapping(value = "/items")
    public List<Item> getItems(){
        return warehouseService.getItems();
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<Item> getItem(@PathVariable int id){

        if(warehouseService.isItemExistsById(id)){
            Item item = warehouseService.getItemById(id);
            return ResponseEntity.ok().body(item);
        }

        return ResponseEntity.badRequest().body(null);
    }


    @PostMapping("/items")
    public ResponseEntity<String> createItem(@RequestBody ItemDTO itemDTO) {

        if(itemDTO == null || itemDTO.getAmountInStorage() < 0){
            return ResponseEntity.badRequest().body("Error");
        }
        if(warehouseService.isItemExistsById( itemDTO.getId() )){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Item with such id exists");
        }
        warehouseService.saveItem(itemDTO);
        return ResponseEntity.ok("Item saved");
    }

    @GetMapping(value = "/items/{id}/sell/{soldAmount}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> sellItem(
            @PathVariable Integer id,
            @PathVariable Integer soldAmount){

        try {
            return ResponseEntity
                    .ok(warehouseService.sellItem(id, soldAmount));
        } catch (NoSuchItemException e) {
            return ResponseEntity
                    .notFound().build();
        } catch (NotEnoughInStorageException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }
    }



}
