package com.scorelens.Service.Interface;

public interface IUserValidatorService {
    void validateEmailUnique(String email, String currentEmail);
    void validatePhoneUnique(String phone, String currentPhone);
    void validateEmailAndPhoneUnique(String email, String phone);
}
