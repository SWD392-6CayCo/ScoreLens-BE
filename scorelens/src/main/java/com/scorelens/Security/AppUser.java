package com.scorelens.Security;

import com.scorelens.Enums.UserType;

public interface AppUser
{
    String getId();
    String getEmail();
    String getPassword();
    UserType getUserType(); // CUSTOMER | STAFF
}
