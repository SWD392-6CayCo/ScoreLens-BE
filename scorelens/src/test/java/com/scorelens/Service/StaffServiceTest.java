//package com.scorelens.Service;
//
//import com.scorelens.DTOs.Request.StaffUpdateRequestDto;
//import com.scorelens.DTOs.Response.StaffResponseDto;
//import com.scorelens.Entity.Staff;
//import com.scorelens.Enums.StatusType;
//import com.scorelens.Exception.AppException;
//import com.scorelens.Exception.ErrorCode;
//import com.scorelens.Mapper.StaffMapper;
//import com.scorelens.Repository.RoleRepository;
//import com.scorelens.Repository.StaffRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.anyList;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class StaffServiceTest {
//
//    @Mock
//    private StaffRepository staffRepository;
//
//    @Mock
//    private StaffMapper staffMapper;
//
//    @Mock
//    private UserValidatorService userValidatorService;
//
//    @Mock
//    private RoleRepository roleRepository;
//
//    @InjectMocks
//    private StaffService staffService;
//
//    private Staff existingStaff;
//    private StaffUpdateRequestDto updateRequestDto;
//
//    @BeforeEach
//    void setUp() {
//        existingStaff = new Staff();
//        existingStaff.setStaffID("ST0000001");
//        existingStaff.setName("Old Name");
//        existingStaff.setEmail("old@gmail.com");
//        existingStaff.setPhoneNumber("0123456789");
//
//        updateRequestDto = new StaffUpdateRequestDto(
//                "Blackpro",
//                "blackpro2k4@gmail.com",
//                "0344017063",
//                LocalDate.of(2004, 10, 26),
//                "Go Cong City",
//                StatusType.active,
//                Arrays.asList("ADMIN"),
//                "" // managerID rỗng
//        );
//    }
//
//    @Test
//    void testUpdateStaffWithEmptyManagerID() {
//        // Given
//        String staffId = "ST0000001";
//
//        when(staffRepository.findById(staffId)).thenReturn(Optional.of(existingStaff));
//        when(staffRepository.save(any(Staff.class))).thenReturn(existingStaff);
//        when(staffMapper.toDto(any(Staff.class))).thenReturn(new StaffResponseDto());
//        when(roleRepository.findAllById(anyList())).thenReturn(Arrays.asList());
//
//        // When & Then - Không nên throw exception
//        assertDoesNotThrow(() -> {
//            StaffResponseDto result = staffService.updateStaff(staffId, updateRequestDto);
//            assertNotNull(result);
//        });
//
//        // Verify rằng staffRepository.findById không được gọi với empty string
//        verify(staffRepository, never()).findById("");
//        verify(staffRepository, times(1)).findById(staffId);
//        verify(staffRepository, times(1)).save(existingStaff);
//    }
//
//    @Test
//    void testUpdateStaffWithNullManagerID() {
//        // Given
//        String staffId = "ST0000001";
//        updateRequestDto = new StaffUpdateRequestDto(
//                "Blackpro",
//                "blackpro2k4@gmail.com",
//                "0344017063",
//                LocalDate.of(2004, 10, 26),
//                "Go Cong City",
//                StatusType.active,
//                Arrays.asList("ADMIN"),
//                null, // managerID null
//                null  // storeID null
//        );
//
//        when(staffRepository.findById(staffId)).thenReturn(Optional.of(existingStaff));
//        when(staffRepository.save(any(Staff.class))).thenReturn(existingStaff);
//        when(staffMapper.toDto(any(Staff.class))).thenReturn(new StaffResponseDto());
//        when(roleRepository.findAllById(anyList())).thenReturn(Arrays.asList());
//
//        // When & Then - Không nên throw exception
//        assertDoesNotThrow(() -> {
//            StaffResponseDto result = staffService.updateStaff(staffId, updateRequestDto);
//            assertNotNull(result);
//        });
//
//        // Verify manager được set thành null
//        verify(staffRepository, times(1)).save(existingStaff);
//        assertNull(existingStaff.getManager());
//    }
//
//    @Test
//    void testUpdateStaffWithValidManagerID() {
//        // Given
//        String staffId = "ST0000001";
//        String managerId = "ST0000002";
//        Staff manager = new Staff();
//        manager.setStaffID(managerId);
//
//        updateRequestDto = new StaffUpdateRequestDto(
//                "Blackpro",
//                "blackpro2k4@gmail.com",
//                "0344017063",
//                LocalDate.of(2004, 10, 26),
//                "Go Cong City",
//                StatusType.active,
//                Arrays.asList("ADMIN"),
//                managerId,
//                null // storeID null
//        );
//
//        when(staffRepository.findById(staffId)).thenReturn(Optional.of(existingStaff));
//        when(staffRepository.findById(managerId)).thenReturn(Optional.of(manager));
//        when(staffRepository.save(any(Staff.class))).thenReturn(existingStaff);
//        when(staffMapper.toDto(any(Staff.class))).thenReturn(new StaffResponseDto());
//        when(roleRepository.findAllById(anyList())).thenReturn(Arrays.asList());
//
//        // When
//        StaffResponseDto result = staffService.updateStaff(staffId, updateRequestDto);
//
//        // Then
//        assertNotNull(result);
//        verify(staffRepository, times(1)).findById(managerId);
//        verify(staffRepository, times(1)).save(existingStaff);
//        assertEquals(manager, existingStaff.getManager());
//    }
//
//    @Test
//    void testUpdateStaffWithInvalidManagerID() {
//        // Given
//        String staffId = "ST0000001";
//        String invalidManagerId = "INVALID_ID";
//
//        updateRequestDto = new StaffUpdateRequestDto(
//                "Blackpro",
//                "blackpro2k4@gmail.com",
//                "0344017063",
//                LocalDate.of(2004, 10, 26),
//                "Go Cong City",
//                StatusType.active,
//                Arrays.asList("ADMIN"),
//                invalidManagerId,
//                null // storeID null
//        );
//
//        when(staffRepository.findById(staffId)).thenReturn(Optional.of(existingStaff));
//        when(staffRepository.findById(invalidManagerId)).thenReturn(Optional.empty());
//
//        // When & Then
//        AppException exception = assertThrows(AppException.class, () -> {
//            staffService.updateStaff(staffId, updateRequestDto);
//        });
//
//        assertEquals(ErrorCode.USER_NOT_EXIST, exception.getErrorCode());
//    }
//}
