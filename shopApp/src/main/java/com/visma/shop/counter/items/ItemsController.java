package com.visma.shop.counter.items;

import com.item.ItemDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
public class ItemsController {

    private ItemsService itemsService;

    @RequestMapping(method = RequestMethod.GET, path = "/home")
    public String showItems(Model model) {
        model.addAttribute("items", getItems());
        return "home";
    }

    @RequestMapping(path = "/items", method = RequestMethod.GET)
    public @ResponseBody List<ItemDTO> getItems(){
        return itemsService.getItems();
    }

    @RequestMapping(path = "/items/{id}/sell/{amount}", method = RequestMethod.GET)
    public @ResponseBody String sellItem(@PathVariable int id, @PathVariable int amount){
        return itemsService.sellItem(id, amount);
    }

}
