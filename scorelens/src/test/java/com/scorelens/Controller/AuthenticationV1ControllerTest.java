package com.scorelens.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scorelens.Controller.v1.AuthenticationV1Controller;
import com.scorelens.DTOs.Request.CustomerCreateRequestDto;
import com.scorelens.DTOs.Response.CustomerResponseDto;
import com.scorelens.Service.AuthenticationService;
import com.scorelens.Service.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(AuthenticationV1Controller.class)
public class AuthenticationV1ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterWithNullPhoneNumber() throws Exception {
        // Tạo request với phoneNumber là null
        CustomerCreateRequestDto request = CustomerCreateRequestDto.builder()
                .name("blackpro hehe")
                .email("testRegis2@gmail.com")
                .phoneNumber(null)
                .password("123456")
                .dob(null)
                .build();

        // Mock response
        CustomerResponseDto response = new CustomerResponseDto(
                "test-id",
                "blackpro hehe",
                "testRegis2@gmail.com",
                null,
                null,
                LocalDate.now(),
                null,
                "normal"
        );

        Mockito.when(customerService.createCustomer(any())).thenReturn(response);

        // Thực hiện test
        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(1000))
                .andExpect(jsonPath("$.message").value("Register successfully"))
                .andExpect(jsonPath("$.data.name").value("blackpro hehe"))
                .andExpect(jsonPath("$.data.email").value("testRegis2@gmail.com"))
                .andExpect(jsonPath("$.data.phoneNumber").isEmpty());
    }

    @Test
    void testRegisterWithValidPhoneNumber() throws Exception {
        // Tạo request với phoneNumber hợp lệ
        CustomerCreateRequestDto request = CustomerCreateRequestDto.builder()
                .name("test user")
                .email("testuser@gmail.com")
                .phoneNumber("0987654321")
                .password("123456")
                .dob(LocalDate.of(2000, 1, 1))
                .build();

        // Mock response
        CustomerResponseDto response = new CustomerResponseDto(
                "test-id-2",
                "test user",
                "testuser@gmail.com",
                "0987654321",
                LocalDate.of(2000, 1, 1),
                LocalDate.now(),
                null,
                "normal"
        );

        Mockito.when(customerService.createCustomer(any())).thenReturn(response);

        // Thực hiện test
        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(1000))
                .andExpect(jsonPath("$.message").value("Register successfully"))
                .andExpect(jsonPath("$.data.name").value("test user"))
                .andExpect(jsonPath("$.data.email").value("testuser@gmail.com"))
                .andExpect(jsonPath("$.data.phoneNumber").value("0987654321"));
    }
}
