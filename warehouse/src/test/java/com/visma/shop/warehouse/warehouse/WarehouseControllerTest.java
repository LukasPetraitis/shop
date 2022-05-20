package com.visma.shop.warehouse.warehouse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.item.ItemDTO;
import com.visma.shop.warehouse.item.Item;
import com.visma.shop.warehouse.user.UserRepository;
import com.visma.shop.warehouse.userActivity.UserActivityRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class WarehouseControllerTest {
    @Autowired
    WebApplicationContext context;
    @MockBean
    UserActivityRepository userActivityRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    WarehouseRepository warehouseRepository;
    @Autowired
    MockMvc mockMvc;
    Item item;
    ItemDTO itemDto;
    ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    void init() throws JsonProcessingException {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        item = new Item(
                2,
                new BigDecimal("9.99"),
                "screwdriwer",
                "tool for work",
                10);

        itemDto = new ItemDTO(
                2,
                "9.99",
                "hammer",
                "good old hammer",
                10);

    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    public void getItemIntegrationTest() throws Exception {

        doReturn(item).when(warehouseRepository).getById(eq(2));
        doReturn(true).when(warehouseRepository).existsById(eq(2));

        mockMvc
            .perform( get("/warehouse/items/2") )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json("{'id': 2, " +
                                                "'price': 9.99, " +
                                                "'name': 'screwdriwer', " +
                                                "'description': 'tool for work', " +
                                                "'amountInStorage': 10}"));
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    public void getItemNoSuchIdIntegrationTest() throws Exception {

        doReturn(item).when(warehouseRepository).getById(any());
        doReturn(false).when(warehouseRepository).existsById(any());

        mockMvc
            .perform(get("/warehouse/items/1000"))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    public void getItemsIntegrationTest() throws Exception {

        Item item1 = new Item(1, new BigDecimal("4.99"), "box", "wooden box", 5);
        Item item2 = new Item(2, new BigDecimal("5.99"), "earphones", "cheap chinesse earphones", 10);
        Item item3 = new Item(3, new BigDecimal("6.99"), "cup", "black cup", 15);

        List<Item> items = List.of(item1, item2, item3);

        doReturn(items).when(warehouseRepository).findAll();

        mockMvc
                .perform(get("/warehouse/items"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 2, 3)));
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    public void sellItemSuccessIntegrationTest() throws Exception {

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        doReturn(Optional.of(item)).when(warehouseRepository).findById(eq(item.getId()));

        Integer soldAmount = 3;

        Integer amountLeftInStorage = item.getAmountInStorage() - soldAmount;

        mockMvc
                .perform(get("/warehouse/items/"+ item.getId()+"/sell/"+ soldAmount))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("left in storage: " + amountLeftInStorage));
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    public void sellItemNotEnoughInStorageIntegrationTest() throws Exception {
        doReturn(Optional.of(item)).when(warehouseRepository).findById(eq(item.getId()));

        Integer soldAmount = 11;

        mockMvc
                .perform(get("/warehouse/items/"+ item.getId()+"/sell/"+ soldAmount))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("Not enough items in storage"));
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    public void sellItemNotFoundByIdIntegrationTest() throws Exception {

        doReturn( Optional.empty() ).when(warehouseRepository).findById(any());

        mockMvc
                .perform(get("/warehouse/items/"+ 1000 +"/sell/" + 1000))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    public void createItemWithExistingIdIntegrationTest() throws Exception {

        String itemDtoString = objectMapper.writeValueAsString(itemDto);

        Mockito
                .when(warehouseRepository.existsById( eq(itemDto.getId()) ))
                .thenReturn(true);

        mockMvc
                .perform(post("/warehouse/items")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content( itemDtoString ))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().string("Item with such id exists"));
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    public void createItemIntegrationTest() throws Exception {

        String itemDtoString = objectMapper.writeValueAsString(itemDto);

        Mockito
            .when(warehouseRepository.save(ArgumentMatchers.any(Item.class)))
            .thenAnswer(i -> i.getArguments()[0]);

        Mockito
            .when(warehouseRepository.existsById( eq(itemDto.getId()) ))
            .thenReturn(false);


        mockMvc
                .perform(post("/warehouse/items")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content( itemDtoString ))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Item saved"));

    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    public void createItemWithNullValueIntegrationTest() throws Exception {

        mockMvc
                .perform(post("/warehouse/items"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    public void createItemWithNegativeAmountIntegrationTest() throws Exception {

            itemDto.setAmountInStorage( -1 );
            String itemDtoStringWithNegativeAmount = objectMapper.writeValueAsString(itemDto);
            itemDto.setAmountInStorage( 10 );

        mockMvc
                .perform(post("/warehouse/items")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content( itemDtoStringWithNegativeAmount ))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error"));
    }
}
