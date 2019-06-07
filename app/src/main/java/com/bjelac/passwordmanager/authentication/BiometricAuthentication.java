package com.bjelac.passwordmanager.authentication;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;

import com.bjelac.passwordmanager.R;
import com.bjelac.passwordmanager.utils.LoggerUtils;
import com.bjelac.passwordmanager.utils.NotifyUser;
import com.bjelac.passwordmanager.views.MainActivity;

import static android.content.Context.KEYGUARD_SERVICE;

public class BiometricAuthentication implements IBiometricAuthentication {

    private final String TAG = BiometricAuthentication.class.getName();
    private final MainActivity mainActivity;

    public BiometricAuthentication(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public boolean checkBiometricSupport() {
        KeyguardManager keyguardManager =
                (KeyguardManager) mainActivity.getSystemService(KEYGUARD_SERVICE);

        PackageManager packageManager = mainActivity.getPackageManager();

        if (!keyguardManager.isKeyguardSecure()) {
            NotifyUser.notify(mainActivity, mainActivity.getResources().getString(R.string.msg_isKeyguardSecure));
            LoggerUtils.logD(TAG, "Lock screen security not enabled in SettingsFragmentActivity.");
            return false;
        }

        if (ActivityCompat.checkSelfPermission(mainActivity,
                Manifest.permission.USE_BIOMETRIC) !=
                PackageManager.PERMISSION_GRANTED) {

            NotifyUser.notify(mainActivity, mainActivity.getResources().getString(R.string.msg_checkSelfPermission));
            LoggerUtils.logD(TAG, "Fingerprint authentication permission not enabled.");
            return false;
        }

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            return true;
        }
        return true;
    }

    @Override
    public void authenticateUser() {
        mainActivity.showSetPasswordControls(false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(mainActivity)
                    .setTitle(mainActivity.getResources().getString(R.string.lbl_biometricPromptTitle))
                    .setSubtitle(mainActivity.getResources().getString(R.string.lbl_biometricPromptSubtitle))
                    .setDescription(mainActivity.getResources().getString(R.string.lbl_biometricPromptDescription))
                    .setNegativeButton(mainActivity.getResources().getText(R.string.btn_signInWithPassword), mainActivity.getMainExecutor(),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mainActivity.getResources().getString(R.string.msg_authenticationCanceled);
                                    mainActivity.showSignInControls();
                                    LoggerUtils.logD(TAG, "Biometric authentication cancelled by user. Starting password authentication.");
                                }
                            })
                    .build();

            biometricPrompt.authenticate(getCancellationSignal(), mainActivity.getMainExecutor(),
                    getAuthenticationCallback());
        } else {
            mainActivity.showSignInControls();
        }
    }

    private BiometricPrompt.AuthenticationCallback getAuthenticationCallback() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            return new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode,
                                                  CharSequence errString) {
                    if (errorCode == 10) {
                        mainActivity.finishAffinity();
                    } else {
                        LoggerUtils.logD(TAG, "Authentication error: " + errString);
                        mainActivity.showEnableBiometricInstructions();
                        super.onAuthenticationError(errorCode, errString);
                    }
                }

                @Override
                public void onAuthenticationHelp(int helpCode,
                                                 CharSequence helpString) {
                    super.onAuthenticationHelp(helpCode, helpString);
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                }

                @Override
                public void onAuthenticationSucceeded(
                        BiometricPrompt.AuthenticationResult result) {
                    LoggerUtils.logD(TAG, "Biometric authentication successful.");
                    mainActivity.startVaultActivity();
                    super.onAuthenticationSucceeded(result);
                }
            };
        } else {
            return null;
        }
    }

    private CancellationSignal getCancellationSignal() {
        CancellationSignal cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(
                new CancellationSignal.OnCancelListener() {
                    @Override
                    public void onCancel() {
                        LoggerUtils.logD(TAG, "Authentication cancelled via signal.");
                    }
                });
        return cancellationSignal;
    }
}