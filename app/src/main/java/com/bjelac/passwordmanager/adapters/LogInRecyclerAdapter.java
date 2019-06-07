package com.bjelac.passwordmanager.adapters;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bjelac.passwordmanager.R;
import com.bjelac.passwordmanager.database.DataProviderService;
import com.bjelac.passwordmanager.database.DatabaseService;
import com.bjelac.passwordmanager.models.LoginModel;
import com.bjelac.passwordmanager.presenter.VaultLoginFragmentPresenter;
import com.bjelac.passwordmanager.utils.ActivityStarter;
import com.bjelac.passwordmanager.utils.ApplicationContextProvider;
import com.bjelac.passwordmanager.utils.NotifyUser;
import com.bjelac.passwordmanager.views.VaultLoginFragmentActivity;

public class LogInRecyclerAdapter extends RecyclerView.Adapter<LogInRecyclerAdapter.ViewHolder> {
    private Context context;
    private DatabaseService dbService;

    public LogInRecyclerAdapter(Context context) {
        this.context = context;

        dbService = new DatabaseService();
    }

    @NonNull
    @Override
    public LogInRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_login_recycler, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LogInRecyclerAdapter.ViewHolder viewHolder, int index) {
        final LoginModel loginModel = DataProviderService.getInstance().filteredLoginModels.get(index);
        viewHolder.summary.setText(loginModel.getSummary());
        viewHolder.logIn.setText(loginModel.getLogIn());
        viewHolder.password.setText(loginModel.getPassword());
        viewHolder.tag.setText(loginModel.getTag().equals(ApplicationContextProvider.getContext().getResources().getString(R.string.lbl_defaultTag)) ? "" : loginModel.getTag());

        viewHolder.logIn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                NotifyUser.notify(v.getContext(), v.getResources().getString(R.string.msg_usernameCopiedToClipboard));
                ClipboardManager clipboard = (ClipboardManager) ApplicationContextProvider.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("logIn", viewHolder.logIn.getText().toString());
                clipboard.setPrimaryClip(clip);
                return false;
            }
        });

        viewHolder.password.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                NotifyUser.notify(v.getContext(), v.getResources().getString(R.string.msg_passwordCopiedToClipboard));
                ClipboardManager clipboard = (ClipboardManager) ApplicationContextProvider.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("password", viewHolder.password.getText().toString());
                clipboard.setPrimaryClip(clip);
                return false;
            }
        });

        viewHolder.btnShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.password.getTransformationMethod() == null) {
                    viewHolder.password.setTransformationMethod(new PasswordTransformationMethod());
                } else {
                    viewHolder.password.setTransformationMethod(null);
                }
            }
        });

        viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityStarter.showAddNewLogInPopUp(context, loginModel);
                VaultLoginFragmentPresenter.refreshDataSet();
            }
        });

        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View confirmDeletePopupView = View.inflate(context, R.layout.confirm_delete_popup, null);
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
                dialogBuilder.setView(confirmDeletePopupView);
                if (loginModel.getSummary() != null && loginModel.getSummary().equals("")) {
                    dialogBuilder.setTitle(v.getResources().getString(R.string.lbl_confirmDelMessageSummary));
                } else {
                    dialogBuilder.setTitle(v.getResources().getString(R.string.lbl_confirmDelTitleSummary, loginModel.getSummary()));
                }
                dialogBuilder.setCancelable(true);
                final AlertDialog alertDialog = dialogBuilder.show();

                final Button btnYes = confirmDeletePopupView.findViewById(R.id.btnYes);
                final Button btnNo = confirmDeletePopupView.findViewById(R.id.btnNo);

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        dbService.deleteLogInFromDB(loginModel);
                        VaultLoginFragmentPresenter.refreshDataSet();
                        VaultLoginFragmentActivity.vaultLoginFragmentActivity.onResume();
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
    }


    @Override
    public int getItemCount() {
        return DataProviderService.getInstance().filteredLoginModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView summary;
        TextView logIn;
        TextView password;
        TextView tag;
        Button btnShowPassword;
        Button btnEdit;
        Button btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            summary = itemView.findViewById(R.id.lbl_SummaryData);
            logIn = itemView.findViewById(R.id.lbl_logInData);
            password = itemView.findViewById(R.id.lbl_passwordData);
            tag = itemView.findViewById(R.id.lbl_tagData);
            btnShowPassword = itemView.findViewById(R.id.btn_showPass);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_del);
        }
    }
}