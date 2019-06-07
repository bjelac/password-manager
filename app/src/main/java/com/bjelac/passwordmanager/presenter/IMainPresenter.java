package com.bjelac.passwordmanager.presenter;

public interface IMainPresenter {
    void authenticateUserWithBiometric();
    void authenticateUserWithPassword(String password1, String password2);
    void updateMasterPasswordHash();
}