package com.bjelac.passwordmanager.authentication;

public interface IPasswordAuthentication {
    void setMasterPasswordOrLogIn(String password1, String password2);
    String getMasterPasswordHash();
}