package com.bjelac.passwordmanager.database;

import com.bjelac.passwordmanager.R;
import com.bjelac.passwordmanager.encryption.AES_EncryptionKeyProvider;
import com.bjelac.passwordmanager.models.LoginModel;
import com.bjelac.passwordmanager.models.TwoFAModel;
import com.bjelac.passwordmanager.presenter.VaultLoginFragmentPresenter;
import com.bjelac.passwordmanager.utils.ApplicationContextProvider;
import com.bjelac.passwordmanager.views.VaultLoginFragmentActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataProviderService {
    public static boolean isLMDecryptionFinished = false;
    public static boolean is2FADecryptionFinished = false;
    private static DataProviderService singleInstance;
    public List<LoginModel> allDecryptedLoginModels;
    public List<LoginModel> filteredLoginModels;
    public Set<String> loginTags;
    public List<TwoFAModel> allDecryptedTwoFAModels;

    public static boolean activityLocked = true;

    private DataProviderService() {
        getDataFromDB();
    }

    public static DataProviderService getInstance() {
        if (singleInstance == null) {
            singleInstance = new DataProviderService();
            AES_EncryptionKeyProvider.getInstance();
        }
        return singleInstance;
    }

    private void getDataFromDB() {
        DatabaseService databaseService = new DatabaseService();
        allDecryptedLoginModels = databaseService.getLogInModels();
        filteredLoginModels = allDecryptedLoginModels;
        loginTags = getTagsFromDecryptedLogInModels();
        allDecryptedTwoFAModels = databaseService.getTwoFaModels();
    }

    private Set<String> getTagsFromDecryptedLogInModels() {
        Set<String> tags = new HashSet<>();
        for (LoginModel login : allDecryptedLoginModels) {
            if (login.getTag() != null && !login.getTag().isEmpty() && !login.getTag().equals(ApplicationContextProvider.getContext().getResources().getString(R.string.lbl_defaultTag))) {
                tags.add(login.getTag());
            }
        }
        tags.add(ApplicationContextProvider.getContext().getResources().getString(R.string.lbl_defaultTag));
        return tags;
    }

    public void refreshTags() {
        loginTags = getTagsFromDecryptedLogInModels();
    }

    public void filterLogins(String tag) {
        List<LoginModel> filteredLogins = new ArrayList<>();
        for (LoginModel logInModel : allDecryptedLoginModels) {
            if (logInModel.getTag().equals(tag)) {
                filteredLogins.add(logInModel);
            }
        }
        filteredLoginModels = filteredLogins;
        VaultLoginFragmentPresenter.refreshDataSet();
    }

    public void removeFilter() {
        filteredLoginModels = allDecryptedLoginModels;
        VaultLoginFragmentPresenter.refreshDataSet();
        VaultLoginFragmentActivity.tagFilterSpinner.setSelection(0);
    }

    public static void destroyInstance() {
        singleInstance = null;
        AES_EncryptionKeyProvider.destroyInstance();
    }
}