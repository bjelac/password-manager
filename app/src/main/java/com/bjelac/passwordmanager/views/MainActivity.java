package com.bjelac.passwordmanager.views;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bjelac.passwordmanager.R;
import com.bjelac.passwordmanager.database.DataProviderService;
import com.bjelac.passwordmanager.presenter.IMainPresenter;
import com.bjelac.passwordmanager.presenter.MainPresenter;
import com.bjelac.passwordmanager.utils.ActivityStarter;
import com.bjelac.passwordmanager.utils.LoggerUtils;

public class MainActivity extends AppCompatActivity implements IMainActivity {

    private static final String TAG = MainActivity.class.getName();

    private IMainPresenter mainPresenter;
    private TextView lblSignIn;
    private EditText txtPassword1;
    private EditText txtPassword2;
    private Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainPresenter.authenticateUserWithPassword(txtPassword1.getText().toString(), txtPassword2.getText().toString());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainPresenter.authenticateUserWithBiometric();
        mainPresenter.updateMasterPasswordHash();
    }

    @Override
    public void showSetPasswordControls(boolean bool) {
        int visible;
        if (bool) {
            visible = View.VISIBLE;
        } else {
            visible = View.INVISIBLE;
        }
        lblSignIn.setVisibility(visible);
        lblSignIn.setText(R.string.lbl_setMasterPassword);
        btnSignIn.setText(R.string.btn_signUp);
        txtPassword1.setText("");
        txtPassword1.setVisibility(visible);
        txtPassword2.setText("");
        txtPassword2.setVisibility(visible);
        btnSignIn.setVisibility(visible);
    }

    @Override
    public void showSignInControls() {
        lblSignIn.setText(R.string.lbl_enterMasterPassword);
        btnSignIn.setText(R.string.btn_logIn);
        lblSignIn.setVisibility(View.VISIBLE);
        txtPassword1.setText("");
        txtPassword1.setVisibility(View.VISIBLE);
        txtPassword2.setText("");
        txtPassword2.setVisibility(View.INVISIBLE);
        btnSignIn.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEnableBiometricInstructions() {
        showSetPasswordControls(false);
    }

    @Override
    public void startVaultActivity() {
        final ProgressDialog nDialog;
        nDialog = new ProgressDialog(this);
        nDialog.setMessage(getResources().getString(R.string.lbl_decryptingDB));
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(false);
        nDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    do {
                        Thread.sleep(1000);
                    }
                    while (!DataProviderService.isLMDecryptionFinished && !DataProviderService.is2FADecryptionFinished);
                    nDialog.dismiss();
                    startNextActivity();
                    finish();
                } catch (InterruptedException e) {
                    LoggerUtils.logD(TAG, e.getMessage());
                }
            }
        }).start();
    }

    private void startNextActivity() {
        ActivityStarter.showVaultTabActivity(this);
    }

    private void init() {
        lblSignIn = findViewById(R.id.lblSignIn);
        txtPassword1 = findViewById(R.id.txtPassword1);
        txtPassword2 = findViewById(R.id.txtPassword2);
        btnSignIn = findViewById(R.id.btnSignIn);
        mainPresenter = new MainPresenter(this);
        showSetPasswordControls(false);
    }

    public void resetTxtPassword1Text() {
        this.txtPassword1.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}