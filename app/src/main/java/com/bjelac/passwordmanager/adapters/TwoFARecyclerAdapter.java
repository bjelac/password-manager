package com.bjelac.passwordmanager.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjelac.passwordmanager.R;
import com.bjelac.passwordmanager.database.DataProviderService;
import com.bjelac.passwordmanager.database.DatabaseService;
import com.bjelac.passwordmanager.models.TwoFAModel;
import com.bjelac.passwordmanager.presenter.VaultTwoFAuthFragmentPresenter;
import com.bjelac.passwordmanager.utils.ActivityStarter;

import java.util.List;
import java.util.Locale;

public class TwoFARecyclerAdapter extends RecyclerView.Adapter<TwoFARecyclerAdapter.ViewHolder> {

    private Context context;
    private List<TwoFAModel> twoFAModels;

    public TwoFARecyclerAdapter(Context context) {
        this.context = context;
        this.twoFAModels = DataProviderService.getInstance().allDecryptedTwoFAModels;
    }

    @NonNull
    @Override
    public TwoFARecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_two_f_auth_recycler, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TwoFARecyclerAdapter.ViewHolder viewHolder, int index) {
        final TwoFAModel twoFAModel = twoFAModels.get(index);
        viewHolder.summary.setText(twoFAModel.getSummary());

        String[] twoFAModelDataValues = {
                twoFAModel.getRecoveryCode1() != null ? twoFAModel.getRecoveryCode1() : "",
                twoFAModel.getRecoveryCode2() != null ? twoFAModel.getRecoveryCode2() : "",
                twoFAModel.getRecoveryCode3() != null ? twoFAModel.getRecoveryCode3() : "",
                twoFAModel.getRecoveryCode4() != null ? twoFAModel.getRecoveryCode4() : "",
                twoFAModel.getRecoveryCode5() != null ? twoFAModel.getRecoveryCode5() : "",
                twoFAModel.getRecoveryCode6() != null ? twoFAModel.getRecoveryCode6() : "",
                twoFAModel.getRecoveryCode7() != null ? twoFAModel.getRecoveryCode7() : "",
                twoFAModel.getRecoveryCode8() != null ? twoFAModel.getRecoveryCode8() : "",
                twoFAModel.getRecoveryCode9() != null ? twoFAModel.getRecoveryCode9() : "",
                twoFAModel.getRecoveryCode10() != null ? twoFAModel.getRecoveryCode10() : "",
                twoFAModel.getRecoveryCode11() != null ? twoFAModel.getRecoveryCode11() : "",
                twoFAModel.getRecoveryCode12() != null ? twoFAModel.getRecoveryCode12() : "",
                twoFAModel.getRecoveryCode13() != null ? twoFAModel.getRecoveryCode13() : "",
                twoFAModel.getRecoveryCode14() != null ? twoFAModel.getRecoveryCode14() : "",
                twoFAModel.getRecoveryCode15() != null ? twoFAModel.getRecoveryCode15() : "",
                twoFAModel.getRecoveryCode16() != null ? twoFAModel.getRecoveryCode16() : "",
                twoFAModel.getRecoveryCode17() != null ? twoFAModel.getRecoveryCode17() : "",
                twoFAModel.getRecoveryCode18() != null ? twoFAModel.getRecoveryCode18() : ""};

        TextView[] textViews = {viewHolder.recoveryCode1, viewHolder.recoveryCode2, viewHolder.recoveryCode3,
                viewHolder.recoveryCode4, viewHolder.recoveryCode5, viewHolder.recoveryCode6,
                viewHolder.recoveryCode7, viewHolder.recoveryCode8, viewHolder.recoveryCode9,
                viewHolder.recoveryCode10, viewHolder.recoveryCode11, viewHolder.recoveryCode12,
                viewHolder.recoveryCode13, viewHolder.recoveryCode14, viewHolder.recoveryCode15,
                viewHolder.recoveryCode16, viewHolder.recoveryCode17, viewHolder.recoveryCode18};

        LinearLayout[] rows = {viewHolder.row1, viewHolder.row2, viewHolder.row3, viewHolder.row4, viewHolder.row5, viewHolder.row6};

        //sets text in every text view
        for (int i = 0; i < textViews.length; i++) {
            String text = twoFAModelDataValues[i];
            if (!text.isEmpty()) {
                textViews[i].setText(String.format(Locale.ENGLISH, "%d. %s", i + 1, text));
            } else {
                textViews[i].setText("");
            }
        }

        //hides unused rows
        int rowIndex;
        for (int i = 0; i < rows.length; i++) {
            rowIndex = i * 3;
            if ((twoFAModelDataValues[rowIndex].isEmpty())
                    && twoFAModelDataValues[rowIndex + 1].isEmpty()
                    && twoFAModelDataValues[rowIndex + 2].isEmpty()) {
                viewHolder.rowsParent.removeView(rows[i]);
            }
        }

        viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityStarter.showAddNew2FAPopUp(context, twoFAModel);
                VaultTwoFAuthFragmentPresenter.refreshDataSet();
            }
        });

        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View confirmDeletePopupView = View.inflate(context, R.layout.confirm_delete_popup, null);
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
                dialogBuilder.setView(confirmDeletePopupView);
                if (twoFAModel.getSummary() != null && twoFAModel.getSummary().equals("")) {
                    dialogBuilder.setTitle(v.getResources().getString(R.string.lbl_confirmDelMessageSummary));
                } else {
                    dialogBuilder.setTitle(v.getResources().getString(R.string.lbl_confirmDelTitleSummary, twoFAModel.getSummary()));
                }
                dialogBuilder.setCancelable(true);
                final AlertDialog alertDialog = dialogBuilder.show();

                final Button btnYes = confirmDeletePopupView.findViewById(R.id.btnYes);
                final Button btnNo = confirmDeletePopupView.findViewById(R.id.btnNo);

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        DatabaseService databaseService = new DatabaseService();
                        databaseService.delete2FAFromDB(twoFAModel);
                        VaultTwoFAuthFragmentPresenter.refreshDataSet();
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
        return twoFAModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView summary;
        TextView recoveryCode1;
        TextView recoveryCode2;
        TextView recoveryCode3;
        TextView recoveryCode4;
        TextView recoveryCode5;
        TextView recoveryCode6;
        TextView recoveryCode7;
        TextView recoveryCode8;
        TextView recoveryCode9;
        TextView recoveryCode10;
        TextView recoveryCode11;
        TextView recoveryCode12;
        TextView recoveryCode13;
        TextView recoveryCode14;
        TextView recoveryCode15;
        TextView recoveryCode16;
        TextView recoveryCode17;
        TextView recoveryCode18;

        LinearLayout rowsParent;
        LinearLayout row1;
        LinearLayout row2;
        LinearLayout row3;
        LinearLayout row4;
        LinearLayout row5;
        LinearLayout row6;

        Button btnEdit;
        Button btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            summary = itemView.findViewById(R.id.lbl_summary);
            recoveryCode1 = itemView.findViewById(R.id.lbl_recoveryCode1Data);
            recoveryCode2 = itemView.findViewById(R.id.lbl_recoveryCode2Data);
            recoveryCode3 = itemView.findViewById(R.id.lbl_recoveryCode3Data);
            recoveryCode4 = itemView.findViewById(R.id.lbl_recoveryCode4Data);
            recoveryCode5 = itemView.findViewById(R.id.lbl_recoveryCode5Data);
            recoveryCode6 = itemView.findViewById(R.id.lbl_recoveryCode6Data);
            recoveryCode7 = itemView.findViewById(R.id.lbl_recoveryCode7Data);
            recoveryCode8 = itemView.findViewById(R.id.lbl_recoveryCode8Data);
            recoveryCode9 = itemView.findViewById(R.id.lbl_recoveryCode9Data);
            recoveryCode10 = itemView.findViewById(R.id.lbl_recoveryCode10Data);
            recoveryCode11 = itemView.findViewById(R.id.lbl_recoveryCode11Data);
            recoveryCode12 = itemView.findViewById(R.id.lbl_recoveryCode12Data);
            recoveryCode13 = itemView.findViewById(R.id.lbl_recoveryCode13Data);
            recoveryCode14 = itemView.findViewById(R.id.lbl_recoveryCode14Data);
            recoveryCode15 = itemView.findViewById(R.id.lbl_recoveryCode15Data);
            recoveryCode16 = itemView.findViewById(R.id.lbl_recoveryCode16Data);
            recoveryCode17 = itemView.findViewById(R.id.lbl_recoveryCode17Data);
            recoveryCode18 = itemView.findViewById(R.id.lbl_recoveryCode18Data);

            rowsParent = itemView.findViewById(R.id.rowsParent);
            row1 = itemView.findViewById(R.id.row1);
            row2 = itemView.findViewById(R.id.row2);
            row3 = itemView.findViewById(R.id.row3);
            row4 = itemView.findViewById(R.id.row4);
            row5 = itemView.findViewById(R.id.row5);
            row6 = itemView.findViewById(R.id.row6);

            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_del);
        }
    }
}