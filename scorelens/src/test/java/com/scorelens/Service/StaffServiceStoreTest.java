package com.scorelens.Service;

import com.scorelens.DTOs.Request.StaffCreateRequestDto;
import com.scorelens.DTOs.Request.StaffUpdateRequestDto;
import com.scorelens.DTOs.Response.StaffResponseDto;
import com.scorelens.Entity.Staff;
import com.scorelens.Entity.Store;
import com.scorelens.Enums.StatusType;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.StaffMapper;
import com.scorelens.Repository.IDSequenceRepository;
import com.scorelens.Repository.RoleRepository;
import com.scorelens.Repository.StaffRepository;
import com.scorelens.Repository.StoreRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffServiceStoreTest {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private StoreRepo storeRepo;

    @Mock
    private StaffMapper staffMapper;

    @Mock
    private UserValidatorService userValidatorService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private IDSequenceRepository idSequenceRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private StaffService staffService;

    private Store testStore;
    private StaffCreateRequestDto createRequestDto;
    private StaffUpdateRequestDto updateRequestDto;

    @BeforeEach
    void setUp() {
        testStore = new Store();
        testStore.setStoreID("STORE001");
        testStore.setName("Test Store");
        testStore.setAddress("Test Address");
        testStore.setStatus("active");
        testStore.setDescription("Test Description");

        Set<String> roles = new HashSet<>();
        roles.add("STAFF");

        createRequestDto = StaffCreateRequestDto.builder()
                .name("Test Staff")
                .email("teststaff@gmail.com")
                .phoneNumber("0123456789")
                .dob(LocalDate.of(1990, 1, 1))
                .address("Test Address")
                .password("123456")
                .roles(roles)
                .managerID(null)
                .storeID("STORE001")
                .build();

        updateRequestDto = new StaffUpdateRequestDto(
                "Updated Staff",
                "updatedstaff@gmail.com",
                "0987654321",
                LocalDate.of(1995, 5, 15),
                "Updated Address",
                StatusType.active,
                Arrays.asList("ADMIN"),
                null, // managerID
                "STORE001" // storeID
        );
    }

    @Test
    void testCreateStaffWithValidStore() {
        // Given
        when(roleRepository.existsById("STAFF")).thenReturn(true);
        when(storeRepo.findById("STORE001")).thenReturn(Optional.of(testStore));
        when(staffMapper.toEntity(any())).thenReturn(new Staff());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findAllById(any())).thenReturn(Arrays.asList());
        when(staffRepository.save(any())).thenReturn(new Staff());
        when(staffMapper.toDto(any(Staff.class))).thenReturn(new StaffResponseDto());

        // When & Then - Không nên throw exception
        assertDoesNotThrow(() -> {
            StaffResponseDto result = staffService.createStaff(createRequestDto);
            assertNotNull(result);
        });

        // Verify store được tìm kiếm
        verify(storeRepo, times(1)).findById("STORE001");
    }

    @Test
    void testCreateStaffWithInvalidStore() {
        // Given
        when(roleRepository.existsById("STAFF")).thenReturn(true);
        when(storeRepo.findById("INVALID_STORE")).thenReturn(Optional.empty());

        StaffCreateRequestDto invalidRequest = StaffCreateRequestDto.builder()
                .name("Test Staff")
                .email("teststaff@gmail.com")
                .phoneNumber("0123456789")
                .dob(LocalDate.of(1990, 1, 1))
                .address("Test Address")
                .password("123456")
                .roles(Set.of("STAFF"))
                .managerID(null)
                .storeID("INVALID_STORE")
                .build();

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> {
            staffService.createStaff(invalidRequest);
        });

        assertEquals(ErrorCode.STORE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testCreateStaffWithNullStoreID() {
        // Given
        when(roleRepository.existsById("STAFF")).thenReturn(true);
        when(staffMapper.toEntity(any())).thenReturn(new Staff());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findAllById(any())).thenReturn(Arrays.asList());
        when(staffRepository.save(any())).thenReturn(new Staff());
        when(staffMapper.toDto(any(Staff.class))).thenReturn(new StaffResponseDto());

        StaffCreateRequestDto nullStoreRequest = StaffCreateRequestDto.builder()
                .name("Test Staff")
                .email("teststaff@gmail.com")
                .phoneNumber("0123456789")
                .dob(LocalDate.of(1990, 1, 1))
                .address("Test Address")
                .password("123456")
                .roles(Set.of("STAFF"))
                .managerID(null)
                .storeID(null) // null storeID
                .build();

        // When & Then - Không nên throw exception
        assertDoesNotThrow(() -> {
            StaffResponseDto result = staffService.createStaff(nullStoreRequest);
            assertNotNull(result);
        });

        // Verify store không được tìm kiếm khi storeID là null
        verify(storeRepo, never()).findById(anyString());
    }

    @Test
    void testUpdateStaffWithValidStore() {
        // Given
        Staff existingStaff = new Staff();
        existingStaff.setStaffID("ST0000001");
        existingStaff.setEmail("old@gmail.com");
        existingStaff.setPhoneNumber("0111111111");

        when(staffRepository.findById("ST0000001")).thenReturn(Optional.of(existingStaff));
        when(storeRepo.findById("STORE001")).thenReturn(Optional.of(testStore));
        when(roleRepository.findAllById(any())).thenReturn(Arrays.asList());
        when(staffRepository.save(any())).thenReturn(existingStaff);
        when(staffMapper.toDto(any(Staff.class))).thenReturn(new StaffResponseDto());

        // When
        StaffResponseDto result = staffService.updateStaff("ST0000001", updateRequestDto);

        // Then
        assertNotNull(result);
        verify(storeRepo, times(1)).findById("STORE001");
        verify(staffRepository, times(1)).save(existingStaff);
    }

    @Test
    void testUpdateStaffWithInvalidStore() {
        // Given
        Staff existingStaff = new Staff();
        existingStaff.setStaffID("ST0000001");
        existingStaff.setEmail("old@gmail.com");
        existingStaff.setPhoneNumber("0111111111");

        when(staffRepository.findById("ST0000001")).thenReturn(Optional.of(existingStaff));
        when(storeRepo.findById("INVALID_STORE")).thenReturn(Optional.empty());

        StaffUpdateRequestDto invalidRequest = new StaffUpdateRequestDto(
                "Updated Staff",
                "updatedstaff@gmail.com",
                "0987654321",
                LocalDate.of(1995, 5, 15),
                "Updated Address",
                StatusType.active,
                Arrays.asList("ADMIN"),
                null,
                "INVALID_STORE" // invalid storeID
        );

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> {
            staffService.updateStaff("ST0000001", invalidRequest);
        });

        assertEquals(ErrorCode.STORE_NOT_FOUND, exception.getErrorCode());
    }
}
