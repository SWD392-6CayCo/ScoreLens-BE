package com.scorelens.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scorelens.Controller.v1.StaffV1Controller;
import com.scorelens.DTOs.Request.StaffUpdateRequestDto;
import com.scorelens.DTOs.Response.StaffResponseDto;
import com.scorelens.DTOs.Response.StoreBasicResponse;
import com.scorelens.DTOs.Response.StaffBasicResponse;
import com.scorelens.Enums.StatusType;
import com.scorelens.Service.StaffService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(StaffV1Controller.class)
class StaffControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StaffService staffService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStaffByIdReturnsStore() throws Exception {
        // Given
        StoreBasicResponse storeResponse = new StoreBasicResponse(
                "TEST_STORE",
                "Test Store",
                "Test Address",
                "active",
                "Test Description"
        );

        StaffResponseDto staffResponse = new StaffResponseDto(
                "TEST_STAFF",
                "Test Staff",
                "teststaff@gmail.com",
                "0123456789",
                LocalDate.of(1990, 1, 1),
                "Staff Address",
                new HashSet<>(),
                LocalDate.now(),
                LocalDate.now(),
                StatusType.active,
                null, // manager
                storeResponse // store
        );

        when(staffService.getStaffById("TEST_STAFF")).thenReturn(staffResponse);

        // When & Then
        mockMvc.perform(get("/v1/staffs/{id}", "TEST_STAFF"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(1000))
                .andExpect(jsonPath("$.data.staffID").value("TEST_STAFF"))
                .andExpect(jsonPath("$.data.name").value("Test Staff"))
                .andExpect(jsonPath("$.data.email").value("teststaff@gmail.com"))
                .andExpect(jsonPath("$.data.store").exists())
                .andExpect(jsonPath("$.data.store.storeID").value("TEST_STORE"))
                .andExpect(jsonPath("$.data.store.name").value("Test Store"))
                .andExpect(jsonPath("$.data.store.address").value("Test Address"))
                .andExpect(jsonPath("$.data.store.status").value("active"))
                .andExpect(jsonPath("$.data.store.description").value("Test Description"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateStaffWithStore() throws Exception {
        // Given
        StoreBasicResponse storeResponse = new StoreBasicResponse(
                "2358214f-af77-4a21-868f-b623d65bf61c",
                "KAT Billiard",
                "Quận 12, TPHCM",
                "activate",
                "KAT Billiard - place to play by your style"
        );

        StaffResponseDto staffResponse = new StaffResponseDto(
                "UPDATE_STAFF",
                "Updated Staff",
                "updated@gmail.com",
                "0111222333",
                LocalDate.of(1995, 5, 15),
                "Updated Address",
                new HashSet<>(),
                LocalDate.now(),
                LocalDate.now(),
                StatusType.active,
                null, // manager
                storeResponse // store
        );

        StaffUpdateRequestDto updateRequest = new StaffUpdateRequestDto(
                "Updated Staff",
                "updated@gmail.com",
                "0111222333",
                LocalDate.of(1995, 5, 15),
                "Updated Address",
                StatusType.active,
                Arrays.asList("STAFF"),
                null, // managerID
                "2358214f-af77-4a21-868f-b623d65bf61c" // storeID
        );

        when(staffService.updateStaff(eq("UPDATE_STAFF"), any(StaffUpdateRequestDto.class)))
                .thenReturn(staffResponse);

        // When & Then
        mockMvc.perform(put("/v1/staffs/{id}", "UPDATE_STAFF")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(1000))
                .andExpect(jsonPath("$.data.staffID").value("UPDATE_STAFF"))
                .andExpect(jsonPath("$.data.name").value("Updated Staff"))
                .andExpect(jsonPath("$.data.email").value("updated@gmail.com"))
                .andExpect(jsonPath("$.data.store").exists())
                .andExpect(jsonPath("$.data.store.storeID").value("2358214f-af77-4a21-868f-b623d65bf61c"))
                .andExpect(jsonPath("$.data.store.name").value("KAT Billiard"))
                .andExpect(jsonPath("$.data.store.address").value("Quận 12, TPHCM"))
                .andExpect(jsonPath("$.data.store.status").value("activate"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStaffByIdWithNullStore() throws Exception {
        // Given - Staff without store
        StaffResponseDto staffResponse = new StaffResponseDto(
                "NO_STORE_STAFF",
                "Staff Without Store",
                "nostore@gmail.com",
                "0999888777",
                LocalDate.of(1985, 3, 10),
                "No Store Address",
                new HashSet<>(),
                LocalDate.now(),
                LocalDate.now(),
                StatusType.active,
                null, // manager
                null  // store is null
        );

        when(staffService.getStaffById("NO_STORE_STAFF")).thenReturn(staffResponse);

        // When & Then
        mockMvc.perform(get("/v1/staffs/{id}", "NO_STORE_STAFF"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(1000))
                .andExpect(jsonPath("$.data.staffID").value("NO_STORE_STAFF"))
                .andExpect(jsonPath("$.data.name").value("Staff Without Store"))
                .andExpect(jsonPath("$.data.email").value("nostore@gmail.com"))
                .andExpect(jsonPath("$.data.store").doesNotExist());
    }
}
