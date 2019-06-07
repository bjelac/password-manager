package com.bjelac.passwordmanager.authentication;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bjelac.passwordmanager.R;
import com.bjelac.passwordmanager.database.DatabaseService;
import com.bjelac.passwordmanager.presenter.MainPresenter;
import com.bjelac.passwordmanager.utils.LoggerUtils;
import com.bjelac.passwordmanager.utils.NotifyUser;
import com.bjelac.passwordmanager.utils.SSH1Service;
import com.bjelac.passwordmanager.views.IMainActivity;
import com.bjelac.passwordmanager.views.MainActivity;

public class PasswordAuthentication implements IPasswordAuthentication {

    private final String TAG = MainPresenter.class.getName();
    private final String key = "identification";
    private final int failureCounter = 5;
    private final boolean securityEnabled = true;

    private MainActivity mainActivity;
    private String masterPassword;
    private SharedPreferences preferences;
    private int logInFailedCounter;

    public PasswordAuthentication(IMainActivity view) {
        this.mainActivity = (MainActivity) view;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        this.masterPassword = getMasterPasswordHash();
        this.logInFailedCounter = failureCounter;

    }

    @Override
    public void setMasterPasswordOrLogIn(String password1, String password2) {
        if (masterPassword == null) {
            if (!password1.isEmpty()) {
                if (password1.equals(password2)) {
                    if (setMasterPassword(password1)) {
                        masterPassword = getMasterPasswordHash();
                        mainActivity.showSetPasswordControls(false);
                        mainActivity.startVaultActivity();
                    }
                } else {
                    NotifyUser.notify(mainActivity, mainActivity.getResources().getString(R.string.msg_passwordDoNotMatch));
                }
            } else {
                NotifyUser.notify(mainActivity, mainActivity.getResources().getString(R.string.msg_passwordCanNotBeEmpty));
            }
        } else {
            if (authenticateWithMasterPassword(password1)) {
                mainActivity.showSetPasswordControls(false);
                logInFailedCounter = failureCounter;
                mainActivity.startVaultActivity();
                LoggerUtils.logD(TAG, "Password authentication successful.");
            } else {
                logInFailedCounter--;
                if (securityEnabled) {
                    if (logInFailedCounter == 0) {
                        masterPassword = null;
                        logInFailedCounter = failureCounter;

                        DatabaseService dbService = new DatabaseService();
                        dbService.deleteAllData();
                        deleteMasterPassword();
                        mainActivity.showSetPasswordControls(true);
                        NotifyUser.notify(mainActivity, mainActivity.getResources().getString(R.string.msg_securityIncident));
                        LoggerUtils.logD(TAG, "Security incident! Dropping password database!");
                    } else {
                        NotifyUser.notify(mainActivity, mainActivity.getResources().getString(R.string.msg_wrongPassword, logInFailedCounter));
                        mainActivity.resetTxtPassword1Text();
                    }
                } else {
                    NotifyUser.notify(mainActivity, mainActivity.getResources().getString(R.string.msg_wrongPassword));
                    mainActivity.resetTxtPassword1Text();
                }
            }
        }
    }

    @Override
    public String getMasterPasswordHash() {
        return preferences.getString(key, null);
    }

    private boolean setMasterPassword(String password) {
        String hashedPassword = SSH1Service.getSHA1Hash(password);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, hashedPassword);
        editor.commit();

        if (getMasterPasswordHash().equals(SSH1Service.getSHA1Hash(password))) {
            LoggerUtils.logD(TAG, "Master password saved!");
            return true;
        }
        return false;
    }

    private void deleteMasterPassword() {
        preferences.edit().remove(key).apply();
    }

    private boolean authenticateWithMasterPassword(String password) {
        return masterPassword.equals(SSH1Service.getSHA1Hash(password));
    }
}
