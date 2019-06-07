package com.bjelac.passwordmanager.models;

import com.orm.SugarRecord;

import java.io.Serializable;

public class TwoFAModel extends SugarRecord implements Serializable, IDataModel {

    private String summary;
    private String recoveryCode1;
    private String recoveryCode2;
    private String recoveryCode3;
    private String recoveryCode4;
    private String recoveryCode5;
    private String recoveryCode6;
    private String recoveryCode7;
    private String recoveryCode8;
    private String recoveryCode9;
    private String recoveryCode10;
    private String recoveryCode11;
    private String recoveryCode12;
    private String recoveryCode13;
    private String recoveryCode14;
    private String recoveryCode15;
    private String recoveryCode16;
    private String recoveryCode17;
    private String recoveryCode18;
    private byte[] initializationVector;

    public TwoFAModel() {
        // Empty constructor needed
    }

    public TwoFAModel(TwoFAModel twoFAModel) {
        this.setId(twoFAModel.getId());
        this.summary = twoFAModel.getSummary();
        this.recoveryCode1 = twoFAModel.getRecoveryCode1();
        this.recoveryCode2 = twoFAModel.getRecoveryCode2();
        this.recoveryCode3 = twoFAModel.getRecoveryCode3();
        this.recoveryCode4 = twoFAModel.getRecoveryCode4();
        this.recoveryCode5 = twoFAModel.getRecoveryCode5();
        this.recoveryCode6 = twoFAModel.getRecoveryCode6();
        this.recoveryCode7 = twoFAModel.getRecoveryCode7();
        this.recoveryCode8 = twoFAModel.getRecoveryCode8();
        this.recoveryCode9 = twoFAModel.getRecoveryCode9();
        this.recoveryCode10 = twoFAModel.getRecoveryCode10();
        this.recoveryCode11 = twoFAModel.getRecoveryCode11();
        this.recoveryCode12 = twoFAModel.getRecoveryCode12();
        this.recoveryCode13 = twoFAModel.getRecoveryCode13();
        this.recoveryCode14 = twoFAModel.getRecoveryCode14();
        this.recoveryCode15 = twoFAModel.getRecoveryCode15();
        this.recoveryCode16 = twoFAModel.getRecoveryCode16();
        this.recoveryCode17 = twoFAModel.getRecoveryCode17();
        this.recoveryCode18 = twoFAModel.getRecoveryCode18();
        this.initializationVector = twoFAModel.getInitializationVector();
    }

    @Override
    public String getSummary() {
        return summary;
    }

    @Override
    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setRecoveryCode1(String recoveryCode1) {
        this.recoveryCode1 = recoveryCode1;
    }

    public void setRecoveryCode2(String recoveryCode2) {
        this.recoveryCode2 = recoveryCode2;
    }

    public void setRecoveryCode3(String recoveryCode3) {
        this.recoveryCode3 = recoveryCode3;
    }

    public void setRecoveryCode4(String recoveryCode4) {
        this.recoveryCode4 = recoveryCode4;
    }

    public void setRecoveryCode5(String recoveryCode5) {
        this.recoveryCode5 = recoveryCode5;
    }

    public void setRecoveryCode6(String recoveryCode6) {
        this.recoveryCode6 = recoveryCode6;
    }

    public void setRecoveryCode7(String recoveryCode7) {
        this.recoveryCode7 = recoveryCode7;
    }

    public void setRecoveryCode8(String recoveryCode8) {
        this.recoveryCode8 = recoveryCode8;
    }

    public void setRecoveryCode9(String recoveryCode9) {
        this.recoveryCode9 = recoveryCode9;
    }

    public void setRecoveryCode10(String recoveryCode10) {
        this.recoveryCode10 = recoveryCode10;
    }

    public void setRecoveryCode11(String recoveryCode11) {
        this.recoveryCode11 = recoveryCode11;
    }

    public void setRecoveryCode12(String recoveryCode12) {
        this.recoveryCode12 = recoveryCode12;
    }

    public void setRecoveryCode13(String recoveryCode13) {
        this.recoveryCode13 = recoveryCode13;
    }

    public void setRecoveryCode14(String recoveryCode14) {
        this.recoveryCode14 = recoveryCode14;
    }

    public void setRecoveryCode15(String recoveryCode15) {
        this.recoveryCode15 = recoveryCode15;
    }

    public void setRecoveryCode16(String recoveryCode16) {
        this.recoveryCode16 = recoveryCode16;
    }

    public void setRecoveryCode17(String recoveryCode17) {
        this.recoveryCode17 = recoveryCode17;
    }

    public void setRecoveryCode18(String recoveryCode18) {
        this.recoveryCode18 = recoveryCode18;
    }

    public String getRecoveryCode1() {
        return recoveryCode1;
    }

    public String getRecoveryCode2() {
        return recoveryCode2;
    }

    public String getRecoveryCode3() {
        return recoveryCode3;
    }

    public String getRecoveryCode4() {
        return recoveryCode4;
    }

    public String getRecoveryCode5() {
        return recoveryCode5;
    }

    public String getRecoveryCode6() {
        return recoveryCode6;
    }

    public String getRecoveryCode7() {
        return recoveryCode7;
    }

    public String getRecoveryCode8() {
        return recoveryCode8;
    }

    public String getRecoveryCode9() {
        return recoveryCode9;
    }

    public String getRecoveryCode10() {
        return recoveryCode10;
    }

    public String getRecoveryCode11() {
        return recoveryCode11;
    }

    public String getRecoveryCode12() {
        return recoveryCode12;
    }

    public String getRecoveryCode13() {
        return recoveryCode13;
    }

    public String getRecoveryCode14() {
        return recoveryCode14;
    }

    public String getRecoveryCode15() {
        return recoveryCode15;
    }

    public String getRecoveryCode16() {
        return recoveryCode16;
    }

    public String getRecoveryCode17() {
        return recoveryCode17;
    }

    public String getRecoveryCode18() {
        return recoveryCode18;
    }

    public byte[] getInitializationVector() {
        return initializationVector;
    }

    public void setInitializationVector(byte[] initializationVector) {
        this.initializationVector = initializationVector;
    }
}