package com.bjelac.passwordmanager.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bjelac.passwordmanager.R;
import com.bjelac.passwordmanager.database.DataProviderService;
import com.bjelac.passwordmanager.database.DatabaseService;
import com.bjelac.passwordmanager.presenter.SettingsFragmentPresenter;
import com.bjelac.passwordmanager.utils.NotifyUser;

public class SettingsFragmentActivity extends Fragment {
    private final String TAG = SettingsFragmentActivity.class.getName();
    private static final int OPEN_REQUEST_CODE = 41;
    private static final int SAVE_REQUEST_CODE = 42;

    private SettingsFragmentPresenter settingsFragmentPresenter;
    private EditText passwordBox1;
    private EditText passwordBox2;

    public SettingsFragmentActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);
        settingsFragmentPresenter = new SettingsFragmentPresenter(view, this.getActivity());
        passwordBox1 = view.findViewById(R.id.txtPassword1);
        passwordBox2 = view.findViewById(R.id.txtPassword2);
        Button btnResetMasterPassword = view.findViewById(R.id.btnResetMasterPassword);
        Button btnBackUpBD = view.findViewById(R.id.btn_backupDB);
        Button btnRestoreBD = view.findViewById(R.id.btn_restoreDB);
        Button btnDeleteDB = view.findViewById(R.id.btn_deleteDB);
        Button btnShowInfo = view.findViewById(R.id.btn_aboutInfo);

        btnResetMasterPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsFragmentPresenter.resetMasterPassword(passwordBox1.getText().toString(), passwordBox2.getText().toString());
            }
        });

        btnBackUpBD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/plain");
                startActivityForResult(intent, SAVE_REQUEST_CODE);
            }
        });

        btnRestoreBD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/plain");
                startActivityForResult(intent, OPEN_REQUEST_CODE);
            }
        });

        btnDeleteDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View confirmDeletePopupView = View.inflate(getContext(), R.layout.confirm_delete_popup, null);
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
                dialogBuilder.setView(confirmDeletePopupView);
                dialogBuilder.setTitle(v.getResources().getString(R.string.lbl_confirmPopupTitle_deleteDB));
                dialogBuilder.setMessage(v.getResources().getString(R.string.lbl_confirmPopupMessage_deleteDB));
                dialogBuilder.setCancelable(true);
                final AlertDialog alertDialog = dialogBuilder.show();

                final Button btnYes = confirmDeletePopupView.findViewById(R.id.btnYes);
                final Button btnNo = confirmDeletePopupView.findViewById(R.id.btnNo);

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        DatabaseService dbService = new DatabaseService();
                        dbService.deleteAllData();
                    }
                });

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
        });

        btnShowInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View infoView = View.inflate(getContext(), R.layout.info_popup, null);
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
                dialogBuilder.setView(infoView);
                dialogBuilder.setTitle(v.getResources().getString(R.string.lbl_infoTitle));
                dialogBuilder.setCancelable(true);
                dialogBuilder.show();

                Button btnReportIssue = infoView.findViewById(R.id.btn_reportIssue);
                Button btnGoToGitHub = infoView.findViewById(R.id.btn_goToGitHub);
                Button btnSupportMe = infoView.findViewById(R.id.btn_supportMe);

                btnReportIssue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://github.com/coolintentions/password-manager/issues"));
                        startActivity(viewIntent);
                    }
                });

                btnGoToGitHub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://github.com/coolintentions/password-manager"));
                        startActivity(viewIntent);
                    }
                });

                btnSupportMe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://ko-fi.com/dusko_"));
                        startActivity(viewIntent);
                    }
                });
            }
        });
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        Uri fileUri;
        DataProviderService.activityLocked = false;

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SAVE_REQUEST_CODE) {
                if (resultData != null) {
                    fileUri = resultData.getData();
                    askUserForKey(fileUri, true);
                }
            } else if (requestCode == OPEN_REQUEST_CODE) {
                if (resultData != null) {
                    fileUri = resultData.getData();
                    askUserForKey(fileUri, false);
                }
            }
        }
    }

    private void askUserForKey(final Uri fileUri, final boolean startBackUp) {
        final View encryptionKeyPopupView = getLayoutInflater().inflate(R.layout.get_encryption_key_popup, null);
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(encryptionKeyPopupView.getContext());
        dialogBuilder.setView(encryptionKeyPopupView);
        dialogBuilder.setCancelable(false);
        final AlertDialog ad = dialogBuilder.show();


        final TextView keyInfo = encryptionKeyPopupView.findViewById(R.id.keyInfo);
        final EditText txtGetSetKey = encryptionKeyPopupView.findViewById(R.id.txtGetKey);
        final EditText txtConfirmKey = encryptionKeyPopupView.findViewById(R.id.txtGetKeyConfirm);
        final Button btnEnDecrypt = encryptionKeyPopupView.findViewById(R.id.btnEnDecrypt);
        final Button btnCancel = encryptionKeyPopupView.findViewById(R.id.btnCancel);

        if (startBackUp) {
            txtGetSetKey.setHint(getResources().getString(R.string.hint_setKey));
            keyInfo.setText(getResources().getString(R.string.lbl_keyInfoBackup));
            btnEnDecrypt.setText(getResources().getText(R.string.lbl_backUpDB));
            txtConfirmKey.setVisibility(View.VISIBLE);
        } else {
            txtConfirmKey.setVisibility(View.GONE);
        }

        btnEnDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = txtGetSetKey.getText().toString().trim();
                String confirmKey = txtConfirmKey.getText().toString().trim();

                if (!key.isEmpty()) {
                    if (startBackUp) {
                        if (key.equals(confirmKey)) {
                            settingsFragmentPresenter.backUpBD(fileUri, key);
                            ad.dismiss();
                        } else {
                            NotifyUser.notify(getContext(), getString(R.string.msg_passwordDoNotMatch));
                        }
                    } else {
                        settingsFragmentPresenter.restoreBD(fileUri, key);
                        ad.dismiss();
                    }
                } else {
                    NotifyUser.notify(getContext(), getString(R.string.msg_passwordCanNotBeEmpty));
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });
    }


}
