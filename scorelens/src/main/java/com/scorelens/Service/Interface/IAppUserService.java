package com.scorelens.Service.Interface;

import com.scorelens.Security.AppUser;

public interface IAppUserService {
    AppUser authenticateUser(String email, String password);
}
