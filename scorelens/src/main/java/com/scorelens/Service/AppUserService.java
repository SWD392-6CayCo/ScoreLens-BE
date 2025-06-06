package com.scorelens.Service;

import com.scorelens.Entity.Customer;
import com.scorelens.Entity.Staff;
import com.scorelens.Enums.StatusType;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.CustomerMapper;
import com.scorelens.Mapper.StaffMapper;
import com.scorelens.Repository.CustomerRepo;
import com.scorelens.Repository.StaffRepository;
import com.scorelens.Security.AppUser;
import com.scorelens.Service.Interface.IAppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AppUserService implements IAppUserService {

    private final CustomerRepo customerRepo;
    private final StaffRepository staffRepo;
    private final PasswordEncoder passwordEncoder;

    public AppUser authenticateUser(String email, String password) {
        return customerRepo.findByEmail(email)
                .filter(customer -> {
                    if (customer.getStatus() == StatusType.inactive) {
                        throw new AppException(ErrorCode.USER_INACTIVE);
                    }
                    return passwordEncoder.matches(password, customer.getPassword());
                })
                .map(c -> (AppUser) c)
                .or(() -> staffRepo.findByEmail(email)
                        .filter(staff -> {
                            if (staff.getStatus() == StatusType.inactive) {
                                throw new AppException(ErrorCode.USER_INACTIVE);
                            }
                            return passwordEncoder.matches(password, staff.getPassword());
                        })
                        .map(s -> (AppUser) s))
                .orElseThrow(() -> new AppException(ErrorCode.INCORRECT_EMAIL_OR_PASSWORD));
    }
}
