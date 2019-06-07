package com.bjelac.passwordmanager.presenter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;

import com.bjelac.passwordmanager.R;
import com.bjelac.passwordmanager.database.DataProviderService;
import com.bjelac.passwordmanager.database.DatabaseService;
import com.bjelac.passwordmanager.models.LoginModel;
import com.bjelac.passwordmanager.models.TwoFAModel;
import com.bjelac.passwordmanager.utils.ApplicationContextProvider;
import com.bjelac.passwordmanager.utils.LoggerUtils;
import com.bjelac.passwordmanager.utils.NotifyUser;
import com.bjelac.passwordmanager.utils.SSH1Service;
import com.bjelac.passwordmanager.views.VaultTwoFAuthFragmentActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SettingsFragmentPresenter {
    private final String TAG = SettingsFragmentPresenter.class.getName();
    private View view;
    private Activity activity;

    public SettingsFragmentPresenter(View view, Activity activity) {
        this.view = view;
        this.activity = activity;
    }

    public void resetMasterPassword(String password1, String password2) {
        EditText passwordBox1 = view.findViewById(R.id.txtPassword1);
        EditText passwordBox2 = view.findViewById(R.id.txtPassword2);

        if (!password1.isEmpty() && !password2.isEmpty()) {
            if (password1.equals(password2)) {
                final String hashedPassword = SSH1Service.getSHA1Hash(password1);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(view.getContext());
                SharedPreferences.Editor editor = preferences.edit();
                String key = "identification";
                editor.putString(key, hashedPassword);
                editor.apply();

                NotifyUser.notify(view.getContext(), view.getResources().getString(R.string.msg_masterPasswordChanged));
                passwordBox1.setText("");
                passwordBox2.setText("");

            } else {
                NotifyUser.notify(view.getContext(), view.getResources().getString(R.string.msg_passwordDoNotMatch));
                passwordBox1.setText("");
                passwordBox2.setText("");
            }
        } else {
            NotifyUser.notify(view.getContext(), view.getResources().getString(R.string.msg_passwordCanNotBeEmpty));
        }
    }

    public void backUpBD(Uri fileUri, String key) {
        Gson gson = new Gson();
        DatabaseService dbService = new DatabaseService();

        List<LoginModel> encryptedLoginModels = new ArrayList<>();
        List<TwoFAModel> encryptedTwoFAModels = new ArrayList<>();

        String keyHash = SSH1Service.getSHA1Hash(key);
        byte[] enDecryptionKey = Arrays.copyOf(keyHash.getBytes(), 16);

        for (LoginModel lm : DataProviderService.getInstance().allDecryptedLoginModels) {
            encryptedLoginModels.add(dbService.encryptBackUpLoginModel(lm, enDecryptionKey));
        }

        for (TwoFAModel twoFAModel : DataProviderService.getInstance().allDecryptedTwoFAModels) {
            encryptedTwoFAModels.add(dbService.encryptBackUp2FAModel(twoFAModel, enDecryptionKey));
        }

        String loginModelsJson = gson.toJson(encryptedLoginModels);
        String twoFAModelsJson = gson.toJson(encryptedTwoFAModels);
        String textDataToSave = loginModelsJson + "splitHere" + twoFAModelsJson;
        try {
            ParcelFileDescriptor parcelFileDescriptor = activity.getContentResolver().openFileDescriptor(fileUri, "w");
            if (parcelFileDescriptor != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
                fileOutputStream.write(textDataToSave.getBytes());
                fileOutputStream.close();
                parcelFileDescriptor.close();
            }
            NotifyUser.notify(ApplicationContextProvider.getContext(), ApplicationContextProvider.getContext().getResources().getString(R.string.msg_backupSeceded));
        } catch (Exception e){
            LoggerUtils.logD(TAG, e.getMessage());
        }
    }

    public void restoreBD(Uri fileUri, String key) {
        try {
            InputStream inputStream = activity.getContentResolver().openInputStream(fileUri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            DatabaseService dbService = new DatabaseService();
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                stringBuilder.append(currentLine);
            }
            if (inputStream != null) inputStream.close();

            String rawTextData = stringBuilder.toString();
            String[] jsonObjects = rawTextData.split("splitHere");

            Gson gson = new Gson();
            Type loginCollectionType = new TypeToken<Collection<LoginModel>>() {
            }.getType();
            Collection<LoginModel> restoredEncryptedLoginModel = gson.fromJson(jsonObjects[0], loginCollectionType);
            Type twoFACollectionType = new TypeToken<Collection<TwoFAModel>>() {
            }.getType();
            Collection<TwoFAModel> restoredEncryptedTwoFAModel = gson.fromJson(jsonObjects[1], twoFACollectionType);

            String keyHash = SSH1Service.getSHA1Hash(key);
            byte[] enDecryptionKey = Arrays.copyOf(keyHash.getBytes(), 16);

            List<LoginModel> restoredDecryptedLoginModel = new ArrayList<>();
            for (LoginModel logInModel : restoredEncryptedLoginModel) {
                restoredDecryptedLoginModel.add(dbService.decryptBackUpLoginModel(logInModel, enDecryptionKey));
            }
            List<TwoFAModel> restoredDecryptedTwoFAModel = new ArrayList<>();
            for (TwoFAModel twoFAModel : restoredEncryptedTwoFAModel) {
                restoredDecryptedTwoFAModel.add(dbService.decryptBackUp2FAModel(twoFAModel, enDecryptionKey));
            }

            dbService.deleteAllData();

            for (LoginModel logInModel : restoredDecryptedLoginModel) {
                if (logInModel.getSummary() != null) {
                    dbService.saveLoginToDB(logInModel);
                }
            }

            for (TwoFAModel twoFAModel : restoredDecryptedTwoFAModel) {
                if (twoFAModel.getSummary() != null) {
                    dbService.save2FAToDB(twoFAModel);
                }
            }

            VaultLoginFragmentPresenter.refreshDataSet();
            VaultTwoFAuthFragmentPresenter.resetAdapter();

            //Move add button to corner
            VaultTwoFAuthFragmentActivity.twoFAuthFragmentActivity.onResume();

            if (DataProviderService.getInstance().allDecryptedLoginModels.size() == 0 &&
                    DataProviderService.getInstance().allDecryptedTwoFAModels.size() == 0) {
                NotifyUser.notify(view.getContext(), view.getResources().getString(R.string.msg_decryptionFailed));
            } else {
                NotifyUser.notify(view.getContext(), view.getResources().getString(R.string.msg_restoringSeceded));
            }

            LoggerUtils.logD(TAG, jsonObjects[0]);
            LoggerUtils.logD(TAG, jsonObjects[1]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}