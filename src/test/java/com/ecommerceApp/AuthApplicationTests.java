package com.ecommerceApp;

import com.ecommerceApp.model.persistence.Cart;
import com.ecommerceApp.model.persistence.Item;
import com.ecommerceApp.model.persistence.User;
import com.ecommerceApp.model.persistence.UserOrder;
import com.ecommerceApp.model.persistence.repositories.CartRepository;
import com.ecommerceApp.model.persistence.repositories.ItemRepository;
import com.ecommerceApp.model.persistence.repositories.OrderRepository;
import com.ecommerceApp.model.persistence.repositories.UserRepository;
import com.ecommerceApp.model.requests.CreateUserRequest;
import com.ecommerceApp.model.requests.ModifyCartRequest;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;

import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

//curl  --header "Content-type: application/json"  --request POST  --data "{"""username""":"""arka""","""password""":"""#pass1234s"""}"  http://localhost:8090/api/user/create
//
//	 http://localhost:8090/api/user/id/1
//	 
//	 curl --header "Content-type: application/json"  --request POST  --data "{"""username""":"""arka""","""password""":"""#pass1234s"""}" http://localhost:8090/login

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class AuthApplicationTests {

    @Autowired
    private MockMvc mockMvc;
//    @MockBean
//    private CartRepository cartRepository;
//    @MockBean
//    private ItemRepository itemRepository;
//    @MockBean
//    private OrderRepository orderRepository;
//    @MockBean
//    private BCryptPasswordEncoder bCryptPasswordEncoder;
//
//    @MockBean
//    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    private Gson gson;
    private static User user;
    private static String authorizationToken;
    private CreateUserRequest createUserRequest;

    @Before
    public void setupService() throws Exception {
        gson = new Gson();
        createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("foo");
        createUserRequest.setPassword("foo@1234");
        if(user == null) {
            mockMvc.perform(post("/api/user/create")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(gson.toJson(createUserRequest)))
            .andExpect(status().isOk());
            user = userRepository.findByUsername("foo");
            ResultActions res = mockMvc.perform(post("/login")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(gson.toJson(createUserRequest)))
                    .andExpect(status().isOk());

            authorizationToken = res.andReturn().getResponse().getHeader("Authorization");
        }

    }


	@Test
	public void contextLoads() {
	}

    @Test
    public void getUserWithoutAut() throws Exception {
        mockMvc.perform(get("/api/user/foo")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createUserWithLessPwdLength() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("foo2");
        req.setPassword("foo@12");
        mockMvc.perform(post("/api/user/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(req))
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

	@Test
    public void createAndGetUser() throws Exception {
        gson = new Gson();
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("foo2");
        req.setPassword("foo@2345");
        ResultActions res = mockMvc.perform(post("/api/user/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(req)))
                .andExpect(status().isOk());
        User newUser = gson.fromJson(res.andReturn().getResponse().getContentAsString() , User.class) ;
        res = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(req)))
                .andExpect(status().isOk());

        String token = res.andReturn().getResponse().getHeader("Authorization");
        mockMvc.perform(get("/api/user/foo2")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newUser.getId()));

        mockMvc.perform(get("/api/user/id/" + Long.toString(newUser.getId()))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("foo2"));
    }

    @Test
    public void submitAndGetOrder() throws Exception {

        ModifyCartRequest modReq = new ModifyCartRequest();
        modReq.setItemId(1L);
        modReq.setQuantity(1);
        modReq.setUsername(createUserRequest.getUsername());

        mockMvc.perform(post("/api/cart/addToCart")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", authorizationToken)
                .content(gson.toJson(modReq)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/order/submit/foo")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", authorizationToken)
                .content(createUserRequest.getUsername()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/order/history/foo")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", authorizationToken)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$[0].items", hasSize(1)));
    }

    @Test
    public void addAndRemoveFromCart() throws Exception {
        ModifyCartRequest modReq = new ModifyCartRequest();
        modReq.setItemId(1L);
        modReq.setQuantity(1);
        modReq.setUsername(createUserRequest.getUsername());

        mockMvc.perform(post("/api/cart/addToCart")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", authorizationToken)
                .content(gson.toJson(modReq)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/cart/removeFromCart")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", authorizationToken)
                .content(gson.toJson(modReq)))
                .andExpect(status().isOk());

    }


    @Test
    public void getItems() throws Exception {
        mockMvc.perform(get("/api/item/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(get("/api/item/1")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/item/name/Round Widget")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }
}
