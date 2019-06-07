package com.bjelac.passwordmanager.views;

public interface IMainActivity {
    void showSetPasswordControls(boolean bool);
    void showEnableBiometricInstructions();
    void startVaultActivity();
    void showSignInControls();
}
