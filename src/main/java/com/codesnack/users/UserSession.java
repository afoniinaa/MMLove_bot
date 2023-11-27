package com.codesnack.users;

public interface UserSession {
    UserState getState();

    void setState(UserState state);

    String getFirstName();

    void setFirstName(String name);

    Long getProfile();

    void setId(Long profile);
}
