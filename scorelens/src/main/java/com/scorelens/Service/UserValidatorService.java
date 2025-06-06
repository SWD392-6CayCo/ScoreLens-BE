package com.scorelens.Service;

import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Repository.CustomerRepo;
import com.scorelens.Repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidatorService {
    private final CustomerRepo customerRepo;
    private final StaffRepository staffRepo;

    //---------------------------- UDPATE EMAIL & PHONENUMBER -------------------------------------
    public void validateEmailUnique(String email, String currentEmail){
        boolean isEmailTaken = (!email.equals(currentEmail)) &&
                (customerRepo.existsByEmail(email) || staffRepo.existsByEmail(email));
        if(isEmailTaken){
            throw new AppException(ErrorCode.EMAIL_EXSITED);
        }
    }

    public void validatePhoneUnique(String phone, String currentPhone){
        boolean isPhoneTaken = (!phone.equals(currentPhone)) &&
                (customerRepo.existsByPhoneNumber(phone) || staffRepo.existsByPhoneNumber(phone));
        if(isPhoneTaken){
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }
    }

    //----------------------------- CREATE EMAIL & PHONENUMBER
    public void validateEmailAndPhoneUnique(String email, String phone) {
        if (customerRepo.existsByEmail(email) || staffRepo.existsByEmail(email)) {
            throw new AppException(ErrorCode.EMAIL_EXSITED);
        }
        if (customerRepo.existsByPhoneNumber(phone) || staffRepo.existsByPhoneNumber(phone)) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }
    }
}
