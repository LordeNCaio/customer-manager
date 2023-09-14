package com.macedocaio.clientmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macedocaio.clientmanager.controllers.CustomerController;
import com.macedocaio.clientmanager.entities.Customer;
import com.macedocaio.clientmanager.utils.CustomerUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerRoutesIntegrationTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    private static UUID resourceId;
    private static Customer customer;

    @BeforeAll
    public static void beforeAll() {
        customer = CustomerUtils.createJohnDoe();
        resourceId = customer.getResourceId();
    }

    @Test
    @Order(1)
    public void shouldCallCreateSingleRoute() throws Exception {
        MockHttpServletRequestBuilder builder = getCreateSingleRoute();

        mvc.perform(builder)
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    @Order(2)
    public void shouldFindSingleResourceId() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get(String.format("%s/%s", CustomerController.BASE_URL, resourceId))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8);

        MvcResult result = mvc.perform(builder)
                .andExpect(status().is(HttpStatus.FOUND.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        Customer customer = mapper.readValue(result.getResponse().getContentAsString(), Customer.class);
        assertNotNull(customer);
    }

    @Test
    @Order(3)
    public void shouldUpdateSingleResourceId() throws Exception {
        Customer customer = CustomerUtils.createJohnDoe();
        customer.setFirstname("Johnny");
        customer.setLastname("Knoxville");

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(String.format("%s/%s", CustomerController.BASE_URL, resourceId))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(customer))
                .characterEncoding(StandardCharsets.UTF_8);

        mvc.perform(builder)
                .andExpect(status().is(HttpStatus.ACCEPTED.value()))
                .andReturn();
    }

    @Test
    @Order(4)
    public void shouldDeleteSingleResourceId() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete(String.format("%s/%s", CustomerController.BASE_URL, resourceId))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(StandardCharsets.UTF_8);

        mvc.perform(builder)
                .andExpect(status().is(HttpStatus.ACCEPTED.value()))
                .andReturn();
    }

    private MockHttpServletRequestBuilder getCreateSingleRoute() throws Exception {
        return MockMvcRequestBuilders
                .post(CustomerController.BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(mapper.writeValueAsBytes(customer));
    }
}