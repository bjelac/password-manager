package com.bjelac.passwordmanager.views;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bjelac.passwordmanager.R;
import com.bjelac.passwordmanager.database.DataProviderService;
import com.bjelac.passwordmanager.presenter.VaultTwoFAuthFragmentPresenter;
import com.bjelac.passwordmanager.utils.ActivityStarter;

/**
 * A simple {@link Fragment} subclass.
 */
public class VaultTwoFAuthFragmentActivity extends Fragment {

    public static VaultTwoFAuthFragmentActivity twoFAuthFragmentActivity;
    private FloatingActionButton fab;

    public VaultTwoFAuthFragmentActivity() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        twoFAuthFragmentActivity = this;
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vualt_two_fauth, container, false);

        VaultTwoFAuthFragmentPresenter.getInstance(view);

        fab = view.findViewById(R.id.fab);

        if (DataProviderService.getInstance().allDecryptedTwoFAModels.size() > 0) {
            moveAddFirstItemButtonToCorner(fab);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               ActivityStarter.showAddNew2FAPopUp(getContext());
            }
        });

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        VaultTwoFAuthFragmentPresenter.refreshDataSet();
        if (DataProviderService.getInstance().allDecryptedTwoFAModels.size() > 0) {
            moveAddFirstItemButtonToCorner(fab);
        } else {
            moveAddFirstItemButtonToMiddle(fab);
        }
    }
    private void moveAddFirstItemButtonToCorner(FloatingActionButton fab) {
        ((FrameLayout.LayoutParams) fab.getLayoutParams()).gravity = Gravity.END;
        ((FrameLayout.LayoutParams) fab.getLayoutParams()).gravity = Gravity.BOTTOM;
    }
    private void moveAddFirstItemButtonToMiddle(FloatingActionButton fab) {
        ((FrameLayout.LayoutParams) fab.getLayoutParams()).gravity = Gravity.CENTER;
    }
}
