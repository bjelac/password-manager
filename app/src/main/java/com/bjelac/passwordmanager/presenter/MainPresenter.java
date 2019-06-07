package com.bjelac.passwordmanager.presenter;

import com.bjelac.passwordmanager.database.DataProviderService;
import com.bjelac.passwordmanager.authentication.BiometricAuthentication;
import com.bjelac.passwordmanager.authentication.IBiometricAuthentication;
import com.bjelac.passwordmanager.authentication.IPasswordAuthentication;
import com.bjelac.passwordmanager.authentication.PasswordAuthentication;
import com.bjelac.passwordmanager.utils.ApplicationContextProvider;
import com.bjelac.passwordmanager.views.IMainActivity;
import com.bjelac.passwordmanager.views.MainActivity;
import com.orm.SugarContext;

public class MainPresenter implements IMainPresenter {
    private MainActivity mainActivity;
    private IBiometricAuthentication biometricAuthentication;
    private IPasswordAuthentication passwordAuthentication;
    private String masterPasswordHash;

    public MainPresenter(IMainActivity view) {
        this.mainActivity = (MainActivity) view;
        SugarContext.init(ApplicationContextProvider.getContext());
        this.biometricAuthentication = new BiometricAuthentication(mainActivity);
        this.passwordAuthentication = new PasswordAuthentication(mainActivity);
        this.masterPasswordHash = passwordAuthentication.getMasterPasswordHash();
        decryptData();
    }

    private void decryptData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataProviderService.getInstance();
            }
        }).start();
    }

    @Override
    public void authenticateUserWithBiometric() {
        if (masterPasswordHash == null) {
            mainActivity.showSetPasswordControls(true);
        } else {
            IBiometricAuthentication biometricAuthentication = new BiometricAuthentication(mainActivity);
            if (isBiometricAuthenticationSupported()) {
                biometricAuthentication.authenticateUser();
            } else {
                mainActivity.showSignInControls();
            }
        }
    }

    @Override
    public void authenticateUserWithPassword(String password1, String password2) {
        passwordAuthentication.setMasterPasswordOrLogIn(password1, password2);
    }

    public void updateMasterPasswordHash() {
        this.masterPasswordHash = passwordAuthentication.getMasterPasswordHash();
    }

    private boolean isBiometricAuthenticationSupported() {
        return biometricAuthentication.checkBiometricSupport();
    }
}