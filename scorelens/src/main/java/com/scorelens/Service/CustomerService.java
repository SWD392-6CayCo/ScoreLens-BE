package com.scorelens.Service;

import com.scorelens.DTOs.Request.ChangePasswordRequestDto;
import com.scorelens.DTOs.Request.CustomerCreateRequestDto;
import com.scorelens.DTOs.Request.CustomerUpdateRequestDto;
import com.scorelens.DTOs.Request.PageableRequestDto;
import com.scorelens.DTOs.Response.CustomerResponseDto;
import com.scorelens.DTOs.Response.PageableResponseDto;
import com.scorelens.Entity.Customer;
import com.scorelens.Enums.StatusType;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.CustomerMapper;
import com.scorelens.Repository.CustomerRepo;
import com.scorelens.Repository.StaffRepository;
import com.scorelens.Service.Interface.ICustomerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomerService implements ICustomerService {

    @Autowired
    CustomerRepo customerRepo;
    @Autowired
    CustomerMapper customerMapper;
    @Autowired
    StaffRepository staffRepo;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserValidatorService userValidatorService;

    //-------------------------------- GET ---------------------------------
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('GET_CUSTOMER_LIST')")
    public List<CustomerResponseDto> findAll() {
        List<Customer> customers = customerRepo.findAll();
        if(customers.isEmpty()){
                throw new AppException(ErrorCode.EMPTY_LIST);
        }
        return customerMapper.toDtoList(customers);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('GET_CUSTOMER_DETAIL')")
    public CustomerResponseDto findById(String id) {
                Optional<Customer> optionalCus = customerRepo.findById(id);
        if (optionalCus.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }
        CustomerResponseDto responseDto = customerMapper.toDto(optionalCus.get());

//        String returnObjectName = responseDto.getName(); // assuming getName() exists
//        String authenticatedName = SecurityContextHolder.getContext().getAuthentication().getName();
//        log.info("returnObject.email = {}, authentication.name = {}", returnObjectName, authenticatedName);

        return responseDto;
    }

    @Override
    @PostAuthorize("returnObject.email == authentication.name")
    public CustomerResponseDto getMyProfile() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName(); //authentication.name là email
        Customer c = customerRepo.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        return customerMapper.toDto(c);
    }

    //-------------------------------- DELETE ---------------------------------
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_CUSTOMER')")
    public boolean deleteById(String id) {
        if(customerRepo.existsById(id)) {
            customerRepo.deleteById(id);
            return true;
        }
        return false;
    }

    //-------------------------------- UPDATE ---------------------------------
    @Override
    @PostAuthorize("hasRole('ADMIN') " +
            "or returnObject.email == authentication.name " +
            "or hasAuthority('UPDATE_CUSTOMER_DETAIL')")
    public CustomerResponseDto updateCustomer(String id, CustomerUpdateRequestDto requestDto) {
        //Lấy ra Customer cần update
        Customer customer = customerRepo.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST)
        );

        //Check email & phone validation
        userValidatorService.validateEmailUnique(requestDto.getEmail(), customer.getEmail());
        userValidatorService.validatePhoneUnique(requestDto.getPhoneNumber(), customer.getPhoneNumber());

        //call updateEntity func() in MapStuct to map requestDto into Entity
        customerMapper.updateEntity(customer, requestDto);
        // Mã hóa password
        //customer.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        customer.setUpdateAt(LocalDate.now());
        customerRepo.save(customer);
        CustomerResponseDto responseDto = customerMapper.toDto(customer);
        return responseDto;
    }

    //-------------------------------- CREATE ---------------------------------
    @Override
    public CustomerResponseDto createCustomer(CustomerCreateRequestDto request){
        //Kiểm tra xem Email và PhoneNumber đã đc sử dụng hay chưa-------
        userValidatorService.validateEmailAndPhoneUnique(request.getEmail(), request.getPhoneNumber());
        //----------------------------------------------------------------

        //Map từ dto sang Entity
        Customer customer = customerMapper.toEntity(request);

        //set các giá trị còn lại của customer
        customer.setCreateAt(LocalDate.now());
        customer.setStatus(StatusType.active);
        customer.setType("normal");
        //upload ảnh...

        //mã hóa password
        customer.setPassword(passwordEncoder.encode(request.getPassword()));

        customerRepo.save(customer);
        CustomerResponseDto responseDto = customerMapper.toDto(customer);
        return responseDto;
    }

    //-------------------------------- UPDATE STATUS BANED/UNBANED ---------------------------------
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_CUSTOMER_STATUS')")
    public boolean updateCustomerStatus(String id, String status) {
        boolean check = true;
        Customer c = customerRepo.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST));
        if (!status.equalsIgnoreCase(StatusType.active.toString()) && !status.equalsIgnoreCase(StatusType.inactive.toString())) {
            throw new AppException(ErrorCode.INVALID_STATUS); // Optional: bạn có thể thêm enum hoặc custom error code
        }
        c.setStatus(StatusType.valueOf(status));
        c.setUpdateAt(LocalDate.now());
        customerRepo.save(c);

        return check;
    }

    //    ---------------------------- UPDATE PASSWORD-----------------------------------
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_CUSTOMER_PASSWORD')")
    public boolean updatePassword (String id, ChangePasswordRequestDto requestDto){
        boolean check = false;
        Customer c = customerRepo.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST));

        String cusPassword = c.getPassword();
        if(passwordEncoder.matches(requestDto.getOldPassword(), cusPassword)){ //password nhập vào đúng với pass của user
            //cho phép đổi
            if(requestDto.getOldPassword().equals(requestDto.getNewPassword())){ //pass mới trùng với pass cũ
                throw new AppException(ErrorCode.DUPLICATED_PASSWORD);
            }
            //pass mới khác pass cũ
            c.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
            customerRepo.save(c);
            check = true;
        } else {
            //không cho phép đổi
            System.out.println(requestDto.getOldPassword());
            System.out.println(cusPassword);
            throw new AppException(ErrorCode.NOT_MATCH_PASSWORD);

        }
        return check;
    }
    //    --------------------------------------------------------------------------

    //-------------------------------- PAGINATION ---------------------------------
    public PageableResponseDto<CustomerResponseDto> getCustomersWithPagination(PageableRequestDto request) {
        // Create sort
        Sort sort = Sort.by(
                "asc".equalsIgnoreCase(request.getSortDirection())
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC,
                request.getSortBy()
        );

        // Create pageable
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        // Create specification for filtering
        Specification<Customer> spec = createCustomerSpecification(request);

        // Get page data
        Page<Customer> customerPage = customerRepo.findAll(spec, pageable);

        // Convert to DTO
        List<CustomerResponseDto> customerDtos = customerMapper.toDtoList(customerPage.getContent());

        return PageableResponseDto.<CustomerResponseDto>builder()
                .content(customerDtos)
                .page(customerPage.getNumber() + 1) // Convert 0-based to 1-based for response
                .size(customerPage.getSize())
                .totalElements(customerPage.getTotalElements())
                .totalPages(customerPage.getTotalPages())
                .first(customerPage.isFirst())
                .last(customerPage.isLast())
                .empty(customerPage.isEmpty())
                .sortBy(request.getSortBy())
                .sortDirection(request.getSortDirection())
                .build();
    }

    private Specification<Customer> createCustomerSpecification(PageableRequestDto request) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();

            // Search by name, email, or phone
            if (request.getSearch() != null && !request.getSearch().trim().isEmpty()) {
                String searchPattern = "%" + request.getSearch().toLowerCase() + "%";
                var searchPredicate = criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("phoneNumber")), searchPattern)
                );
                predicates.add(searchPredicate);
            }

            // Filter by status
            if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
                StatusType statusType = "active".equalsIgnoreCase(request.getStatus())
                    ? StatusType.active
                    : StatusType.inactive;
                predicates.add(criteriaBuilder.equal(root.get("status"), statusType));
            }

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
