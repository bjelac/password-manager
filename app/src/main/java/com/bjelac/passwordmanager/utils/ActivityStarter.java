package com.bjelac.passwordmanager.utils;

import android.content.Context;
import android.content.Intent;

import com.bjelac.passwordmanager.database.DataProviderService;
import com.bjelac.passwordmanager.models.LoginModel;
import com.bjelac.passwordmanager.models.TwoFAModel;
import com.bjelac.passwordmanager.views.New2faActivity;
import com.bjelac.passwordmanager.views.NewLoginActivity;
import com.bjelac.passwordmanager.views.VaultTabActivity;

public class ActivityStarter {
    public static void showVaultTabActivity(Context context) {
        Intent intent = new Intent(context, VaultTabActivity.class);
        context.startActivity(intent);
        DataProviderService.activityLocked = false;
    }

    public static void showAddNewLogInPopUp(Context context, LoginModel loginModel) {
        Intent intent = new Intent(context, NewLoginActivity.class);
        intent.putExtra("model", loginModel);
        intent.putExtra("id", loginModel.getId());
        context.startActivity(intent);
        DataProviderService.activityLocked = false;
    }

    public static void showAddNewLogInPopUp(Context context) {
        Intent intent = new Intent(context, NewLoginActivity.class);
        context.startActivity(intent);
        DataProviderService.activityLocked = false;
    }

    public static void showAddNew2FAPopUp(Context context) {
        Intent intent = new Intent(context, New2faActivity.class);
        context.startActivity(intent);
        DataProviderService.activityLocked = false;
    }

    public static void showAddNew2FAPopUp(Context context, TwoFAModel twoFAModel) {
        Intent intent = new Intent(context, New2faActivity.class);
        intent.putExtra("model", twoFAModel);
        intent.putExtra("id", twoFAModel.getId());
        context.startActivity(intent);
        DataProviderService.activityLocked = false;
    }
}
