package com.scorelens.Service;

import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Repository.CustomerRepo;
import com.scorelens.Repository.StaffRepository;
import com.scorelens.Service.Interface.IUserValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidatorService implements IUserValidatorService {
    private final CustomerRepo customerRepo;
    private final StaffRepository staffRepo;

    //---------------------------- UDPATE EMAIL & PHONENUMBER -------------------------------------
    @Override
    public void validateEmailUnique(String email, String currentEmail){
        boolean isEmailTaken = (!email.equals(currentEmail)) &&
                (customerRepo.existsByEmail(email) || staffRepo.existsByEmail(email));
        if(isEmailTaken){
            throw new AppException(ErrorCode.EMAIL_EXSITED);
        }
    }

    @Override
    public void validatePhoneUnique(String phone, String currentPhone){
        // Nếu phone là null thì không cần validate
        if (phone == null) {
            return;
        }

        boolean isPhoneTaken = (!phone.equals(currentPhone)) &&
                (customerRepo.existsByPhoneNumber(phone) || staffRepo.existsByPhoneNumber(phone));
        if(isPhoneTaken){
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }
    }

    //----------------------------- CREATE EMAIL & PHONENUMBER
    @Override
    public void validateEmailAndPhoneUnique(String email, String phone) {
        if (customerRepo.existsByEmail(email) || staffRepo.existsByEmail(email)) {
            throw new AppException(ErrorCode.EMAIL_EXSITED);
        }
        if (phone != null && (customerRepo.existsByPhoneNumber(phone) || staffRepo.existsByPhoneNumber(phone))) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }
    }
}
