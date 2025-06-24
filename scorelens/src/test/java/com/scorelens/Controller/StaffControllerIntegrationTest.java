package com.scorelens.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scorelens.DTOs.Request.StaffUpdateRequestDto;
import com.scorelens.Entity.Staff;
import com.scorelens.Entity.Store;
import com.scorelens.Enums.StatusType;
import com.scorelens.Repository.StaffRepository;
import com.scorelens.Repository.StoreRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
@Transactional
class StaffControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StoreRepo storeRepo;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStaffByIdReturnsStore() throws Exception {
        // Given - Tạo store
        Store store = new Store();
        store.setStoreID("TEST_STORE");
        store.setName("Test Store");
        store.setAddress("Test Address");
        store.setStatus("active");
        store.setDescription("Test Description");
        storeRepo.save(store);

        // Tạo staff với store
        Staff staff = new Staff();
        staff.setStaffID("TEST_STAFF");
        staff.setName("Test Staff");
        staff.setEmail("teststaff@gmail.com");
        staff.setPhoneNumber("0123456789");
        staff.setDob(LocalDate.of(1990, 1, 1));
        staff.setAddress("Staff Address");
        staff.setCreateAt(LocalDate.now());
        staff.setStatus(StatusType.active);
        staff.setStore(store);
        staffRepository.save(staff);

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
        // Given - Tạo store
        Store store = new Store();
        store.setStoreID("UPDATE_STORE");
        store.setName("Update Store");
        store.setAddress("Update Address");
        store.setStatus("active");
        store.setDescription("Update Description");
        storeRepo.save(store);

        // Tạo staff
        Staff staff = new Staff();
        staff.setStaffID("UPDATE_STAFF");
        staff.setName("Original Staff");
        staff.setEmail("original@gmail.com");
        staff.setPhoneNumber("0987654321");
        staff.setDob(LocalDate.of(1995, 5, 15));
        staff.setAddress("Original Address");
        staff.setCreateAt(LocalDate.now());
        staff.setStatus(StatusType.active);
        staffRepository.save(staff);

        // Tạo update request với storeID
        StaffUpdateRequestDto updateRequest = new StaffUpdateRequestDto(
                "Updated Staff",
                "updated@gmail.com",
                "0111222333",
                LocalDate.of(1995, 5, 15),
                "Updated Address",
                StatusType.active, // managerID
                Arrays.asList("STAFF"),
                null
        );

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
                .andExpect(jsonPath("$.data.store.storeID").value("UPDATE_STORE"))
                .andExpect(jsonPath("$.data.store.name").value("Update Store"));
    }
}
