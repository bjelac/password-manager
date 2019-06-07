package com.bjelac.passwordmanager.models;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

import java.io.Serializable;

@Table
public class LoginModel extends SugarRecord implements Serializable, IDataModel {
    private String summary;
    private String logIn;
    private String password;
    private String tag;
    private byte[] initializationVector;

    public LoginModel() {
        // Empty constructor needed
    }

    public LoginModel(String summary, String logIn, String password, String tag) {
        this.summary = summary;
        this.logIn = logIn;
        this.password = password;
        this.tag = tag;
    }

    public LoginModel(LoginModel loginModel) {
        this.setId(loginModel.getId());
        this.summary = loginModel.getSummary();
        this.logIn = loginModel.getLogIn();
        this.password = loginModel.getPassword();
        this.tag = loginModel.getTag();
        this.initializationVector = loginModel.getInitializationVector();
    }

    @Override
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLogIn() {
        return logIn;
    }

    public void setLogIn(String logIn) {
        this.logIn = logIn;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public byte[] getInitializationVector() {
        return initializationVector;
    }

    public void setInitializationVector(byte[] initializationVector) {
        this.initializationVector = initializationVector;
    }
}
