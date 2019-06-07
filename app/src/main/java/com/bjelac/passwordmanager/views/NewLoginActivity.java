package com.bjelac.passwordmanager.views;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.bjelac.passwordmanager.R;
import com.bjelac.passwordmanager.database.DataProviderService;
import com.bjelac.passwordmanager.database.DatabaseService;
import com.bjelac.passwordmanager.models.LoginModel;
import com.bjelac.passwordmanager.utils.DatabaseIdGenerator;
import com.bjelac.passwordmanager.utils.NotifyUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NewLoginActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private List<String> tags;
    private EditText txtSummary;
    private EditText txtLogIn;
    private EditText txtPassword;
    private Spinner tagSpinner;
    private Button btnGeneratePassword;
    private Button btnSave;
    private Button btnAddNewTag;
    private Button btnDelNewTag;
    private Button btnShowPassword;

    private DatabaseService dbService;
    LoginModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_login);
        init();

        Intent vaultActivityIntent = this.getIntent();
        model = (LoginModel) vaultActivityIntent.getSerializableExtra("model");
        final boolean isUpdate = model != null;

        // if true user will update
        if (model != null) {
            model.setId(vaultActivityIntent.getLongExtra("id", 0)); //fix sugar orm bug (id becomes null at serialization)
            txtSummary.setText(model.getSummary());
            txtLogIn.setText(model.getLogIn());
            txtPassword.setText(model.getPassword());
            for (int i = 0; i < tags.size(); i++) {
                String tag = tags.get(i);
                if (tag != null && tag.equals(model.getTag())) {
                    tagSpinner.setSelection(i);
                    break;
                }
            }
        }

        if (tags.isEmpty()) {
            btnDelNewTag.setEnabled(false);
        }

        btnAddNewTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View addTagPopupView = getLayoutInflater().inflate(R.layout.add_tag_popup, null);
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(addTagPopupView.getContext());
                dialogBuilder.setView(addTagPopupView);
                dialogBuilder.setTitle(getResources().getString(R.string.lbl_addTag));
                dialogBuilder.setCancelable(true);
                final AlertDialog ad = dialogBuilder.show();

                final EditText txtNewTag = addTagPopupView.findViewById(R.id.txtAddTag);
                final Button btnAdd = addTagPopupView.findViewById(R.id.btnAdd);

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newTag = txtNewTag.getText().toString().trim();
                        if (!newTag.equals("")) {
                            tags.add(newTag);
                            DataProviderService.getInstance().loginTags.add(newTag);
                            adapter.notifyDataSetChanged();
                            tagSpinner.setSelection(tags.size());
                            ad.dismiss();
                            btnDelNewTag.setEnabled(true);
                        }
                    }
                });
            }
        });

        btnDelNewTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!DataProviderService.getInstance().loginTags.isEmpty()) {
                    String tag = tagSpinner.getSelectedItem().toString();
                    if (!tag.equals(getResources().getString(R.string.lbl_defaultTag)) && tags != null) {
                        tags.remove(tag);
                        DataProviderService.getInstance().loginTags.remove(tag);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    btnDelNewTag.setEnabled(false);
                }
            }
        });

        btnShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtPassword.getTransformationMethod() == null) {
                    txtPassword.setTransformationMethod(new PasswordTransformationMethod());
                } else {
                    txtPassword.setTransformationMethod(null);
                }
            }
        });

        btnGeneratePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ALLOWED_CHARACTERS = "0123456789qwA!erBtyCuiDop%EasFdfGghH?jkLMzOxcPvbRnmS$WQ";
                final int sizeOfPasswordString = 15;
                final Random random = new Random();
                final StringBuilder sb = new StringBuilder(sizeOfPasswordString);
                for (int i = 0; i < sizeOfPasswordString; ++i) {
                    sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
                }
                txtPassword.setText(sb.toString());
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUpdate) {
                    LoginModel loginModel = new LoginModel(txtSummary.getText().toString().trim(),
                            txtLogIn.getText().toString().trim(),
                            txtPassword.getText().toString().trim(),
                            tagSpinner.getSelectedItem() != null ? tagSpinner.getSelectedItem().toString().trim() : null);
                    loginModel.setId(model.getId());

                    dbService.updateLoginInDB(loginModel);
                    finish();
                } else {
                    if (!txtLogIn.getText().toString().trim().isEmpty() && !txtSummary.getText().toString().trim().isEmpty()) {
                        LoginModel loginModel = new LoginModel(txtSummary.getText().toString().trim(),
                                txtLogIn.getText().toString().trim(),
                                txtPassword.getText().toString().trim(),
                                tagSpinner.getSelectedItem() != null ? tagSpinner.getSelectedItem().toString().trim() : null);
                        loginModel.setId(DatabaseIdGenerator.generateId());

                        dbService.saveLoginToDB(loginModel);
                        finish();
                    } else {
                        if (txtLogIn.getText().toString().trim().isEmpty() && txtSummary.getText().toString().trim().isEmpty()) {
                            NotifyUser.notify(getBaseContext(), getResources().getString(R.string.msg_summaryFieldRequired) + " " + getResources().getString(R.string.msg_userNameFieldRequired));
                        } else if (txtSummary.getText().toString().trim().isEmpty()) {
                            NotifyUser.notify(getBaseContext(), getResources().getString(R.string.msg_summaryFieldRequired));
                        } else
                            NotifyUser.notify(getBaseContext(), getResources().getString(R.string.msg_userNameFieldRequired));
                    }
                }
            }
        });
    }

    private void init() {
        txtSummary = findViewById(R.id.txtSummary);
        txtLogIn = findViewById(R.id.txtLogInID);
        txtPassword = findViewById(R.id.txtLogPassword);
        tagSpinner = findViewById(R.id.tagSpinner);
        btnGeneratePassword = findViewById(R.id.btnGeneratePassword);
        btnSave = findViewById(R.id.btnSave);
        btnAddNewTag = findViewById(R.id.btnAdd);
        btnDelNewTag = findViewById(R.id.btnDel);
        btnShowPassword = findViewById(R.id.btnShow);
        tags = new ArrayList<>(DataProviderService.getInstance().loginTags);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tags);
        tagSpinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dbService = new DatabaseService();
    }
}
