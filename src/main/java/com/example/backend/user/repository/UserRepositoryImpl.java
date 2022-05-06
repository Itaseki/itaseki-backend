package com.example.backend.user.repository;

public class UserRepositoryImpl implements UserRepository{

    @Override
    public boolean idDuplicate(String id) {
        return false;
    }
}
