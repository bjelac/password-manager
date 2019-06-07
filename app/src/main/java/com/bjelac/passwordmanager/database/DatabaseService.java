package com.bjelac.passwordmanager.database;

import android.database.sqlite.SQLiteException;

import com.bjelac.passwordmanager.R;
import com.bjelac.passwordmanager.encryption.AES_EncryptionKeyProvider;
import com.bjelac.passwordmanager.encryption.AES_EncryptionService;
import com.bjelac.passwordmanager.models.LoginModel;
import com.bjelac.passwordmanager.models.TwoFAModel;
import com.bjelac.passwordmanager.presenter.VaultLoginFragmentPresenter;
import com.bjelac.passwordmanager.presenter.VaultTwoFAuthFragmentPresenter;
import com.bjelac.passwordmanager.utils.ApplicationContextProvider;
import com.bjelac.passwordmanager.utils.DatabaseIdGenerator;
import com.bjelac.passwordmanager.utils.LoggerUtils;
import com.bjelac.passwordmanager.utils.NotifyUser;
import com.bjelac.passwordmanager.utils.SummaryComparator;
import com.bjelac.passwordmanager.views.VaultTwoFAuthFragmentActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DatabaseService {
    private final String TAG = DatabaseService.class.getName();
    private AES_EncryptionService aesEncryptionService;
    private byte[] enDecryptionKey;

    public DatabaseService() {
        enDecryptionKey = AES_EncryptionKeyProvider.getInstance().getEncryptionKey().getEncoded();
        aesEncryptionService = new AES_EncryptionService();
    }
    /*
    Difference between the sugar ORM save() and update() method is:
    save() uses the id field to select the record to be updated,
    update() makes use of any field that might have been annotated with @Unique to choose the record, only using the id as a last resort.
     */

    public void saveLoginToDB(final LoginModel loginModel) {
        DataProviderService.getInstance().allDecryptedLoginModels.add(loginModel);
        Collections.sort(DataProviderService.getInstance().allDecryptedLoginModels, new SummaryComparator());
        DataProviderService.getInstance().loginTags.add(loginModel.getTag());

        DataProviderService.getInstance().removeFilter();

        new Thread(new Runnable() {
            @Override
            public void run() {
                loginModel.setInitializationVector(aesEncryptionService.getRandomVi());
                LoginModel encryptedLoginModel = new LoginModel(encryptLoginModel(loginModel, enDecryptionKey));
                encryptedLoginModel.save();
                LoggerUtils.logD(TAG, "Data saved.");
            }
        }).start();
    }

    public void updateLoginInDB(final LoginModel loginModel) {
        String oldTag = loginModel.getTag();

        List<LoginModel> loginModels = DataProviderService.getInstance().allDecryptedLoginModels;
        if (loginModels != null) {
            int tagRepetition = 0;
            for (int i = 0; i < loginModels.size(); i++) {
                if (loginModels.get(i).getId().equals(loginModel.getId())) {
                    loginModels.set(i, loginModel);
                }
                if (oldTag.equals(loginModels.get(i).getTag())) {
                    tagRepetition++;
                }
            }
            if (tagRepetition < 1 && oldTag != null) {
                DataProviderService.getInstance().loginTags.remove(oldTag);
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                reEncryptLoginModel(loginModel, enDecryptionKey);
                LoggerUtils.logD(TAG, "Data updated.");
            }
        }).start();

        Collections.sort(DataProviderService.getInstance().allDecryptedLoginModels, new SummaryComparator());
        DataProviderService.getInstance().removeFilter();
    }

    private void reEncryptLoginModel(final LoginModel loginModel, byte[] encryptionKey) {
        loginModel.setInitializationVector(aesEncryptionService.getRandomVi());
        LoginModel encryptedLoginModel = new LoginModel(encryptLoginModel(loginModel, encryptionKey));
        encryptedLoginModel.save();
    }

    public void deleteLogInFromDB(LoginModel loginModel) {
        try {
            final Long idToRemoveFromDB = loginModel.getId();
            List<LoginModel> allLoginModels = DataProviderService.getInstance().allDecryptedLoginModels;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    LoginModel dBloginModel = LoginModel.findById(LoginModel.class, idToRemoveFromDB);
                    if (dBloginModel != null) {
                        dBloginModel.delete();
                        LoggerUtils.logD(TAG, "Data deleted.");
                    }
                }
            }).start();

            List<LoginModel> loginModels = DataProviderService.getInstance().allDecryptedLoginModels;

            HashMap<Long, Integer> indexMap = new HashMap<>();
            for (int i = 0; i < loginModels.size(); i++) {
                indexMap.put(loginModels.get(i).getId(), i);
            }

            for (LoginModel lm : allLoginModels) {
                if (lm.getId().equals(idToRemoveFromDB)) {
                    int index = indexMap.get(idToRemoveFromDB);
                    loginModels.remove(index);
                    break;
                }
            }

            DataProviderService.getInstance().removeFilter();
        } catch (Exception e) {
            LoggerUtils.logD(TAG, e.getMessage());
        }
    }

    public void save2FAToDB(final TwoFAModel twoFAModel) {
        twoFAModel.setId(DatabaseIdGenerator.generateId());
        DataProviderService.getInstance().allDecryptedTwoFAModels.add(twoFAModel);
        Collections.sort(DataProviderService.getInstance().allDecryptedTwoFAModels, new SummaryComparator());

        new Thread(new Runnable() {
            @Override
            public void run() {
                twoFAModel.setInitializationVector(aesEncryptionService.getRandomVi());
                TwoFAModel encrypt2faModel = new TwoFAModel(encrypt2FAModel(twoFAModel, enDecryptionKey));
                encrypt2faModel.save();
                LoggerUtils.logD(TAG, "Data saved.");
            }
        }).start();
    }

    public void update2FAInDB(final TwoFAModel twoFAModel) {
        if (twoFAModel != null && twoFAModel.getId() != null) {
            List<TwoFAModel> twoFAModels = DataProviderService.getInstance().allDecryptedTwoFAModels;
            if (twoFAModels != null) {
                for (int i = 0; i < twoFAModels.size(); i++) {
                    if (twoFAModels.get(i).getId().equals(twoFAModel.getId())) {
                        twoFAModels.set(i, twoFAModel);
                    }
                }
            }
            Collections.sort(DataProviderService.getInstance().allDecryptedTwoFAModels, new SummaryComparator());
            VaultTwoFAuthFragmentPresenter.resetAdapter();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    reEncrypt2FAModel(twoFAModel, enDecryptionKey);
                    LoggerUtils.logD(TAG, "Data updated.");
                }
            }).start();
        }
    }

    private void reEncrypt2FAModel(final TwoFAModel twoFAModel, final byte[] encryptionKey) {
        twoFAModel.setInitializationVector(aesEncryptionService.getRandomVi());
        TwoFAModel encrypted2FAModel = new TwoFAModel(encrypt2FAModel(twoFAModel, encryptionKey));
        encrypted2FAModel.save();
    }

    public void delete2FAFromDB(TwoFAModel twoFAModel) {
        try {
            final Long idToRemoveFromDB = twoFAModel.getId();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    TwoFAModel twoFAModel1 = TwoFAModel.findById(TwoFAModel.class, idToRemoveFromDB);
                    if (twoFAModel1 != null) {
                        twoFAModel1.delete();
                        LoggerUtils.logD(TAG, "Data deleted.");
                    }
                }
            }).start();

            List<TwoFAModel> twoFAModels = DataProviderService.getInstance().allDecryptedTwoFAModels;

            HashMap<Long, Integer> indexMap = new HashMap<>();
            for (int i = 0; i < twoFAModels.size(); i++) {
                indexMap.put(twoFAModels.get(i).getId(), i);
            }

            for (TwoFAModel lm : twoFAModels) {
                if (lm.getId().equals(idToRemoveFromDB)) {
                    int index = indexMap.get(idToRemoveFromDB);
                    twoFAModels.remove(index);
                    break;
                }
            }
            VaultTwoFAuthFragmentPresenter.resetAdapter();
        } catch (Exception e) {
            LoggerUtils.logD(TAG, e.getMessage());
        }
    }

    List<LoginModel> getLogInModels() {
        List<LoginModel> encryptedLoginModels;
        List<LoginModel> decryptedLoginModels = null;
        try {
            encryptedLoginModels = LoginModel.listAll(LoginModel.class);
            decryptedLoginModels = decryptLoginData(encryptedLoginModels, enDecryptionKey);
            Collections.sort(decryptedLoginModels, new SummaryComparator());
            DataProviderService.isLMDecryptionFinished = true;
        } catch (SQLiteException e) {
            LoggerUtils.logD(TAG, e.getMessage());
        }
        return decryptedLoginModels;
    }

    List<TwoFAModel> getTwoFaModels() {
        List<TwoFAModel> encryptedTwoFAModels;
        List<TwoFAModel> decryptedTwoFAModels = null;
        try {
            encryptedTwoFAModels = TwoFAModel.listAll(TwoFAModel.class);
            decryptedTwoFAModels = decryptTwoFAData(encryptedTwoFAModels, enDecryptionKey);
            Collections.sort(decryptedTwoFAModels, new SummaryComparator());
            DataProviderService.is2FADecryptionFinished = true;
        } catch (SQLiteException e) {
            LoggerUtils.logD(TAG, e.getMessage());
        }
        return decryptedTwoFAModels;
    }

    public LoginModel encryptBackUpLoginModel(LoginModel logInModel, byte[] encryptionKey) {
        LoginModel encryptLoginModel = new LoginModel(logInModel);
        encryptLoginModel.setSummary(aesEncryptionService.encrypt(logInModel.getSummary(), encryptionKey, logInModel.getInitializationVector()));
        encryptLoginModel.setLogIn(aesEncryptionService.encrypt(logInModel.getLogIn(), encryptionKey, logInModel.getInitializationVector()));
        encryptLoginModel.setPassword(aesEncryptionService.encrypt(logInModel.getPassword(), encryptionKey, logInModel.getInitializationVector()));
        encryptLoginModel.setTag(aesEncryptionService.encrypt(logInModel.getTag(), encryptionKey, logInModel.getInitializationVector()));
        return encryptLoginModel;
    }

    public LoginModel decryptBackUpLoginModel(LoginModel logInModel, byte[] encryptionKey) {
        LoginModel decryptLoginModel = new LoginModel(logInModel);
        decryptLoginModel.setSummary(aesEncryptionService.decrypt(logInModel.getSummary(), encryptionKey, logInModel.getInitializationVector()));
        decryptLoginModel.setLogIn(aesEncryptionService.decrypt(logInModel.getLogIn(), encryptionKey, logInModel.getInitializationVector()));
        decryptLoginModel.setPassword(aesEncryptionService.decrypt(logInModel.getPassword(), encryptionKey, logInModel.getInitializationVector()));
        decryptLoginModel.setTag(aesEncryptionService.decrypt(logInModel.getTag(), encryptionKey, logInModel.getInitializationVector()));
        return decryptLoginModel;
    }

    public TwoFAModel encryptBackUp2FAModel(TwoFAModel twoFAModel, byte[] encryptionKey) {
        TwoFAModel encrypted2faModel = new TwoFAModel(twoFAModel);
        encrypted2faModel.setSummary(aesEncryptionService.encrypt(twoFAModel.getSummary(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode1(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode1(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode2(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode2(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode3(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode3(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode4(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode4(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode5(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode5(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode6(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode6(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode7(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode7(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode8(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode8(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode9(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode9(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode10(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode10(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode11(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode11(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode12(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode12(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode13(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode13(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode14(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode14(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode15(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode15(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode16(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode16(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode17(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode17(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode18(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode18(), encryptionKey, twoFAModel.getInitializationVector()));
        return encrypted2faModel;
    }

    public TwoFAModel decryptBackUp2FAModel(TwoFAModel twoFAModel, byte[] encryptionKey) {
        TwoFAModel decrypted2faModel = new TwoFAModel(twoFAModel);
        decrypted2faModel.setSummary(aesEncryptionService.decrypt(twoFAModel.getSummary(), encryptionKey, twoFAModel.getInitializationVector()));
        decrypted2faModel.setRecoveryCode1(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode1(), encryptionKey, twoFAModel.getInitializationVector()));
        decrypted2faModel.setRecoveryCode2(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode2(), encryptionKey, twoFAModel.getInitializationVector()));
        decrypted2faModel.setRecoveryCode3(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode3(), encryptionKey, twoFAModel.getInitializationVector()));
        decrypted2faModel.setRecoveryCode4(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode4(), encryptionKey, twoFAModel.getInitializationVector()));
        decrypted2faModel.setRecoveryCode5(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode5(), encryptionKey, twoFAModel.getInitializationVector()));
        decrypted2faModel.setRecoveryCode6(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode6(), encryptionKey, twoFAModel.getInitializationVector()));
        decrypted2faModel.setRecoveryCode7(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode7(), encryptionKey, twoFAModel.getInitializationVector()));
        decrypted2faModel.setRecoveryCode8(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode8(), encryptionKey, twoFAModel.getInitializationVector()));
        decrypted2faModel.setRecoveryCode9(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode9(), encryptionKey, twoFAModel.getInitializationVector()));
        decrypted2faModel.setRecoveryCode10(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode10(), encryptionKey, twoFAModel.getInitializationVector()));
        decrypted2faModel.setRecoveryCode11(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode11(), encryptionKey, twoFAModel.getInitializationVector()));
        decrypted2faModel.setRecoveryCode12(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode12(), encryptionKey, twoFAModel.getInitializationVector()));
        decrypted2faModel.setRecoveryCode13(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode13(), encryptionKey, twoFAModel.getInitializationVector()));
        decrypted2faModel.setRecoveryCode14(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode14(), encryptionKey, twoFAModel.getInitializationVector()));
        decrypted2faModel.setRecoveryCode15(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode15(), encryptionKey, twoFAModel.getInitializationVector()));
        decrypted2faModel.setRecoveryCode16(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode16(), encryptionKey, twoFAModel.getInitializationVector()));
        decrypted2faModel.setRecoveryCode17(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode17(), encryptionKey, twoFAModel.getInitializationVector()));
        decrypted2faModel.setRecoveryCode18(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode18(), encryptionKey, twoFAModel.getInitializationVector()));
        return decrypted2faModel;
    }

    public void deleteAllData() {
        LoginModel.deleteAll(LoginModel.class);
        TwoFAModel.deleteAll(TwoFAModel.class);
        DataProviderService.getInstance().allDecryptedLoginModels = new ArrayList<>();
        DataProviderService.getInstance().allDecryptedTwoFAModels = new ArrayList<>();
        DataProviderService.getInstance().filteredLoginModels = new ArrayList<>();
        DataProviderService.getInstance().loginTags = new HashSet<>();
        VaultLoginFragmentPresenter.refreshDataSet();
        VaultTwoFAuthFragmentPresenter.resetAdapter();
        if (VaultTwoFAuthFragmentActivity.twoFAuthFragmentActivity != null) {
            VaultTwoFAuthFragmentActivity.twoFAuthFragmentActivity.onResume();
        }
        NotifyUser.notify(ApplicationContextProvider.getContext(), ApplicationContextProvider.getContext().getResources().getString(R.string.msg_allDataDeleted));
    }

    private LoginModel encryptLoginModel(LoginModel loginModel, byte[] encryptionKey) {
        LoginModel encryptLoginModel = new LoginModel(loginModel);
        encryptLoginModel.setSummary(aesEncryptionService.encrypt(loginModel.getSummary(), encryptionKey, loginModel.getInitializationVector()));
        encryptLoginModel.setLogIn(aesEncryptionService.encrypt(loginModel.getLogIn(), encryptionKey, loginModel.getInitializationVector()));
        encryptLoginModel.setPassword(aesEncryptionService.encrypt(loginModel.getPassword(), encryptionKey, loginModel.getInitializationVector()));
        encryptLoginModel.setTag(aesEncryptionService.encrypt(loginModel.getTag(), encryptionKey, loginModel.getInitializationVector()));
        return encryptLoginModel;
    }

    private List<LoginModel> decryptLoginData(List<LoginModel> loginModels, byte[] decryptionKey) {
        for (LoginModel loginModel : loginModels) {
            loginModel.setSummary(aesEncryptionService.decrypt(loginModel.getSummary(), decryptionKey, loginModel.getInitializationVector()));
            loginModel.setLogIn(aesEncryptionService.decrypt(loginModel.getLogIn(), decryptionKey, loginModel.getInitializationVector()));
            loginModel.setPassword(aesEncryptionService.decrypt(loginModel.getPassword(), decryptionKey, loginModel.getInitializationVector()));
            loginModel.setTag(aesEncryptionService.decrypt(loginModel.getTag(), decryptionKey, loginModel.getInitializationVector()));
        }
        return loginModels;
    }

    private TwoFAModel encrypt2FAModel(TwoFAModel twoFAModel, byte[] encryptionKey) {
        TwoFAModel encrypted2faModel = new TwoFAModel(twoFAModel);
        encrypted2faModel.setSummary(aesEncryptionService.encrypt(twoFAModel.getSummary(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode1(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode1(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode2(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode2(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode3(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode3(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode4(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode4(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode5(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode5(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode6(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode6(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode7(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode7(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode8(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode8(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode9(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode9(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode10(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode10(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode11(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode11(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode12(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode12(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode13(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode13(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode14(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode14(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode15(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode15(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode16(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode16(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode17(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode17(), encryptionKey, twoFAModel.getInitializationVector()));
        encrypted2faModel.setRecoveryCode18(aesEncryptionService.encrypt(twoFAModel.getRecoveryCode18(), encryptionKey, twoFAModel.getInitializationVector()));
        return encrypted2faModel;
    }

    private List<TwoFAModel> decryptTwoFAData(List<TwoFAModel> twoFAModels, byte[] decryptionKey) {
        for (TwoFAModel twoFAModel : twoFAModels) {
            twoFAModel.setSummary(aesEncryptionService.decrypt(twoFAModel.getSummary(), decryptionKey, twoFAModel.getInitializationVector()));
            twoFAModel.setRecoveryCode1(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode1(), decryptionKey, twoFAModel.getInitializationVector()));
            twoFAModel.setRecoveryCode2(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode2(), decryptionKey, twoFAModel.getInitializationVector()));
            twoFAModel.setRecoveryCode3(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode3(), decryptionKey, twoFAModel.getInitializationVector()));
            twoFAModel.setRecoveryCode4(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode4(), decryptionKey, twoFAModel.getInitializationVector()));
            twoFAModel.setRecoveryCode5(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode5(), decryptionKey, twoFAModel.getInitializationVector()));
            twoFAModel.setRecoveryCode6(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode6(), decryptionKey, twoFAModel.getInitializationVector()));
            twoFAModel.setRecoveryCode7(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode7(), decryptionKey, twoFAModel.getInitializationVector()));
            twoFAModel.setRecoveryCode8(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode8(), decryptionKey, twoFAModel.getInitializationVector()));
            twoFAModel.setRecoveryCode9(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode9(), decryptionKey, twoFAModel.getInitializationVector()));
            twoFAModel.setRecoveryCode10(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode10(), decryptionKey, twoFAModel.getInitializationVector()));
            twoFAModel.setRecoveryCode11(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode11(), decryptionKey, twoFAModel.getInitializationVector()));
            twoFAModel.setRecoveryCode12(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode12(), decryptionKey, twoFAModel.getInitializationVector()));
            twoFAModel.setRecoveryCode13(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode13(), decryptionKey, twoFAModel.getInitializationVector()));
            twoFAModel.setRecoveryCode14(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode14(), decryptionKey, twoFAModel.getInitializationVector()));
            twoFAModel.setRecoveryCode15(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode15(), decryptionKey, twoFAModel.getInitializationVector()));
            twoFAModel.setRecoveryCode16(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode16(), decryptionKey, twoFAModel.getInitializationVector()));
            twoFAModel.setRecoveryCode17(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode17(), decryptionKey, twoFAModel.getInitializationVector()));
            twoFAModel.setRecoveryCode18(aesEncryptionService.decrypt(twoFAModel.getRecoveryCode18(), decryptionKey, twoFAModel.getInitializationVector()));
        }
        return twoFAModels;
    }
}
