package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import static org.hamcrest.Matchers.hasSize;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class AuthApplicationTests {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CartRepository cartRepository;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private UserRepository userRepository;

    private Gson gson;

    @Before
    public void setupService() {
        gson = new Gson();
        User user = new User();
        user.setUsername("foo");
        user.setId(1L);
        Cart cart = new Cart();
        cart.setId(1L);
        user.setCart(cart);
        Item item = new Item();
        item.setName("test");
        item.setId(1L);
        List<Item> items = new ArrayList<>();
        items.add(item);
        cart.setItems(items);
        UserOrder order = UserOrder.createFromCart(user.getCart());
        List<UserOrder> allOrders = new ArrayList<>();
        allOrders.add(order);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartRepository.findByUser(any(User.class))).thenReturn(cart);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemRepository.findByName(any(String.class))).thenReturn(order.getItems());
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.of(item));
        when(orderRepository.save(any(UserOrder.class))).thenReturn(order);
        when(orderRepository.findByUser(any(User.class))).thenReturn(allOrders);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findByUsername(any(String.class))).thenReturn(user);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
    }


	@Test
	public void contextLoads() {
	}

	@Test
    public void createAndGetUser() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("foo");
        mockMvc.perform(post("/api/user/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(req)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/user/foo")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        mockMvc.perform(get("/api/user/id/1")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("foo"));
    }

    @Test
    public void submitAndGetOrder() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("foo");
        mockMvc.perform(post("/api/user/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(req)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/order/submit/foo")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(req.getUsername()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/order/history/foo")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$[0].items", hasSize(1)));
    }

    @Test
    public void addAndRemoveFromCard() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("foo");
        ModifyCartRequest modReq = new ModifyCartRequest();
        modReq.setItemId(1L);
        modReq.setQuantity(0);
        modReq.setUsername(req.getUsername());
        mockMvc.perform(post("/api/user/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(req)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/cart/addToCart")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(modReq)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/cart/removeFromCart")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(modReq)))
                .andExpect(status().isOk());

    }

}
