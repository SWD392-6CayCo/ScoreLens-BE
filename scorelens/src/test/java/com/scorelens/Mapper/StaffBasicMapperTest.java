package com.scorelens.Mapper;

import com.scorelens.DTOs.Response.StaffBasicResponse;
import com.scorelens.Entity.Staff;
import com.scorelens.Enums.StatusType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StaffBasicMapperTest {

    @Autowired
    private StaffBasicMapper staffBasicMapper;

    @Test
    void testStaffToStaffBasicResponse() {
        // Given
        Staff staff = new Staff();
        staff.setStaffID("ST0000001");
        staff.setName("Test Staff");
        staff.setEmail("test@gmail.com");
        staff.setPhoneNumber("0987654321");
        staff.setDob(LocalDate.of(1990, 1, 1));
        staff.setAddress("Staff Address");
        staff.setStatus(StatusType.active);

        // When
        StaffBasicResponse result = staffBasicMapper.toStaffBasicResponse(staff);

        // Then
        assertNotNull(result);
        assertEquals("ST0000001", result.getStaffID());
        assertEquals("Test Staff", result.getName());
        assertEquals("test@gmail.com", result.getEmail());
        assertEquals("0987654321", result.getPhoneNumber());
        assertEquals(LocalDate.of(1990, 1, 1), result.getDob());
        assertEquals("Staff Address", result.getAddress());
        assertEquals(StatusType.active, result.getStatus());
    }

    @Test
    void testStaffToStaffBasicResponseWithNullValues() {
        // Given
        Staff staff = new Staff();
        staff.setStaffID("ST0000002");
        staff.setName("Test Staff 2");
        staff.setEmail("test2@gmail.com");
        // phoneNumber, dob, address are null
        staff.setStatus(StatusType.inactive);

        // When
        StaffBasicResponse result = staffBasicMapper.toStaffBasicResponse(staff);

        // Then
        assertNotNull(result);
        assertEquals("ST0000002", result.getStaffID());
        assertEquals("Test Staff 2", result.getName());
        assertEquals("test2@gmail.com", result.getEmail());
        assertNull(result.getPhoneNumber());
        assertNull(result.getDob());
        assertNull(result.getAddress());
        assertEquals(StatusType.inactive, result.getStatus());
    }
}
