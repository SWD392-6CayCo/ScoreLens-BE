package com.scorelens.Mapper;

import com.scorelens.DTOs.Response.StaffResponseDto;
import com.scorelens.DTOs.Response.StoreBasicResponse;
import com.scorelens.DTOs.Response.StaffBasicResponse;
import com.scorelens.Entity.Staff;
import com.scorelens.Entity.Store;
import com.scorelens.Enums.StatusType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StaffMapperTest {

    @Autowired
    private StaffMapper staffMapper;

    @Test
    void testToDtoWithStore() {
        // Given
        Store store = new Store();
        store.setStoreID("STORE001");
        store.setName("Test Store");
        store.setAddress("Test Address");
        store.setStatus("active");
        store.setDescription("Test Description");

        Staff staff = new Staff();
        staff.setStaffID("ST0000001");
        staff.setName("Test Staff");
        staff.setEmail("test@gmail.com");
        staff.setPhoneNumber("0123456789");
        staff.setDob(LocalDate.of(1990, 1, 1));
        staff.setAddress("Staff Address");
        staff.setCreateAt(LocalDate.now());
        staff.setStatus(StatusType.active);
        staff.setStore(store);

        // When
        StaffResponseDto result = staffMapper.toDto(staff);

        // Then
        assertNotNull(result);
        assertEquals("ST0000001", result.getStaffID());
        assertEquals("Test Staff", result.getName());
        assertEquals("test@gmail.com", result.getEmail());
        
        // Kiểm tra store được map đúng
        assertNotNull(result.getStore());
        assertEquals("STORE001", result.getStore().getStoreID());
        assertEquals("Test Store", result.getStore().getName());
        assertEquals("Test Address", result.getStore().getAddress());
        assertEquals("active", result.getStore().getStatus());
        assertEquals("Test Description", result.getStore().getDescription());
    }

    @Test
    void testStaffToDtoWithManager() {
        // Given
        Store store = new Store();
        store.setStoreID("STORE001");
        store.setName("Test Store");
        store.setAddress("Test Address");
        store.setStatus("active");
        store.setDescription("Test Description");

        Staff manager = new Staff();
        manager.setStaffID("MG0000001");
        manager.setName("Manager Name");
        manager.setEmail("manager@gmail.com");
        manager.setPhoneNumber("0123456789");
        manager.setDob(LocalDate.of(1980, 1, 1));
        manager.setAddress("Manager Address");
        manager.setStatus(StatusType.active);

        Staff staff = new Staff();
        staff.setStaffID("ST0000001");
        staff.setName("Test Staff");
        staff.setEmail("test@gmail.com");
        staff.setPhoneNumber("0987654321");
        staff.setDob(LocalDate.of(1990, 1, 1));
        staff.setAddress("Staff Address");
        staff.setStatus(StatusType.active);
        staff.setStore(store);
        staff.setManager(manager);

        // When
        StaffResponseDto result = staffMapper.toDto(staff);

        // Then
        assertNotNull(result);
        assertEquals("ST0000001", result.getStaffID());
        assertEquals("Test Staff", result.getName());
        assertEquals("test@gmail.com", result.getEmail());

        // Kiểm tra manager được map đúng
        assertNotNull(result.getManager());
        assertEquals("MG0000001", result.getManager().getStaffID());
        assertEquals("Manager Name", result.getManager().getName());
        assertEquals("manager@gmail.com", result.getManager().getEmail());
        assertEquals("0123456789", result.getManager().getPhoneNumber());
        assertEquals(StatusType.active, result.getManager().getStatus());

        // Kiểm tra store được map đúng
        assertNotNull(result.getStore());
        assertEquals("STORE001", result.getStore().getStoreID());
        assertEquals("Test Store", result.getStore().getName());
    }

    @Test
    void testToDtoWithNullStore() {
        // Given
        Staff staff = new Staff();
        staff.setStaffID("ST0000002");
        staff.setName("Test Staff 2");
        staff.setEmail("test2@gmail.com");
        staff.setPhoneNumber("0987654321");
        staff.setDob(LocalDate.of(1995, 5, 15));
        staff.setAddress("Staff Address 2");
        staff.setCreateAt(LocalDate.now());
        staff.setStatus(StatusType.active);
        staff.setStore(null); // Store là null

        // When
        StaffResponseDto result = staffMapper.toDto(staff);

        // Then
        assertNotNull(result);
        assertEquals("ST0000002", result.getStaffID());
        assertEquals("Test Staff 2", result.getName());
        assertEquals("test2@gmail.com", result.getEmail());
        
        // Kiểm tra store là null
        assertNull(result.getStore());
    }
}
