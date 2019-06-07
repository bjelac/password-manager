package com.bjelac.passwordmanager.presenter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bjelac.passwordmanager.R;
import com.bjelac.passwordmanager.adapters.TwoFARecyclerAdapter;

public class VaultTwoFAuthFragmentPresenter {
    private static VaultTwoFAuthFragmentPresenter singleInstance = null;
    private static RecyclerView.Adapter recyclerViewAdapter;
    private static View tfaView;

    public static VaultTwoFAuthFragmentPresenter getInstance(View view) {
        tfaView = view;
        if (singleInstance == null) singleInstance = new VaultTwoFAuthFragmentPresenter();
        setRecycleViewAdapter();
        return singleInstance;
    }

    public static void refreshDataSet() {
        recyclerViewAdapter.notifyDataSetChanged();
    }

    public static void resetAdapter() {
        setRecycleViewAdapter();
    }

    private static void setRecycleViewAdapter() {
        if (tfaView != null) {
            recyclerViewAdapter = new TwoFARecyclerAdapter(tfaView.getContext());
            RecyclerView recyclerView = tfaView.findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(tfaView.getContext()));
            recyclerView.setAdapter(recyclerViewAdapter);
        }
    }
}
