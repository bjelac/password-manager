package com.bjelac.passwordmanager.authentication;

public interface IBiometricAuthentication {
    boolean checkBiometricSupport();
    void authenticateUser();
}