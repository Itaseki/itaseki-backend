package com.example.backend.user.service;

public class UserServiceImpl implements UserService{

    @Override
    public boolean idDuplicateCheck(String id) {
        return false;
    }
}
