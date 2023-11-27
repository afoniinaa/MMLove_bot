package com.codesnack.users;

public class InMemoryUserSession implements UserSession {
    private UserState state = UserState.NEW_USER;
    private String name;
    private Long profile;

    @Override
    public UserState getState() {
        return state;
    }

    @Override
    public void setState(UserState state) {
        this.state = state;
    }

    @Override
    public String getFirstName() {
        return name;
    }

    @Override
    public void setFirstName(String name) {
        this.name = name;
    }

    @Override
    public Long getProfile() {
        return profile;
    }

    @Override
    public void setId(Long profile) {
        this.profile = profile;
    }
}
