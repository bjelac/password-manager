package com.bjelac.passwordmanager.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bjelac.passwordmanager.R;
import com.bjelac.passwordmanager.database.DatabaseService;
import com.bjelac.passwordmanager.models.TwoFAModel;
import com.bjelac.passwordmanager.utils.NotifyUser;

public class New2faActivity extends AppCompatActivity {

    TwoFAModel twoFAModel;

    private EditText txtSummary;
    private EditText txtRecoveryCode1;
    private EditText txtRecoveryCode2;
    private EditText txtRecoveryCode3;
    private EditText txtRecoveryCode4;
    private EditText txtRecoveryCode5;
    private EditText txtRecoveryCode6;
    private EditText txtRecoveryCode7;
    private EditText txtRecoveryCode8;
    private EditText txtRecoveryCode9;
    private EditText txtRecoveryCode10;
    private EditText txtRecoveryCode11;
    private EditText txtRecoveryCode12;
    private EditText txtRecoveryCode13;
    private EditText txtRecoveryCode14;
    private EditText txtRecoveryCode15;
    private EditText txtRecoveryCode16;
    private EditText txtRecoveryCode17;
    private EditText txtRecoveryCode18;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new2fa);
        init();

        Intent vaultActivityIntent = this.getIntent();
        twoFAModel = (TwoFAModel) vaultActivityIntent.getSerializableExtra("model");

        final boolean isUpdate = twoFAModel != null;

        if (isUpdate) {
            twoFAModel.setId(vaultActivityIntent.getLongExtra("id", 0));
            updateTextViews(vaultActivityIntent);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseService databaseService = new DatabaseService();
                if (!txtSummary.getText().toString().trim().isEmpty()
                        && !txtRecoveryCode1.getText().toString().trim().isEmpty()
                        && !txtRecoveryCode2.getText().toString().trim().isEmpty()
                        && !txtRecoveryCode3.getText().toString().trim().isEmpty()
                        && !txtRecoveryCode4.getText().toString().trim().isEmpty()
                        && !txtRecoveryCode5.getText().toString().trim().isEmpty()
                        && !txtRecoveryCode6.getText().toString().trim().isEmpty()) {
                    if (isUpdate) {
                        setUpModel();
                        databaseService.update2FAInDB(twoFAModel);
                        finish();
                    } else {
                        twoFAModel = new TwoFAModel();
                        setUpModel();
                        databaseService.save2FAToDB(twoFAModel);
                        finish();
                    }

                } else {
                    NotifyUser.notify(v.getContext(), getResources().getString(R.string.msg_summaryFieldRequired));
                }
            }
        });
    }

    private void setUpModel() {
        twoFAModel.setSummary(txtSummary.getText().toString().trim());
        twoFAModel.setRecoveryCode1(txtRecoveryCode1.getText().toString().trim());
        twoFAModel.setRecoveryCode2(txtRecoveryCode2.getText().toString().trim());
        twoFAModel.setRecoveryCode3(txtRecoveryCode3.getText().toString().trim());
        twoFAModel.setRecoveryCode4(txtRecoveryCode4.getText().toString().trim());
        twoFAModel.setRecoveryCode5(txtRecoveryCode5.getText().toString().trim());
        twoFAModel.setRecoveryCode6(txtRecoveryCode6.getText().toString().trim());
        twoFAModel.setRecoveryCode7(txtRecoveryCode7.getText().toString().trim());
        twoFAModel.setRecoveryCode8(txtRecoveryCode8.getText().toString().trim());
        twoFAModel.setRecoveryCode9(txtRecoveryCode9.getText().toString().trim());
        twoFAModel.setRecoveryCode10(txtRecoveryCode10.getText().toString().trim());
        twoFAModel.setRecoveryCode11(txtRecoveryCode11.getText().toString().trim());
        twoFAModel.setRecoveryCode12(txtRecoveryCode12.getText().toString().trim());
        twoFAModel.setRecoveryCode13(txtRecoveryCode13.getText().toString().trim());
        twoFAModel.setRecoveryCode14(txtRecoveryCode14.getText().toString().trim());
        twoFAModel.setRecoveryCode15(txtRecoveryCode15.getText().toString().trim());
        twoFAModel.setRecoveryCode16(txtRecoveryCode16.getText().toString().trim());
        twoFAModel.setRecoveryCode17(txtRecoveryCode17.getText().toString().trim());
        twoFAModel.setRecoveryCode18(txtRecoveryCode18.getText().toString().trim());
    }

    private void updateTextViews(Intent vaultActivityIntent) {
        twoFAModel.setId(vaultActivityIntent.getLongExtra("id", 0)); //fix sugar orm bug (id becomes null at serialization)
        txtSummary.setText(twoFAModel.getSummary());
        txtRecoveryCode1.setText(twoFAModel.getRecoveryCode1());
        txtRecoveryCode2.setText(twoFAModel.getRecoveryCode2());
        txtRecoveryCode3.setText(twoFAModel.getRecoveryCode3());
        txtRecoveryCode4.setText(twoFAModel.getRecoveryCode4());
        txtRecoveryCode5.setText(twoFAModel.getRecoveryCode5());
        txtRecoveryCode6.setText(twoFAModel.getRecoveryCode6());
        txtRecoveryCode7.setText(twoFAModel.getRecoveryCode7());
        txtRecoveryCode8.setText(twoFAModel.getRecoveryCode8());
        txtRecoveryCode9.setText(twoFAModel.getRecoveryCode9());
        txtRecoveryCode10.setText(twoFAModel.getRecoveryCode10());
        txtRecoveryCode11.setText(twoFAModel.getRecoveryCode11());
        txtRecoveryCode12.setText(twoFAModel.getRecoveryCode12());
        txtRecoveryCode13.setText(twoFAModel.getRecoveryCode13());
        txtRecoveryCode14.setText(twoFAModel.getRecoveryCode14());
        txtRecoveryCode15.setText(twoFAModel.getRecoveryCode15());
        txtRecoveryCode16.setText(twoFAModel.getRecoveryCode16());
        txtRecoveryCode17.setText(twoFAModel.getRecoveryCode17());
        txtRecoveryCode18.setText(twoFAModel.getRecoveryCode18());
    }

    private void init() {
        txtSummary = findViewById(R.id.txt_summary);
        txtRecoveryCode1 = findViewById(R.id.txt_recoveryCode1);
        txtRecoveryCode2 = findViewById(R.id.txt_recoveryCode2);
        txtRecoveryCode3 = findViewById(R.id.txt_recoveryCode3);
        txtRecoveryCode4 = findViewById(R.id.txt_recoveryCode4);
        txtRecoveryCode5 = findViewById(R.id.txt_recoveryCode5);
        txtRecoveryCode6 = findViewById(R.id.txt_recoveryCode6);
        txtRecoveryCode7 = findViewById(R.id.txt_recoveryCode7);
        txtRecoveryCode8 = findViewById(R.id.txt_recoveryCode8);
        txtRecoveryCode9 = findViewById(R.id.txt_recoveryCode9);
        txtRecoveryCode10 = findViewById(R.id.txt_recoveryCode10);
        txtRecoveryCode11 = findViewById(R.id.txt_recoveryCode11);
        txtRecoveryCode12 = findViewById(R.id.txt_recoveryCode12);
        txtRecoveryCode13 = findViewById(R.id.txt_recoveryCode13);
        txtRecoveryCode14 = findViewById(R.id.txt_recoveryCode14);
        txtRecoveryCode15 = findViewById(R.id.txt_recoveryCode15);
        txtRecoveryCode16 = findViewById(R.id.txt_recoveryCode16);
        txtRecoveryCode17 = findViewById(R.id.txt_recoveryCode17);
        txtRecoveryCode18 = findViewById(R.id.txt_recoveryCode18);
        btnSave = findViewById(R.id.btnSave);
    }
}
